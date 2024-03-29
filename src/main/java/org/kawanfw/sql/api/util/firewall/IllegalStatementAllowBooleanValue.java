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

public class IllegalStatementAllowBooleanValue extends IllegalArgumentException {

    private String statement = null;
    private int lineNumber = -1;

    private static final long serialVersionUID = 3329147381309094047L;

    public IllegalStatementAllowBooleanValue(File file, String value, String statement, int lineNumber) {
	super(file.getName() + ": " + "value \"" + value + "\" is not of expected \"false\" or \"true\" for \"" + statement + "\" column (line " + lineNumber + ").");
	this.statement = statement;
	this.lineNumber = lineNumber;
    }

    public String getStatement() {
        return statement;
    }

    public int getLineNumber() {
        return lineNumber;
    }

}
