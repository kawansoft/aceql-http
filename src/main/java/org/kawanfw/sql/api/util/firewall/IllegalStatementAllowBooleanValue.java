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
