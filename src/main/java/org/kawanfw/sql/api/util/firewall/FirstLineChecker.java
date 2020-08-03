/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2020,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.
 *
 * AceQL HTTP is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * AceQL HTTP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301  USA
 *
 * Any modifications to this file must keep this entire header
 * intact.
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
