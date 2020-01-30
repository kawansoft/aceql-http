package org.kawanfw.sql.api.util.firewall;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
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
public class CsvAllowFirewallManagerLoader {

    private File file = null;

    /** The tables of the current database */
    private Set<String> tableSet = null;

    /** the Map of (username, TableAllowStatements) */
    private Map<String, TableAllowStatements> mapTableAllowStatementsPerUser = new HashMap<>();

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
	    String line = null;
	    while ((line = bufferedReader.readLine()) != null) {
		TableAllowStatements tableAllowStatements = tableAllowStatementsBuild(line);
		mapTableAllowStatementsPerUser.put(tableAllowStatements.getUsername(), tableAllowStatements);
	    }
	}
    }

    private TableAllowStatements tableAllowStatementsBuild(String line) {
	String[] elements = line.split(";");
	String username = elements[0];
	String table = elements[1];
	boolean allowDelete = Boolean.parseBoolean(elements[2]);
	boolean allowInsert = Boolean.parseBoolean(elements[3]);
	boolean allowSelect = Boolean.parseBoolean(elements[4]);
	boolean allowUpdate = Boolean.parseBoolean(elements[5]);

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
	    throw new IllegalFirstLineException("There must be 6 column names in CSV file header. Header is: " + line);
	}

	String table = elements[1].toLowerCase();
	if (!tableSet.contains(table)) {
	    throw new IllegalTableNameException(table, lineNumber);
	}

	if (!isStrictBooleanValue(elements[2].toLowerCase())) {
	    throw new IllegalStatementAllowBooleanValue("delete", lineNumber);
	}

	if (!isStrictBooleanValue(elements[3].toLowerCase())) {
	    throw new IllegalStatementAllowBooleanValue("insert", lineNumber);
	}

	if (!isStrictBooleanValue(elements[4].toLowerCase())) {
	    throw new IllegalStatementAllowBooleanValue("select", lineNumber);
	}

	if (!isStrictBooleanValue(elements[5].toLowerCase())) {
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
	    throw new IllegalFirstLineException("There must be 6 column names in CSV file header. Header is: " + line);
	}

	String username = elements[0];
	String table = elements[1];
	String delete = elements[2];
	String insert = elements[3];
	String select = elements[4];
	String update = elements[5];

	if (username.contentEquals("username")) {
	    throw new IllegalFirstLineException("Missing \"username\" header.");
	}
	if (table.contentEquals("table")) {
	    throw new IllegalFirstLineException("Missing \"table\" header.");
	}
	if (delete.contentEquals("delete")) {
	    throw new IllegalFirstLineException("Missing \"delete\" header.");
	}
	if (insert.contentEquals("insert")) {
	    throw new IllegalFirstLineException("Missing \"insert\" header.");
	}
	if (select.contentEquals("select")) {
	    throw new IllegalFirstLineException("Missing \"select\" header.");
	}
	if (update.contentEquals("update")) {
	    throw new IllegalFirstLineException("Missing \"update\" header.");
	}

    }

}
