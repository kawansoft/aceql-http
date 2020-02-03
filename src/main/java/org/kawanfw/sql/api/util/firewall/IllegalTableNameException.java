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
