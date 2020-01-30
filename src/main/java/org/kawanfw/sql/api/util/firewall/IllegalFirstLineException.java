package org.kawanfw.sql.api.util.firewall;

public class IllegalFirstLineException extends IllegalArgumentException {

    private static final long serialVersionUID = -7299666473941182269L;

    public IllegalFirstLineException(String s) {
	super(s);
    }

    public IllegalFirstLineException(Throwable cause) {
	super(cause);
    }

    public IllegalFirstLineException(String message, Throwable cause) {
	super(message, cause);

    }

}
