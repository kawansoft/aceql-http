/*
 * Copyright (c)2023 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-02-21
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.api.server.firewall;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.StatementAnalyzer;
import org.kawanfw.sql.api.util.firewall.CsvRulesManagerLoader;
import org.kawanfw.sql.api.util.firewall.DatabaseUserTableTriplet;
import org.kawanfw.sql.api.util.firewall.TableAllowStatements;
import org.kawanfw.sql.metadata.AceQLMetaData;
import org.kawanfw.sql.servlet.injection.classes.InjectedClassesStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileStore;
import org.kawanfw.sql.servlet.util.logging.LoggerWrapper;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.SqlTag;
import org.kawanfw.sql.util.TimestampUtil;
import org.slf4j.Logger;

/**
 * Firewall manager that checks each SQL request against the content of a CSV
 * File. The CSV file is loaded in memory at AceQL server startup. <br>
 * <br>
 * The name of the CSV file that will be used by a database is:&nbsp;
 * <code>&lt;database&gt;_rules_manager.csv</code>, where {@code database} is
 * the name of the database declared in the {@code aceql-server.properties} files.<br>
 * The file must be located in the same directory as the
 * {@code aceql-server.properties} file used when starting the AceQL server.<br>
 * <br>
 * The CSV file contains the rules for accessing the tables, with semicolon for
 * separator:
 * <ul>
 * <li>First line contains the element names:
 * <code>username;table;delete;insert;select;update;optional comments</code></li>
 * <li>Subsequent lines contain the rules, with the values for each element:
 * <ul>
 * <li>{@code username}: AceQL username of the connected client.</li>
 * <li>{@code table}: the table name to access. Name must not include dots and
 * prefixes.</li>
 * <li>{@code delete}: {@code true} if the username has the right to delete rows
 * of the table, else {@code false}.</li>
 * <li>{@code insert}: {@code true} if the username has the right to insert rows
 * in the table, else {@code false}.</li>
 * <li>{@code select}: {@code true} if the username has the right to select rows
 * of the table, else {@code false}.</li>
 * <li>{@code update}: {@code true} if the username has the right to update rows
 * of the table, else {@code false}.</li>
 * <li>Optional comments for the rule.</li>
 * </ul>
 * </ul>
 * <br>
 * Note that:
 * <ul>
 * <li>{@code public} value may be used for the {@code username} column and
 * means any username. At execution time: if a rule with {@code public} returns
 * true for a CSV column, the rule supersedes other declared rules declared for
 * specific users for the same CSV column.
 * <li>{@code all} value is allowed for {@code table} column and means any
 * table. At execution time: If a rule with {@code all} returns true for a CSV
 * column, the rule supersedes other specific rules declared for specific tables
 * for the same CSV column.
 * </ul>
 * <br>
 * <b>Note that updating the CSV file will reload the rules.</b> If you prefer
 * to disallow dynamic reloading, use a {@link CsvRulesManagerNoReload}
 * implementation. <br>
 * <br>
 * See an example of CSV file: <a href=
 * "https://docs.aceql.com/rest/soft/12.3/src/sampledb_rules_manager.csv">sampledb_rules_manager.csv</a>
 * <br>
 * <br>
 *
 * @author Nicolas de Pomereu
 * @since 4.1
 */
public class CsvRulesManager implements SqlFirewallManager {

    private static boolean DEBUG = FrameworkDebug.isSet(CsvRulesManager.class);

    private Set<String> databaseSetForReset = new HashSet<>();
    
    /**
     * The map that contains for each database/username the table and their rights
     */
    private Map<DatabaseUserTableTriplet, TableAllowStatements> mapTableAllowStatementsSet = null;

    private FileTime storedFileTime = null;

    /** Default behavior is to allow reload of rules if CSV file is updated */
    protected boolean allowReload = true;

    /**
     * Allows the execution of the statement if an allowing rules exists in
     * the:&nbsp; <code>&lt;database&gt;_rules_manager.csv</code> file.
     */
    @Override
    public boolean allowSqlRunAfterAnalysis(SqlEvent sqlEvent, Connection connection) throws IOException, SQLException {
	// Load all rules if not already done:
	loadRules(sqlEvent.getDatabase(), connection);

	boolean isAllowed = isAllowed(sqlEvent.getUsername(), sqlEvent.getDatabase(), sqlEvent.getSql(),
		sqlEvent.getParameterValues());
	return isAllowed;

    }

	/**
     * @return <code><b>true</b></code>. (Client programs will be allowed to create
     *         raw <code>Statement</code>, i.e. call statements without parameters.)
     */
    @Override
    public boolean allowStatementClass(String username, String database, Connection connection)
	    throws IOException, SQLException {
	return true;
    }


    /**
     * @return <code><b>true</b></code>. (Client programs will be allowed to call
     *         the Metadata Query API).
     */
    @Override
    public boolean allowMetadataQuery(String username, String database, Connection connection)
	    throws IOException, SQLException {
	return true;
    }
    
    /**
     * Will say id there is a rule that allows for the usename the SQL statement.
     *
     * @param username
     * @param database
     * @param sql
     * @param parameterValues
     * @return
     * @throws SQLException
     */
    private boolean isAllowed(String username, String database, String sql, List<Object> parameterValues)
	    throws SQLException {

	StatementAnalyzer analyzer = new StatementAnalyzer(sql, parameterValues);
	List<String> tables = analyzer.getTables();
	String statementName = analyzer.getStatementName();
	statementName = statementName.toLowerCase();

	debug("");
	debug("Testing statement: " + statementName + ":");

	boolean isAllowed = false;

	for (String table : tables) {
	    table = table.toLowerCase();

	    // Test public with all tables
	    DatabaseUserTableTriplet databaseUserTableTriplet = new DatabaseUserTableTriplet(database, "public", "all");
	    TableAllowStatements tableAllowStatements = mapTableAllowStatementsSet.get(databaseUserTableTriplet);

	    debug("public and all -  tableAllowStatements: " + tableAllowStatements + ":");
	    if (tableAllowStatements != null) {
		isAllowed = isAllowed(tableAllowStatements, statementName);
		if (isAllowed) {
		    return true;
		}
	    }

	    // Test public with the passed table
	    databaseUserTableTriplet = new DatabaseUserTableTriplet(database, "public", table);
	    tableAllowStatements = mapTableAllowStatementsSet.get(databaseUserTableTriplet);

	    debug("public -  tableAllowStatements: " + tableAllowStatements + ":");

	    if (tableAllowStatements != null) {
		isAllowed = isAllowed(tableAllowStatements, statementName);
		if (isAllowed) {
		    return true;
		}
	    }

	    // Test username with all tables
	    databaseUserTableTriplet = new DatabaseUserTableTriplet(database, username, "all");
	    tableAllowStatements = mapTableAllowStatementsSet.get(databaseUserTableTriplet);

	    debug("all -  tableAllowStatements: " + tableAllowStatements + ":");

	    if (tableAllowStatements != null) {
		isAllowed = isAllowed(tableAllowStatements, statementName);
		if (isAllowed) {
		    return true;
		}
	    }

	    // Test direct values
	    databaseUserTableTriplet = new DatabaseUserTableTriplet(database, username, table);
	    tableAllowStatements = mapTableAllowStatementsSet.get(databaseUserTableTriplet);

	    debug("tableAllowStatements: " + tableAllowStatements + ":");

	    if (tableAllowStatements != null) {
		isAllowed = isAllowed(tableAllowStatements, statementName);
		if (isAllowed) {
		    return true;
		}
	    }
	}

	// No allowance found: return false
	return false;
    }

    private boolean isAllowed(TableAllowStatements tableAllowStatements, String statementName) {

	if (statementName.equals("delete")) {
	    return tableAllowStatements.isDeleteAllowed();
	} else if (statementName.equals("insert")) {
	    return tableAllowStatements.isInsertAllowe();
	} else if (statementName.equals("select")) {
	    return tableAllowStatements.isSelectAllowed();
	} else if (statementName.equals("update")) {
	    return tableAllowStatements.isUpdateAllowed();
	} else {
	    return false;
	}
    }

    /**
     * @param database
     * @param connection
     * @throws FileNotFoundException
     * @throws SQLException
     * @throws IOException
     */
    private void loadRules(String database, Connection connection)
	    throws FileNotFoundException, SQLException, IOException {

	File csvFile = getCsvFile(database);
	BasicFileAttributes basicFileAttributes = Files.readAttributes(csvFile.toPath(), BasicFileAttributes.class);
	FileTime currentFileTime = basicFileAttributes.lastModifiedTime();

	debug("");
	debug("csvFile        : " + csvFile);
	debug("allowReload    : " + allowReload);
	debug("storedFileTime : " + storedFileTime);
	debug("currentFileTime: " + currentFileTime);

	if (storedFileTime != null && !currentFileTime.equals(storedFileTime) && allowReload) {
	    mapTableAllowStatementsSet = null;
	    databaseSetForReset = new HashSet<>();
	    String logInfo = TimestampUtil.getHumanTimestampNow() + " " + SqlTag.USER_CONFIGURATION
		    + " Reloading CsvRulesManager configuration file: " + csvFile;
	    System.err.println(logInfo);
	    DatabaseConfigurator databaseConfigurator = InjectedClassesStore.get().getDatabaseConfigurators()
			.get(database);
	    Logger logger = databaseConfigurator.getLogger();
	    LoggerWrapper.log(logger, logInfo);
	    storedFileTime = currentFileTime;
	}

	if (mapTableAllowStatementsSet == null || ! databaseSetForReset.contains(database)) {

	    AceQLMetaData aceQLMetaData = new AceQLMetaData(connection);
	    List<String> tables = aceQLMetaData.getTableNames();
	    Set<String> tableSet = new TreeSet<>();
	    // Load in lowercase
	    for (String table : tables) {
		tableSet.add(table.toLowerCase());
	    }
	    tableSet.add("all"); // Add "all" values for all tables
	    CsvRulesManagerLoader csvRulesManagerLoader = new CsvRulesManagerLoader(csvFile, database, tableSet);
	    csvRulesManagerLoader.load();

	    mapTableAllowStatementsSet = csvRulesManagerLoader.getMapTableAllowStatementsSet();
	    databaseSetForReset.add(database);

	    Set<TableAllowStatements> tableAllowStatementsSet = csvRulesManagerLoader.getTableAllowStatementsSet();

	    debug("CsvRulesManager Rules Loaded:");
	    for (TableAllowStatements tableAllowStatements : tableAllowStatementsSet) {
		debug("" + tableAllowStatements.toString());
	    }

	    storedFileTime = currentFileTime;
	}
    }

    /**
     * Returns the &lt;database&gt;_rules_manager.csv for the passed database
     *
     * @param database
     * @throws FileNotFoundException
     */
    private static File getCsvFile(String database) throws FileNotFoundException {
	File file = PropertiesFileStore.get();

	Objects.requireNonNull(file, "file cannot be null!");

	if (!file.exists()) {
	    throw new FileNotFoundException("The properties file does not exist: " + file);
	}
	File dir = PropertiesFileStore.get().getParentFile();
	File csvFile = new File(dir + File.separator + database + "_rules_manager.csv");

	if (!csvFile.exists()) {
	    throw new FileNotFoundException("The CSV rules files does not exist: " + csvFile);
	}

	return csvFile;

    }

    private void debug(String string) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + CsvRulesManager.class.getSimpleName() + " " + string);
	}
    }

}
