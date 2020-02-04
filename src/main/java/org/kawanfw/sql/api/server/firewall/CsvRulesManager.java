/**
 *
 */
package org.kawanfw.sql.api.server.firewall;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kawanfw.sql.api.server.DefaultDatabaseConfigurator;
import org.kawanfw.sql.api.server.StatementAnalyzer;
import org.kawanfw.sql.api.util.firewall.CsvRulesManagerLoader;
import org.kawanfw.sql.api.util.firewall.DatabaseUserTableTriplet;
import org.kawanfw.sql.api.util.firewall.TableAllowStatements;
import org.kawanfw.sql.metadata.AceQLMetaData;
import org.kawanfw.sql.servlet.ServerSqlManager;

/**
 * Firewall manager that checks each SQL request against the content of a CSV File.
 * <br>
 * The name of the CSV file that will be used by a database is:
 * <code>&lt;database&gt;_rules_manager.csv</code> where database is the name of the database declared in the .properties files.
 * <br>
 * The file must be located in the same directory as the AceQL .properties file used when starting the AceQL server.
 * <br><br>
 * The CSV file contains the rules for accessing the tables, with semicolon for separator:
 * <ul>
 * <li>First line contains the element names: <code>username;table;delete;insert;select;update</code></li>
 * <li>Subsequent lines contain the rules, with the values for each element:
 * <ul>
 * <li>{@code username}: AceQL username of the connected client.</li>
 * <li>{@code table}: the table name to access. Name must not include dots and prefixes.</i>
 * <li>{@code delete}: {@code true} if username has the right to delete rows of the table, else {@code false}.</li>
 * <li>{@code insert}: {@code true} if username has the right to insert rows in the table, else {@code false}.</li>
 * <li>{@code select}: {@code true} if username has the right to select rows of the table, else {@code false}.</li>
 * <li>{@code update}: {@code true} if username has the right to update rows of the table, else {@code false}.</li>
 * </ul>
 * </ul>
 * Note that:
 * <ul>
 * <li>{@code public} value may be used for username column and means any username.
 * {@code public} supersedes all other rules defines for users for the specified {@code table}, when request is allowed.</li>
 * <li>{@code all} value is allowed for table column and means any table. {@code all} supersedes all other rules
 * to apply to the table for the specified {@code username}, when request is allowed: </li>
 * </ul> *

 * @author Nicolas de Pomereu
 */
public class CsvRulesManager extends DefaultSqlFirewallManager implements SqlFirewallManager {

    private static final boolean DEBUG = false;

    /**
     * The map that contains for each database/username the table and their rights
     */
    private Map<DatabaseUserTableTriplet, TableAllowStatements> mapTableAllowStatementsSet = null;

    /**
     * Constructor. {@code SqlFirewallManager} implementation must have no
     * constructor or a unique no parameters constructor.
     */
    public CsvRulesManager() {

    }

    /**
     * Allows the execution of the statement if a allowing rules exists in the :
     * <br>
     * <code>&lt;database&gt;_rules_manager.csv</code> file.
     */
    @Override
    public boolean allowSqlRunAfterAnalysis(String username, String database, Connection connection, String ipAddress,
	    String sql, boolean isPreparedStatement, List<Object> parameterValues) throws IOException, SQLException {

	// Load all rules if not already done:
	loadRules(database, connection);

	boolean isAllowed = isAllowed(username, database, sql, parameterValues);
	return isAllowed;
    }

    /**
     * Logs the info using {@code DefaultDatabaseConfigurator#getLogger() Logger}.
     */
    @Override
    public void runIfStatementRefused(String username, String database, Connection connection, String ipAddress,
	    boolean isMetadataQuery, String sql, List<Object> parameterValues) throws IOException, SQLException {

	String logInfo = "Client username " + username + " (IP: " + ipAddress
		+ ") has been denied by CsvRulesManager SqlFirewallManager executing the statement: " + sql + ".";

	DefaultDatabaseConfigurator defaultDatabaseConfigurator = new DefaultDatabaseConfigurator();
	Logger logger = defaultDatabaseConfigurator.getLogger();
	logger.log(Level.WARNING, logInfo);
    }

    /**
     * Will say id there is a rule that allows for the usename the sql statement
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

	boolean isAllowed = false;

	for (String table : tables) {
	    table = table.toLowerCase();
	    // Test public
	    DatabaseUserTableTriplet databaseUserTableTriplet = new DatabaseUserTableTriplet(database, "public", table);
	    TableAllowStatements tableAllowStatements = mapTableAllowStatementsSet.get(databaseUserTableTriplet);

	    if (tableAllowStatements != null) {
		isAllowed = isAllowed(tableAllowStatements, statementName);
		if (isAllowed) {
		    return true;
		}
	    }

	    databaseUserTableTriplet = new DatabaseUserTableTriplet(database, username, table);
	    tableAllowStatements = mapTableAllowStatementsSet.get(databaseUserTableTriplet);

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
	if (mapTableAllowStatementsSet == null) {
	    File csvFile = getCsvFile(database);

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

	    Set<TableAllowStatements> tableAllowStatementsSet = csvRulesManagerLoader.getTableAllowStatementsSet();

	    debug("CsvRulesManager Rules Loaded:");
	    for (TableAllowStatements tableAllowStatements : tableAllowStatementsSet) {
		debug("" + tableAllowStatements.toString());
	    }
	}
    }

    /**
     * Returns the user.home/.aceql-http{database}sql_firewal_manager_rules.csv for
     * the passed database
     *
     * @param database
     * @throws FileNotFoundException
     */
    public static File getCsvFile(String database) throws FileNotFoundException {
	File dir = ServerSqlManager.getAceqlServerPropertiesDirectory();
	File csvFile = new File(dir + File.separator + database + "_rules_manager.csv");

	if (!csvFile.exists()) {
	    throw new FileNotFoundException("The CSV rules files does not exist: " + csvFile);
	}

	return csvFile;

    }

    @SuppressWarnings("unused")
    private void debug(String string) {
	if (DEBUG) {
	    System.out.println(new Date() + string);
	}
    }
}
