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

import java.io.File;

public class FirstLineChecker {

    private static final int HEADER_LINE_NB_ELEMENTS = 7;

    private File file = null;
    private String line = null;

    String username;
    String table;
    String delete;
    String insert;
    String select;
    String update;
    String optionalComments;

    public FirstLineChecker(File file, String line) {
	this.file = file;
	this.line = line;
	treat();
    }

    private void treat() {
	String[] elements = line.split(";");

	if (elements.length != HEADER_LINE_NB_ELEMENTS) {
	    throw new IllegalFirstLineException(file, "There must be " + HEADER_LINE_NB_ELEMENTS
		    + " column names in CSV file header line. Incorrect header line: " + line);
	}

	int i = 0;
	username = elements[i++];
	table = elements[i++];
	delete = elements[i++];
	insert = elements[i++];
	select = elements[i++];
	update = elements[i++];
	optionalComments = elements[i++];

    }

    public void checkValues() {
	if (!username.equalsIgnoreCase("username")) {
	    throw new IllegalFirstLineException(file, "Missing \"username\" first column on first line.");
	}
	if (!table.equalsIgnoreCase("table")) {
	    throw new IllegalFirstLineException(file, "Missing \"table\" second column on first line.");
	}
	if (!delete.equalsIgnoreCase("delete")) {
	    throw new IllegalFirstLineException(file, "Missing \"delete\" third column on first line.");
	}
	if (!insert.equalsIgnoreCase("insert")) {
	    throw new IllegalFirstLineException(file, "Missing \"insert\" fourth column on first line.");
	}
	if (!select.equalsIgnoreCase("select")) {
	    throw new IllegalFirstLineException(file, "Missing \"select\" fifth column on first line.");
	}
	if (!update.equalsIgnoreCase("update")) {
	    throw new IllegalFirstLineException(file, "Missing \"update\" sixth column on first line.");
	}
	if (!optionalComments.equalsIgnoreCase("optional comments")) {
	    throw new IllegalFirstLineException(file, "Missing \"optional comments\" seventh column on first line.");
	}
    }

}
