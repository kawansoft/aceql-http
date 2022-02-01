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
package org.kawanfw.sql.api.server.web;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.sql.SQLException;

import org.apache.catalina.LifecycleException;
import org.apache.commons.lang3.SystemUtils;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.tomcat.TomcatStarter;
import org.kawanfw.sql.tomcat.TomcatStarterUtil;
import org.kawanfw.sql.tomcat.util.PortSemaphoreFile;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.SqlTag;
import org.kawanfw.sql.version.VersionWrapper;

/**
 *
 * APIs to start and stop the embedded Web Server from a Java program. <br>
 * <br>
 * Note that the {@code org.kawanfw.sql.WebServer} class is used to start and
 * stop the embedded Web Server from command line and uses the APIs of this
 * class. Source code is available here:&nbsp; <a href=
 * "https://docs.aceql.com/rest/soft/10.1/src/WebServer.java">WebServer.java</a>.
 *
 * @author Nicolas de Pomereu
 *
 */
public class WebServerApi {

    private static String CR_LF = System.getProperty("line.separator");

    /** The default port to use if parameter is not passed */
    public static final int DEFAULT_PORT = 9090;

    private static boolean DEBUG = FrameworkDebug.isSet(WebServerApi.class);

    /**
     * Starts the embedded Web Server.
     *
     * @param host           the host of the Web Server
     * @param port           the port of the Web Server
     * @param propertiesFile properties file to use for configuration of the Web
     *                       Server
     *
     * @throws ConnectException               if the port is not available
     * @throws IOException                    if an IOException occurs
     * @throws DatabaseConfigurationException if there is a configuration error,
     *                                        either in Configurators or in the
     *                                        <code>server-sql.properties</code>
     *                                        file
     * @throws LifecycleException             thrown by the embedded Tomcat engine
     *                                        for any lifecycle related problem
     * @throws SQLException if any SQL error occurs
     */
    public void startServer(String host, int port, File propertiesFile)
	    throws ConnectException, IOException, DatabaseConfigurationException, LifecycleException, SQLException {

	debug("propertiesFiles: " + propertiesFile);

	if (host == null) {
	    throw new DatabaseConfigurationException("host parameter can not be null.");
	}

	if (port <= 0) {
	    throw new DatabaseConfigurationException("port parameter can not be null.");
	}

	if (propertiesFile == null) {
	    throw new DatabaseConfigurationException("propertiesFile parameter can not be null.");
	}

	if (!propertiesFile.exists()) {
	    throw new DatabaseConfigurationException(
		    "The properties file " + propertiesFile + " does not exists. " + SqlTag.PLEASE_CORRECT);
	}

//	if (PropertiesFileStore.get() != null) {
//	    throw new IllegalArgumentException("AceQL is already started in this JVM. Cant not start a second AceQL instance in the same JVM.");
//	}


	if (!TomcatStarterUtil.available(port)) {
	    throw new ConnectException(
		    "The port " + port + " is not available for starting Web server. " + SqlTag.PLEASE_CORRECT);
	}

	PortSemaphoreFile portSemaphoreFile = new PortSemaphoreFile(port);

	try {
	    if (!portSemaphoreFile.exists()) {
		portSemaphoreFile.create();
	    }
	} catch (IOException e) {
	    throw new IOException("Web server can not start. Impossible to create the semaphore file: "
		    + portSemaphoreFile.getSemaphoreFile() + CR_LF
		    + "Create manually the semapahore file to start the Web server on port " + port + ".", e);
	}

	// Do not use SecureRandom class
	if (SystemUtils.IS_OS_UNIX) {
	    System.setProperty("java.security.egd", "file:/dev/./urandom");
	    debug("java.security.egd: " + System.getProperty("java.security.egd"));
	}

	// OK build the Servlet
	TomcatStarter tomcatStarter = new TomcatStarter(host, port, propertiesFile);
	tomcatStarter.startTomcat();
    }

    /**
     * Starts the embedded Web Server on the default port
     * {@link WebServerApi#DEFAULT_PORT}.
     *
     * @param host           the host of the Web Server
     * @param propertiesFile properties file to use for configuration of the Web
     *                       Server
     *
     * @throws ConnectException               if the default port is not available
     * @throws IOException                    if an IOException occurs
     * @throws DatabaseConfigurationException if there is a configuration error,
     *                                        either in Configurators or in the
     *                                        <code>server-sql.properties</code>
     *                                        file
     * @throws LifecycleException             thrown by embedded Tomcat for any
     *                                        lifecycle related problem
     * @throws SQLException 		      thrown by embedded Tomcat for any SQL Error
     */
    public void startServer(String host, File propertiesFile)
	    throws ConnectException, IOException, DatabaseConfigurationException, LifecycleException, SQLException {
	startServer(host, DEFAULT_PORT, propertiesFile);
    }

    /**
     * Stops the embedded Web server running on the default port
     * {@link WebServerApi#DEFAULT_PORT}.
     *
     * @throws IOException if the semaphore file (that signals to the SQL Web Server
     *                     to stop) can not be created
     */
    public void stopServer() throws IOException {

	stopServer(DEFAULT_PORT);
    }

    /**
     * Stops the embedded Web server running on the designated port.
     *
     * @param port the port on which the SQL Web server is running
     *
     * @throws IOException if the semaphore file (that signals to the Web Server to
     *                     stop) can not be created
     */
    public void stopServer(int port) throws IOException {

	PortSemaphoreFile portSemaphoreFile = new PortSemaphoreFile(port);

	if (!portSemaphoreFile.exists()) {
	    throw new ConnectException(
		    "WARNING! There is no " + VersionWrapper.getName() + " Web server running on port " + port);
	}

	// Always Force the deletion of the semaphore file:
	try {
	    portSemaphoreFile.delete();
	} catch (IOException ioe) {
	    throw new IOException("Can not stop the Web server. Please delete manually the semaphore file "
		    + portSemaphoreFile.getSemaphoreFile() + " and then retry. ", ioe);
	}

	if (TomcatStarterUtil.available(port)) {
	    throw new ConnectException("WARNING! There is no SQL Web server running on port " + port);
	}

    }

    /**
     * Says if the the embedded Web Server is running on on the default port
     * {@link WebServerApi#DEFAULT_PORT}.
     * @return true if the Web Server is running on the default port
     */
    public boolean isServerRunning() {
	PortSemaphoreFile portSemaphoreFile = new PortSemaphoreFile(WebServerApi.DEFAULT_PORT);
	return portSemaphoreFile.exists();
    }

    /**
     * Says if the the embedded Web Server is running on the specified port.
     * @param port           the port of the Web Server
     * @return true if the Web Server is running on the specified port
     */
    public boolean isServerRunning(int port) {
	PortSemaphoreFile portSemaphoreFile = new PortSemaphoreFile(port);
	return portSemaphoreFile.exists();
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
