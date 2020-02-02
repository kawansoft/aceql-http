/**
 *
 */
package org.kawanfw.sql.api.server.firewall;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kawanfw.sql.api.server.StatementAnalyzer;
import org.kawanfw.sql.api.util.firewall.CsvRulesManagerLoader;
import org.kawanfw.sql.api.util.firewall.DatabaseUserTableTriplet;
import org.kawanfw.sql.api.util.firewall.TableAllowStatements;
import org.kawanfw.sql.metadata.AceQLMetaData;
import org.kawanfw.sql.servlet.ServerSqlManager;

/**
 * @author Nicolas de Pomereu
 *
 */
public class CsvRulesManager extends DefaultSqlFirewallManager implements SqlFirewallManager {

    /**
     * The map that contains for each database/username the table and their rights
     */
    private Map<DatabaseUserTableTriplet, TableAllowStatements> mapTableAllowStatementsSet = null;

    /**
     * Mandatory Constructor.
     */
    public CsvRulesManager() {

    }

    @Override
    public boolean allowSqlRunAfterAnalysis(String username, String database, Connection connection, String ipAddress,
	    String sql, boolean isPreparedStatement, List<Object> parameterValues) throws IOException, SQLException {

	// Load all rules if not already done:
	loadRules(database, connection);

	boolean isAllowed = isAllowed(username, database, sql, parameterValues);
	return isAllowed;

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

	// No previous allowance, return false
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
	    Set<String> tableSet = new HashSet<>();
	    // Load in lowercase
	    for (String table : tables) {
		tableSet.add(table.toLowerCase());
	    }
	    tableSet.add("all"); // Add "all" values for all tables
	    CsvRulesManagerLoader csvRulesManagerLoader = new CsvRulesManagerLoader(csvFile, database, tableSet);
	    csvRulesManagerLoader.load();

	    mapTableAllowStatementsSet = csvRulesManagerLoader.getMapTableAllowStatementsSet();

	    Set<TableAllowStatements> tableAllowStatementsSet = csvRulesManagerLoader.getTableAllowStatementsSet();

	    System.out.println("CsvRulesManager Rules Loaded:");
	    for (TableAllowStatements tableAllowStatements : tableAllowStatementsSet) {
		System.out.println(tableAllowStatements);
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
	File csvFile = new File(dir + File.separator + database + "rules_manager.csv");

	if (!csvFile.exists()) {
	    throw new FileNotFoundException("The CSV rules files does not exist: " + csvFile);
	}

	return csvFile;

    }

    @Override
    public void runIfStatementRefused(String username, String database, Connection connection, String ipAddress,
	    boolean isMetadataQuery, String sql, List<Object> parameterValues) throws IOException, SQLException {
	super.runIfStatementRefused(username, database, connection, ipAddress, isMetadataQuery, sql, parameterValues);
    }

}
