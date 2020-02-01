package org.kawanfw.sql.api.util.firewall;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Loads all the rules contained in a CSV structured like:
 *
 * <pre>
 * <code>
username;table;delete;insert;select;update
&lt;username&gt;;&lt;table name&gt;;boolean;boolean;boolean;boolean </code>
 * </pre>
 *
 * @author Nicolas de Pomereu
 *
 */
/**
 * @author Nicolas de Pomereu
 *
 */
public class CsvAllowFirewallManagerLoader {

    private File file = null;
    private String database = null;

    /** The tables of the current database */
    private Set<String> tableSet = null;

    ///** the Set of TableAllowStatements) */
    //private Set<TableAllowStatements> tableAllowStatementsSet = new HashSet<>();

    private Map<DatabaseUserTableTriplet, TableAllowStatements> mapTableAllowStatementsSet = new HashMap<>();


    /**
     * Constructor.
     * @param file     the file containing all the rule lines: username, table,
     *                 delete, insert, select, update
     * @param database the database name
     * @param tableSet the table names of the database to check.
     */
    public CsvAllowFirewallManagerLoader(File file, String database, Set<String> tableSet) {

	if (file == null) {
	    throw new NullPointerException("file is null!");
	}

	if (database == null) {
	    throw new NullPointerException("database is null!");
	}

	if (tableSet == null) {
	    throw new NullPointerException("tableSet is null!");
	}

	this.database = database;
	this.file = file;
	this.tableSet = tableSet;
    }

    /**
     * Loads the CSV containing the rules, one rule per line.
     *
     * @param file the CSV containing the rules, one rule per line.
     * @throws IOException
     * @throws FileNotFoundException
     */
    public void load() throws IOException, FileNotFoundException {

	if (!file.exists()) {
	    throw new FileNotFoundException("The file does not exist: " + file);
	}

	checkFileIntegrity();

	try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file));) {
	    bufferedReader.readLine(); // Read first line
	    String line = null;
	    while ((line = bufferedReader.readLine()) != null) {

		DatabaseUserTableTriplet databaseUserTableTriplet = databaseUserTableTripletBuild(line);
		TableAllowStatements tableAllowStatements = tableAllowStatementsBuild(line);

		mapTableAllowStatementsSet.put(databaseUserTableTriplet, tableAllowStatements);

	    }
	}
    }

    private DatabaseUserTableTriplet databaseUserTableTripletBuild(String line) {
	String[] elements = line.split(";");

	int i = 0;
	String username = elements[i++];
	String table = elements[i++];

	DatabaseUserTableTriplet databaseUserTableTriplet = new DatabaseUserTableTriplet(database, username, table);
	return databaseUserTableTriplet;
    }

    private TableAllowStatements tableAllowStatementsBuild(String line) {
	String[] elements = line.split(";");

	int i = 0;
	String username = elements[i++];
	String table = elements[i++];
	boolean allowDelete = Boolean.parseBoolean(elements[i++]);
	boolean allowInsert = Boolean.parseBoolean(elements[i++]);
	boolean allowSelect = Boolean.parseBoolean(elements[i++]);
	boolean allowUpdate = Boolean.parseBoolean(elements[i++]);

	TableAllowStatements tableAllowStatements = new TableAllowStatements(database, username, table, allowDelete,
		allowInsert, allowSelect, allowUpdate);
	return tableAllowStatements;
    }

    /**
     * Check all lines of file for integrity of content:
     * <ul>
     * <li>First line must be CSV head: username;table;delete;insert;select;update
     * <li>All subsequent lines must contain
     * username;table;true/false;true/false;true/false;true/false</li>
     * <li>Table names are checked against tableSet. "all" is accepted.</li>
     * <li>usernames are not checked.</li>
     * </ul>
     *
     * @throws IOException
     */
    private void checkFileIntegrity() throws IOException {

	try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file));) {
	    String line = null;
	    line = bufferedReader.readLine();
	    checkFirstLineIntegrity(line); // If header line is badly formated, throw clean Exception

	    int lineNumber = 1;
	    while ((line = bufferedReader.readLine()) != null) {
		// If current line is badly formated, throw clean Exception
		checkCurrentLineIntegrity(line, lineNumber);
	    }
	}
    }

    private void checkCurrentLineIntegrity(String line, int lineNumber) {
	String[] elements = line.split(";");

	// Double check...
	if (elements.length != 6) {
	    throw new IllegalFirstLineException(
		    "There must be 6 column names in CSV file header line. Incorrect header line: " + line);
	}

	int i = 1;
	String table = elements[i++].toLowerCase();
	if (!tableSet.contains(table)) {
	    throw new IllegalTableNameException(table, lineNumber);
	}

	if (!isStrictBooleanValue(elements[i++].toLowerCase())) {
	    throw new IllegalStatementAllowBooleanValue("delete", lineNumber);
	}

	if (!isStrictBooleanValue(elements[i++].toLowerCase())) {
	    throw new IllegalStatementAllowBooleanValue("insert", lineNumber);
	}

	if (!isStrictBooleanValue(elements[i++].toLowerCase())) {
	    throw new IllegalStatementAllowBooleanValue("select", lineNumber);
	}

	if (!isStrictBooleanValue(elements[i++].toLowerCase())) {
	    throw new IllegalStatementAllowBooleanValue("update", lineNumber);
	}
    }

    private boolean isStrictBooleanValue(String booleanValue) {
	if (booleanValue == null) {
	    return false;
	}
	if (!booleanValue.toLowerCase().contentEquals("false") && !booleanValue.toLowerCase().contentEquals("true")) {
	    return false;
	}
	return true;

    }

    /**
     * Checks that first line integrity
     *
     * @param line
     */
    private void checkFirstLineIntegrity(String line) {

	String[] elements = line.split(";");

	if (elements.length != 6) {
	    throw new IllegalFirstLineException(
		    "There must be 6 column names in CSV file header line. Incorrect header line: " + line);
	}

	int i = 0;
	String username = elements[i++];
	String table = elements[i++];
	String delete = elements[i++];
	String insert = elements[i++];
	String select = elements[i++];
	String update = elements[i++];

	if (!username.equals("username")) {
	    throw new IllegalFirstLineException("Missing \"username\" first column on first line.");
	}
	if (!table.equals("table")) {
	    throw new IllegalFirstLineException("Missing \"table\" second column on first line.");
	}
	if (!delete.equals("delete")) {
	    throw new IllegalFirstLineException("Missing \"delete\" third column on first line.");
	}
	if (!insert.equals("insert")) {
	    throw new IllegalFirstLineException("Missing \"insert\" fourth column on first line.");
	}
	if (!select.equals("select")) {
	    throw new IllegalFirstLineException("Missing \"select\" fifth column on first line.");
	}
	if (!update.equals("update")) {
	    throw new IllegalFirstLineException("Missing \"update\" sixth column on first line.");
	}
    }

    /**
     * Returns the Map of allowed statements per table, per database and username.
     * @return the Map of allowed statements per table, per database and username.
     */
    public Map<DatabaseUserTableTriplet, TableAllowStatements> getMapTableAllowStatementsSet() {
        return mapTableAllowStatementsSet;
    }

    public static void main(String[] argv) throws Exception {

	String database = "kawansoft_example";

	Set<String> tableSet = new HashSet<String>();
	tableSet.add("all");
	tableSet.add("consumer");
	tableSet.add("orderlog");

	File file = new File("I:\\_dev_awake\\aceql-http-main\\aceql-http\\private\\rules.csv");
	CsvAllowFirewallManagerLoader csvAllowFirewallManagerLoader = new CsvAllowFirewallManagerLoader(file, database, tableSet);
	csvAllowFirewallManagerLoader.load();

	Map<DatabaseUserTableTriplet, TableAllowStatements> mapTableAllowStatementsSet = csvAllowFirewallManagerLoader.getMapTableAllowStatementsSet();

	for (Map.Entry<DatabaseUserTableTriplet, TableAllowStatements> entry : mapTableAllowStatementsSet.entrySet()) {
	    DatabaseUserTableTriplet triplet = entry.getKey();
	    TableAllowStatements tableAllowStatements = entry.getValue();
	    System.out.println(triplet + " / " + tableAllowStatements);
	}

    }
}
