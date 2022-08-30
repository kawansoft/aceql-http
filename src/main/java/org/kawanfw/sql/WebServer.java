/*
 * Copyright (c)2022 KawanSoft S.A.S.
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-30
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import org.apache.commons.cli.ParseException;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.api.util.webserver.ParametersExtractor;
import org.kawanfw.sql.api.util.webserver.WebServerApiWrapper;
import org.kawanfw.sql.api.util.webserver.WebServerUtil;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.SqlTag;
import org.kawanfw.sql.version.VersionWrapper;


/**
 *
 * AceQL Web Server start and stop with command line.
 *
 * @author Nicolas de Pomereu
 *
 */
public class WebServer {

    private static boolean DEBUG = FrameworkDebug.isSet(WebServer.class);

    /**
     * Constructor
     */
    protected WebServer() {

    }

    /**
     * Starts or stops the AceQL Web Server.
     *
     * @param args the arguments of Web Server start/stop.
     *
     * @throws ParseException                 if any Exception when parsing command
     *                                        line
     * @throws IOException                    if any I/O Exception
     * @throws ConnectException               if server is unable to connect to
     *                                        specified or default 9090 port
     * @throws DatabaseConfigurationException if any error in configuration
     *                                        properties file
     */
    public static void main(String[] args)
	    throws ParseException, IOException, ConnectException, DatabaseConfigurationException {

	if (args.length > 0) {
	    debug("args[0]: " + args[0] + ":");
	}

	if (args.length > 1) {
	    debug("args[1]: " + args[1] + ":");
	}

	ParametersExtractor parametersExtractor = new ParametersExtractor(args);

	if (parametersExtractor.isStartCommand()) {
	    doStart(parametersExtractor);
	} else {
	    doStop(parametersExtractor);
	}
    }

    /**
     * Starts the Web Server.
     * @param parametersExtractor
     */
    private static void doStart(ParametersExtractor parametersExtractor) {
	String host = parametersExtractor.getHost();
	File propertiesFile = parametersExtractor.getPropertiesFile();
	int port = parametersExtractor.getPort();
	
	WebServerApiWrapper webServerApiWrapper = new WebServerApiWrapper();
	try {
	    webServerApiWrapper.startServer(host, port, propertiesFile);
	} catch (IllegalArgumentException e) {
	    System.err.println(
		    SqlTag.SQL_PRODUCT_START_FAILURE + " " + SqlTag.USER_CONFIGURATION + " " + e.getMessage());

	    if (e.getCause() != null) {
		e.getCause().printStackTrace();
	    }

	    System.err.println();
	    WebServerUtil.systemExitWrapper(-1);
	}

	catch (ConnectException e) {
	    System.err.println(SqlTag.SQL_PRODUCT_START_FAILURE + " " + e.getMessage());
	    e.printStackTrace();
	    System.err.println();
	    WebServerUtil.systemExitWrapper(-1);
	} catch (UnknownHostException e) {
	    System.err.println(SqlTag.SQL_PRODUCT_START_FAILURE + " " + "Unknow host: " + e.getMessage());
	    WebServerUtil.printCauseException(e);
	    System.err.println();
	    WebServerUtil.systemExitWrapper(-1);

	} catch (IOException e) {
	    System.err.println(SqlTag.SQL_PRODUCT_START_FAILURE + " " + e.getMessage());
	    WebServerUtil.printCauseException(e);
	    System.err.println();
	    WebServerUtil.systemExitWrapper(-1);
	} catch (Exception e) {
	    System.err.println(SqlTag.SQL_PRODUCT_START_FAILURE);
	    e.printStackTrace();
	    System.err.println();
	    WebServerUtil.systemExitWrapper(-1);
	}
    }

    /**
     * Stops the Web Server
     * @param parametersExtractor
     */
    private static void doStop(ParametersExtractor parametersExtractor) {
	int port = parametersExtractor.getPort();
	WebServerApiWrapper webServerApiWrapper = new WebServerApiWrapper();
	try {
	    webServerApiWrapper.stopServer(port);

	    System.out.println(VersionWrapper.getName() + " Web server running on port " + port + " successfully stopped!");
	    System.out.println();
	    WebServerUtil.systemExitWrapper(0);
	} catch (ConnectException e) {
	    System.err.println(e.getMessage());
	    System.err.println();
	    WebServerUtil.systemExitWrapper(-1);
	} catch (IOException e) {
	    System.err.println("Impossible to stop the SQL Web server running on port " + port);
	    System.err.println(e.getMessage());

	    if (e.getCause() != null) {
		System.err.println("Java Exception Stack Trace:");
		e.printStackTrace();
	    }

	    System.err.println();
	    WebServerUtil.systemExitWrapper(0);
	}
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
