/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2020,  KawanSoft SAS
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
package org.kawanfw.sql;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.api.server.web.WebServerApi;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.SqlTag;
import org.kawanfw.sql.version.Version;

/**
 *
 * AceQL Web Server start and stop with command line.
 *
 * @author Nicolas de Pomereu
 *
 */
@SuppressWarnings("deprecation")
public class WebServer {

    private static boolean DEBUG = FrameworkDebug.isSet(WebServer.class);

    /** Universal and clean line separator */
    private static String CR_LF = System.getProperty("line.separator");

    /**
     * Constructor
     */
    protected WebServer() {

    }

    /**
     * Prints usage
     *
     * @param options
     *            the CLI Options
     */
    private static void printUsage(Options options) {
	// automatically generate the help statement
	HelpFormatter formatter = new HelpFormatter();
	formatter.setWidth(400);

	String fromAceqlServerScript = System
		.getProperty("from.aceql-server.script");

	String help = null;

	if (fromAceqlServerScript != null
		&& fromAceqlServerScript.equals("true")) {
	    help = "aceql-server -start -host <hostname> -port <port> -properties <file>"
		    + CR_LF + "or " + CR_LF + "-stop -port <port> ";
	} else {
	    help = "java org.kawanfw.sql.WebServer -start -host <hostname> -port <port> -properties <file>"
		    + CR_LF + "or " + CR_LF + "-stop -port <port> ";
	}

	formatter.printHelp(help, options);
	System.out.println();
    }

    /**
     * Create the CLI Options
     *
     * @return the CLI Options
     * @throws IllegalArgumentException
     */
    private static Options createOptions() throws IllegalArgumentException {
	Options options = new Options();

	// add an option
	options.addOption("help", false, "print this message");

	options.addOption("start", false, "start the SQL Web server");

	options.addOption("stop", false, "stop the SQL Web server");

	options.addOption("version", false, "print version");

	String propertiesOptionMesssage = getPropertiesOptionMessage();

	@SuppressWarnings("static-access")
	Option propertiesOption = OptionBuilder.withArgName("file").hasArg()
		.withDescription(propertiesOptionMesssage).create("properties");

	@SuppressWarnings("static-access")
	Option hostOption = OptionBuilder.withArgName("hostname").hasArg()
		.withDescription("hostname of the Web server").create("host");

	@SuppressWarnings("static-access")
	Option portOption = OptionBuilder.withArgName("port number").hasArg()
		.withDescription("port number of the Web server. Defaults to "
			+ WebServerApi.DEFAULT_PORT)
		.create("port");

	options.addOption(propertiesOption);
	options.addOption(hostOption);
	options.addOption(portOption);

	return options;
    }

    private static String getPropertiesOptionMessage() {

	String message = "properties file to use for this SQL Web server session. ";

	File propertiesFile = getDefaultPropertiesFile();
	if (propertiesFile != null) {
	    message += "Defaults to " + propertiesFile;
	}

	return message;
    }

    /**
     * Starts or stops the AceQL Web Server.
     *
     * @param args
     *            the arguments of Web Server start/stop.
     *
     * @throws ParseException
     *             if any Exception when parsing command line
     * @throws IOException
     *             if any I/O Exception
     * @throws ConnectException
     *             if server is unable to connect to specified or default 9090
     *             port
     * @throws DatabaseConfigurationException
     *             if any error in configuration properties file
     */
    public static void main(String[] args) throws ParseException, IOException,
	    ConnectException, DatabaseConfigurationException {

	if (args.length > 0) {
	    debug("args[0]: " + args[0] + ":");
	}

	if (args.length > 1) {
	    debug("args[1]: " + args[1] + ":");
	}

	Options options = createOptions();
	CommandLineParser parser = new GnuParser();

	CommandLine cmd = null;
	try {
	    cmd = parser.parse(options, args);
	} catch (UnrecognizedOptionException e) {
	    System.out.println(e.getMessage());
	    System.out.println();
	    printUsage(options);
	    System.exit(-1);
	}

	if (cmd.hasOption("help")) {
	    printUsage(options);
	    System.exit(-1);
	}

	if (cmd.hasOption("version")) {
	    System.out.println(Version.getServerVersion());
	    System.out.println();
	    System.exit(0);
	}

	if (!cmd.hasOption("start") && !cmd.hasOption("stop")) {
	    System.err.println("Missing start or stop option." + " "
		    + SqlTag.PLEASE_CORRECT);
	    System.out.println();
	    printUsage(options);
	    System.exit(-1);
	}

	int port = WebServerApi.DEFAULT_PORT;

	if (cmd.hasOption("port")) {
	    String portStr = cmd.getOptionValue("port");

	    try {
		port = Integer.parseInt(portStr);
	    } catch (Exception e) {
		displayErrorAndExit(
			"The port parameter is not numeric: " + portStr + ".",
			options);
	    }
	}

	if (cmd.hasOption("start")) {

	    if (!cmd.hasOption("host")) {
		displayErrorAndExit("Missing host option.", options);
	    }

	    String host = cmd.getOptionValue("host");

	    File propertiesFile = null;

	    if (!cmd.hasOption("properties")) {

		propertiesFile = getDefaultPropertiesFile();

		if (propertiesFile == null) {
		    displayErrorAndExit("Missing properties option.", options);
		}

	    } else {
		propertiesFile = new File(cmd.getOptionValue("properties"));
	    }

	    WebServerApi webServerApi = new WebServerApi();
	    try {
		webServerApi.startServer(host, port, propertiesFile);
	    } catch (IllegalArgumentException e) {
		System.err.println(SqlTag.SQL_PRODUCT_START_FAILURE + " "
			+ SqlTag.USER_CONFIGURATION_FAILURE + " "
			+ e.getMessage());
		if (e.getCause() == null) {
		    // e.printStackTrace();
		} else {
		    e.getCause().printStackTrace();
		}
		System.err.println();
		System.exit(-1);
	    }

	    catch (ConnectException e) {
		System.err.println(SqlTag.SQL_PRODUCT_START_FAILURE + " "
			+ e.getMessage());
		e.printStackTrace();
		System.err.println();
		System.exit((-1));
	    }

	    catch (IOException e) {

		if (e instanceof UnknownHostException) {
		    System.err.println(SqlTag.SQL_PRODUCT_START_FAILURE + " "
			    + "Unknow host: " + e.getMessage());
		} else {
		    System.err.println(SqlTag.SQL_PRODUCT_START_FAILURE + " "
			    + e.getMessage());
		}

		if (e.getCause() == null) {
		    e.printStackTrace();
		} else {
		    e.getCause().printStackTrace();
		}
		System.err.println();
		System.exit(-1);
	    } catch (Exception e) {
		System.err.println(SqlTag.SQL_PRODUCT_START_FAILURE);
		e.printStackTrace();
		System.err.println();
		System.exit(-1);
	    }

	} else {

	    WebServerApi webServerApi = new WebServerApi();
	    try {
		webServerApi.stopServer(port);

		System.out.println(
			Version.PRODUCT.NAME + " Web server running on port "
				+ port + " successfully stopped!");
		System.out.println();
		System.exit(0);
	    } catch (IOException e) {

		if (e instanceof ConnectException) {
		    System.err.println(e.getMessage());
		} else {
		    System.err.println(
			    "Impossible to stop the SQL Web server running on port "
				    + port);
		    System.err.println(e.getMessage());

		    if (e.getCause() != null) {
			System.err.println("Java Exception Stack Trace:");
			e.printStackTrace();
		    }

		}

		System.err.println();
		System.exit(-1);
	    }
	}

    }

    /**
     * if ACEQL_HOME is set by calling script, we have a default properties
     * files
     *
     * @return ACEQL_HOME/conf/aceql-server.properties if ACEQL_HOME env var is
     *         set, else null
     */
    private static File getDefaultPropertiesFile() {

	File defaultPropertiesFile = null;

	String aceqlHome = System.getenv("ACEQL_HOME");

	if (aceqlHome != null) {

	    // Remove surrounding " if present
	    aceqlHome = aceqlHome.replaceAll("\"", "");

	    if (aceqlHome.endsWith(File.separator)) {
		aceqlHome = StringUtils.substringBeforeLast(aceqlHome,
			File.separator);
	    }
	    defaultPropertiesFile = new File(aceqlHome + File.separator + "conf"
		    + File.separator + "aceql-server.properties");
	}

	return defaultPropertiesFile;
    }

    /**
     * Displays the error message and exit Java
     *
     * @param message
     *            The message to display before exit
     * @param options
     *            the options passed
     */
    private static void displayErrorAndExit(String message, Options options) {
	System.err.println(message + " " + SqlTag.PLEASE_CORRECT);
	System.err.println();
	System.exit(-1);
    }

    /**
     * debug
     */
    private static void debug(String s) {
	if (DEBUG) {
	    System.out.println(s);
	}
    }

}
