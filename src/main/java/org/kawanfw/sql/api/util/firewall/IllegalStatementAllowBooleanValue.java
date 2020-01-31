package org.kawanfw.sql.api.util.firewall;

public class IllegalStatementAllowBooleanValue extends IllegalArgumentException {

    private String statement = null;
    private int lineNumber = -1;

    private static final long serialVersionUID = 3329147381309094047L;

    public IllegalStatementAllowBooleanValue(String statement, int lineNumber) {
	super("value is not expected \"false\" or \"true\" for " + statement + "column on line " + lineNumber);
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
