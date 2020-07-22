package org.kawanfw.sql;

import java.io.IOException;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.kawanfw.sql.util.SqlTag;

/**
 * Util mehtods for WebServer class.
 * @author Nicolas de Pomereu
 *
 */
class WebServerUtil {

    /** Universal and clean line separator */
    private static String CR_LF = System.getProperty("line.separator");

    /**
    *
    */
    public static void systemExitWrapperZero() {
	System.exit(0);
    }

    /**
     * @param e
     */
    public static void printCauseException(IOException e) {
	if (e.getCause() == null) {
	    e.printStackTrace();
	} else {
	    e.getCause().printStackTrace();
	}
    }

    /**
    *
    */
    public static void SystemExitWrapperMinusOne() {
	System.exit(-1);
    }

    /**
     * Prints usage
     *
     * @param options the CLI Options
     */
    public static void printUsage(Options options) {
	// automatically generate the help statement
	HelpFormatter formatter = new HelpFormatter();
	formatter.setWidth(400);

	String fromAceqlServerScript = System.getProperty("from.aceql-server.script");

	String help = null;

	if (fromAceqlServerScript != null && fromAceqlServerScript.equals("true")) {
	    help = "aceql-server -start -host <hostname> -port <port> -properties <file>" + CR_LF + "or " + CR_LF
		    + "-stop -port <port> ";
	} else {
	    help = "java org.kawanfw.sql.WebServer -start -host <hostname> -port <port> -properties <file>" + CR_LF
		    + "or " + CR_LF + "-stop -port <port> ";
	}

	formatter.printHelp(help, options);
	System.out.println();
    }

    /**
     * Displays the error message and exit Java
     *
     * @param message The message to display before exit
     */
    public static void displayErrorAndExit(String message) {
	System.err.println(message + " " + SqlTag.PLEASE_CORRECT);
	System.err.println();
	SystemExitWrapperMinusOne();
    }

}
