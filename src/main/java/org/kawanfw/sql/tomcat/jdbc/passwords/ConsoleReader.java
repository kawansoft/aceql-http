package org.kawanfw.sql.tomcat.jdbc.passwords;

import java.io.Console;

/*
 java org.kawanfw.sql.tomcat.jdbc.passwords.ConsoleReader
 */
public class ConsoleReader {

    public static void main(String[] args)
    {
	Console console = System.console();
	@SuppressWarnings("unused")
	char[] passwordArray = console.readPassword("JDBC password: ");
    }

}
