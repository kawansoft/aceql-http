/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.api.util.firewall;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

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
public class CsvRulesManagerLoader {

    private static final int HEADER_LINE_NB_ELEMENTS = 7;

    private File file = null;
    private String database = null;

    /** The tables of the current database */
    private Set<String> tableSet = null;

    /**
     * The map that contains for each database/username the table and their rights
     */
    private Map<DatabaseUserTableTriplet, TableAllowStatements> mapTableAllowStatementsSet = new ConcurrentSkipListMap<>();
    private Set<TableAllowStatements> tableAllowStatementsSet = new ConcurrentSkipListSet<>();

    /**
     * Constructor.
     *
     * @param file     the file containing all the rule lines: username, table,
     *                 delete, insert, select, update
     * @param database the database name
     * @param tableSet the table names of the database to check.
     */
    public CsvRulesManagerLoader(File file, String database, Set<String> tableSet) {
	this.database = Objects.requireNonNull(database, "file cannot be null!");
	this.file = Objects.requireNonNull(file, "file cannot be null!");
	this.tableSet = Objects.requireNonNull(tableSet, "tableSet cannot be null!");
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

		tableAllowStatementsSet.add(tableAllowStatements);
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
	boolean delete = Boolean.parseBoolean(elements[i++]);
	boolean insert = Boolean.parseBoolean(elements[i++]);
	boolean select = Boolean.parseBoolean(elements[i++]);
	boolean update = Boolean.parseBoolean(elements[i++]);

	TableAllowStatements tableAllowStatements = new TableAllowStatements(database, username, table, delete, insert,
		select, update);
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

	    // If header line is badly formated, throw clean Exception
	    FirstLineChecker firstLineChecker = new FirstLineChecker(file, line);
	    firstLineChecker.checkValues();

	    int lineNumber = 1;
	    while ((line = bufferedReader.readLine()) != null) {
		// If current line is badly formated, throw clean Exception
		checkCurrentLineIntegrity(line, lineNumber++);
	    }
	}
    }

    private void checkCurrentLineIntegrity(String line, int lineNumber) {
	String[] elements = line.split(";");

	// Double check...
	if (elements.length != HEADER_LINE_NB_ELEMENTS && elements.length != HEADER_LINE_NB_ELEMENTS - 1) {
	    throw new IllegalFirstLineException(file, "There must be " + HEADER_LINE_NB_ELEMENTS + " or "
		    + (HEADER_LINE_NB_ELEMENTS - 1) + " values in CSV file current line. Incorrect line: " + line);
	}

	int i = 1;
	String table = elements[i++].toLowerCase();
	if (!tableSet.contains(table)) {
	    throw new IllegalTableNameException(file, table, lineNumber);
	}

	String value = null;
	value = elements[i++].toLowerCase();
	if (!isStrictBooleanValue(value)) {
	    throw new IllegalStatementAllowBooleanValue(file, value, "delete", lineNumber);
	}

	value = elements[i++].toLowerCase();
	if (!isStrictBooleanValue(value)) {
	    throw new IllegalStatementAllowBooleanValue(file, value, "insert", lineNumber);
	}

	value = elements[i++].toLowerCase();
	if (!isStrictBooleanValue(value)) {
	    throw new IllegalStatementAllowBooleanValue(file, value, "select", lineNumber);
	}

	value = elements[i++].toLowerCase();
	if (!isStrictBooleanValue(value)) {
	    throw new IllegalStatementAllowBooleanValue(file, value, "update", lineNumber);
	}
    }

    private boolean isStrictBooleanValue(String booleanValue) {
	if (booleanValue == null) {
	    return false;
	}

	return booleanValue.toLowerCase().contentEquals("false") || booleanValue.toLowerCase().contentEquals("true");

    }

    /**
     * Returns the Map of allowed statements per table, per database and username.
     *
     * @return the Map of allowed statements per table, per database and username.
     */
    public Map<DatabaseUserTableTriplet, TableAllowStatements> getMapTableAllowStatementsSet() {
	return mapTableAllowStatementsSet;
    }

    public Set<TableAllowStatements> getTableAllowStatementsSet() {
	return tableAllowStatementsSet;
    }

    public static void main(String[] argv) throws Exception {

	String database = "sampledb";

	Set<String> tableSet = new HashSet<String>();
	tableSet.add("all");
	tableSet.add("banned_usernames");
	tableSet.add("customer");
	tableSet.add("orderlog");
	tableSet.add("documentation");

	File file = new File("I:\\_dev_awake\\aceql-http-main\\aceql-http\\conf\\sampledb_rules_manager.csv");
	CsvRulesManagerLoader csvRulesManagerLoader = new CsvRulesManagerLoader(file, database, tableSet);
	csvRulesManagerLoader.load();

	Map<DatabaseUserTableTriplet, TableAllowStatements> mapTableAllowStatements = csvRulesManagerLoader
		.getMapTableAllowStatementsSet();

	for (Map.Entry<DatabaseUserTableTriplet, TableAllowStatements> entry : mapTableAllowStatements.entrySet()) {
	    DatabaseUserTableTriplet triplet = entry.getKey();
	    TableAllowStatements tableAllowStatements = entry.getValue();
	    System.out.println(triplet + " / " + tableAllowStatements);
	}

	System.out.println();
	Set<TableAllowStatements> tableAllowStatementsSet = csvRulesManagerLoader.getTableAllowStatementsSet();
	for (TableAllowStatements tableAllowStatements : tableAllowStatementsSet) {
	    System.out.println(tableAllowStatements);
	}

    }
}
