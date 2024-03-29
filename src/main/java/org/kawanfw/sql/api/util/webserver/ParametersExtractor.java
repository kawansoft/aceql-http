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
package org.kawanfw.sql.api.util.webserver;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.util.SqlTag;
import org.kawanfw.sql.version.VersionWrapper;

/**
 * Extract and build the parameters for the Web Server start/stop.
 * @author Nicolas de Pomereu
 *
 */
public class ParametersExtractor {

    String[] args;

    /** The parameters to return */
    private int port;
    private String host;
    private boolean isStartCommand;

    private File propertiesFile;

    /**
     * Constructor.
     *
     * @param args
     * @throws ParseException
     */
    public ParametersExtractor(String[] args) throws ParseException {
	super();
	this.args = args;
	treat();
    }

    /**
     * Check and extract the parameters
     * @throws ParseException
     */
    private void treat() throws ParseException {
	Options options = createOptions();

	CommandLineParser parser = new DefaultParser();

	CommandLine cmd = null;
	cmd = chekParameters(options, parser);

	port = WebServerApiWrapper.DEFAULT_PORT;

	if (cmd.hasOption("port")) {
	    String portStr = cmd.getOptionValue("port");

	    try {
		port = Integer.parseInt(portStr);
	    } catch (Exception e) {
		WebServerUtil.displayErrorAndExit("The port parameter is not numeric: " + portStr + ".");
	    }
	}

	if (cmd.hasOption("start")) {
	    isStartCommand = true;

	    if (!cmd.hasOption("host")) {
		WebServerUtil.displayErrorAndExit("Missing host option.");
	    }

	    host = cmd.getOptionValue("host");

	    propertiesFile = null;

	    if (!cmd.hasOption("properties")) {

		propertiesFile = getDefaultPropertiesFile();

		if (propertiesFile == null) {
		    WebServerUtil.displayErrorAndExit("Missing properties option.");
		}

	    } else {
		propertiesFile = new File(cmd.getOptionValue("properties"));
	    }
	}
	else {
	    isStartCommand = false;
	}

    }

    /**
     * @param options
     * @param parser
     * @return
     * @throws ParseException
     */
    private CommandLine chekParameters(Options options, CommandLineParser parser)
	    throws ParseException {

	CommandLine cmd = null;
	try {
	    cmd = parser.parse(options, args);
	} catch (UnrecognizedOptionException e) {
	    System.out.println(e.getMessage());
	    System.out.println();
	    WebServerUtil.printUsage(options);
	    WebServerUtil.systemExitWrapper(-1);
	}

	if (cmd.hasOption("help")) {
	    WebServerUtil.printUsage(options);
	    WebServerUtil.systemExitWrapper(-1);
	}

	if (cmd.hasOption("version")) {
	    System.out.println(VersionWrapper.getServerVersion());
	    System.out.println();
	    WebServerUtil.systemExitWrapper(0);
	}

	if (!cmd.hasOption("start") && !cmd.hasOption("stop")) {
	    System.err.println("Missing start or stop option." + " " + SqlTag.PLEASE_CORRECT);
	    System.out.println();
	    WebServerUtil.printUsage(options);
	    WebServerUtil.systemExitWrapper(-1);
	}
	return cmd;
    }

    public boolean isStartCommand() {
        return isStartCommand;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public File getPropertiesFile() {
        return propertiesFile;
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

	/*
	@SuppressWarnings("static-access")
	Option propertiesOption = OptionBuilder.withArgName("file").hasArg().withDescription(propertiesOptionMesssage)
		.create("properties");
		
	@SuppressWarnings("static-access")
	Option hostOption = OptionBuilder.withArgName("hostname").hasArg().withDescription("hostname of the Web server")
		.create("host");

	@SuppressWarnings("static-access")
	Option portOption = OptionBuilder.withArgName("port number").hasArg()
		.withDescription("port number of the Web server. Defaults to " + WebServerApiWrapper.DEFAULT_PORT)
		.create("port");	
	*/
	
	Option propertiesOption  = Option.builder("properties")
                .argName("file")
                .hasArg()
                .desc(propertiesOptionMesssage)
                .build();
	
	Option hostOption  = Option.builder("host")
                .argName("hostname")
                .hasArg()
                .desc("hostname of the Web server")
                .build();
	
	Option portOption  = Option.builder("port")
                .argName("port number")
                .hasArg()
                .desc("port number of the Web server. Defaults to " + WebServerApiWrapper.DEFAULT_PORT)
                .build();

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
     * if ACEQL_HOME is set by calling script, we have a default properties files
     *
     * @return ACEQL_HOME/conf/aceql-server.properties if ACEQL_HOME env var is set,
     *         else null
     */
    private static File getDefaultPropertiesFile() {

	File defaultPropertiesFile = null;

	String aceqlHome = System.getenv("ACEQL_HOME");

	if (aceqlHome != null) {

	    // Remove surrounding " if present
	    aceqlHome = aceqlHome.replaceAll("\"", "");

	    if (aceqlHome.endsWith(File.separator)) {
		aceqlHome = StringUtils.substringBeforeLast(aceqlHome, File.separator);
	    }
	    defaultPropertiesFile = new File(
		    aceqlHome + File.separator + "conf" + File.separator + "aceql-server.properties");
	}

	return defaultPropertiesFile;
    }

}
