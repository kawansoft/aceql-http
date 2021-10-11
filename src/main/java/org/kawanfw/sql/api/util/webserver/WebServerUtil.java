/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2021,  KawanSoft SAS
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
package org.kawanfw.sql.api.util.webserver;

import java.io.IOException;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.kawanfw.sql.util.SqlTag;

/**
 * Util methods for WebServer class.
 * @author Nicolas de Pomereu
 *
 */
public class WebServerUtil {

    /** Universal and clean line separator */
    private static String CR_LF = System.getProperty("line.separator");

    /**
    *
    */
    public static void systemExitWrapper(int value) {
	System.exit(-1);
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
	systemExitWrapper(-1);
    }

}
