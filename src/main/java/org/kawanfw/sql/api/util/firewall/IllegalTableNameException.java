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
package org.kawanfw.sql.api.util.firewall;

import java.io.File;

public class IllegalTableNameException extends IllegalArgumentException {

    private static final long serialVersionUID = -1392006668676537022L;

    private String table = null;
    private int lineNumber = -1;

    public IllegalTableNameException(File file, String table, int lineNumber) {
	super(file.getName() + ": " + "table \"" + table + "\" does no exists in database (line " + lineNumber + ").");
	this.table = table;
	this.lineNumber = lineNumber;
    }

    public String getTable() {
	return table;
    }

    public int getLineNumber() {
	return lineNumber;
    }

}
