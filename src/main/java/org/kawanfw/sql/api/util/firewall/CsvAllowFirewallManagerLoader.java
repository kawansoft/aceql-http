package org.kawanfw.sql.api.util.firewall;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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

    /** The tables of the current database */
    private Set<String> tableSet = null;

    /** the Set of TableAllowStatements) */
    private Set<TableAllowStatements> tableAllowStatementsSet = new HashSet<>();

    /**
     * Constructor.
     *
     * @param file     the file containing all the rule lines: username, table,
     *                 delete, insert, select, update
     * @param tableSet the table names of the database to check.
     */
    public CsvAllowFirewallManagerLoader(File file, Set<String> tableSet) {

	if (file == null) {
	    throw new NullPointerException("file is null!");
	}

	if (tableSet == null) {
	    throw new NullPointerException("tableSet is null!");
	}

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
		TableAllowStatements tableAllowStatements = tableAllowStatementsBuild(line);
		tableAllowStatementsSet.add(tableAllowStatements);
	    }
	}
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

	TableAllowStatements tableAllowStatements = new TableAllowStatements(username, table, allowDelete, allowInsert,
		allowSelect, allowUpdate);
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



    public Set<TableAllowStatements> getTableAllowStatementsSet() {
        return tableAllowStatementsSet;
    }

    public void setTableAllowStatementsSet(Set<TableAllowStatements> tableAllowStatementsSet) {
        this.tableAllowStatementsSet = tableAllowStatementsSet;
    }

    public static void main(String[] argv) throws Exception {
	Set<String> tableSet = new HashSet<String>();
	tableSet.add("all");
	tableSet.add("consumer");
	tableSet.add("orderlog");

	File file = new File("I:\\_dev_awake\\aceql-http-main\\aceql-http\\private\\rules.csv");
	CsvAllowFirewallManagerLoader csvAllowFirewallManagerLoader = new CsvAllowFirewallManagerLoader(file, tableSet);
	csvAllowFirewallManagerLoader.load();

	Set<TableAllowStatements> set = csvAllowFirewallManagerLoader.getTableAllowStatementsSet();

	for (TableAllowStatements tableAllowStatements : set) {
	    System.out.println(tableAllowStatements);
	}

	Map<String, Map<String, TableAllowStatements>> map = new TreeMap<>();

	for (TableAllowStatements tableAllowStatements : set) {
	    String username =  tableAllowStatements.getUsername();
	    String table =  tableAllowStatements.getTable();

	    if (map.containsKey(username)) {
		Map<String, TableAllowStatements> mapTable = map.get(username);
		mapTable.put(table, tableAllowStatements);
		map.put(username,  mapTable);
	    }
	    else {
		Map<String, TableAllowStatements> mapTable = new TreeMap<>();
		mapTable.put(table, tableAllowStatements);
		map.put(username,  mapTable);
	    }
	}

	System.out.println();
	//System.out.println(map);

	for (Map.Entry<String, Map<String, TableAllowStatements>> entry : map.entrySet()) {
	    String username = entry.getKey();
	    Map<String, TableAllowStatements> value = entry.getValue();
	    System.out.println("username: " + username + " / "+ value);
	}

    }
}
