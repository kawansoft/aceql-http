package org.kawanfw.sql.api.util.firewall;

import java.io.File;

public class IllegalFirstLineException extends IllegalArgumentException {

    private static final long serialVersionUID = -7299666473941182269L;

    public IllegalFirstLineException(File file, String s) {
	super(file.getName() + ":  " + s);
    }

    public IllegalFirstLineException(Throwable cause) {
	super(cause);
    }

    public IllegalFirstLineException(String message, Throwable cause) {
	super(message, cause);

    }

}
