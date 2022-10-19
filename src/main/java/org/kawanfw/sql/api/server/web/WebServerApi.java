/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.api.server.web;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.sql.SQLException;

import org.apache.catalina.LifecycleException;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.api.util.webserver.WebServerApiWrapper;

/**
*
* APIs to start and stop the embedded Web Server from a Java program. <br>
* <br>
* Note that the {@code org.kawanfw.sql.WebServer} class is used to start and
* stop the embedded Web Server from command line and uses the APIs of this
* class. Source code is available here:&nbsp; <a href=
* "https://docs.aceql.com/rest/soft/12.0/src/WebServer.java">WebServer.java</a>.
*
* @author Nicolas de Pomereu
*
*/

public class WebServerApi {

    /** The default port to use if parameter is not passed */
    public static final int DEFAULT_PORT = 9090;
    
    private WebServerApiWrapper webServerApiWrapper = new WebServerApiWrapper();

    /**
     * Starts the embedded Web Server.
     *
     * @param host           the host of the Web Server
     * @param port           the port of the Web Server
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
    public void startServer(String host, int port, File propertiesFile)
	    throws ConnectException, IOException, SQLException, DatabaseConfigurationException, LifecycleException {
	
	//WebServerStarterCreator webServerStarterCreator = new WebServerStarterCreator();
	//WebServerStarter webServerStarter = webServerStarterCreator.createInstance();
	//webServerStarter.startServer(webServerApiWrapper, host, port, propertiesFile);
	
	WebServerApiWrapper webServerApiWrapper = new WebServerApiWrapper();
	webServerApiWrapper.startServer(host, port, propertiesFile);
    }

    /**
     * Starts the embedded Web Server on the default port
     * {@link #DEFAULT_PORT}.
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
	startServer(host, WebServerApi.DEFAULT_PORT, propertiesFile);
    }

    /**
     * Stops the embedded Web server running on the default port
     * {@link #DEFAULT_PORT}.
     *
     * @throws IOException if the semaphore file (that signals to the SQL Web Server
     *                     to stop) can not be created
     */
    public void stopServer() throws IOException {
	webServerApiWrapper.stopServer();
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
	webServerApiWrapper.stopServer(port);
    }

    /**
     * Says if the the embedded Web Server is running on on the default port
     * {@link #DEFAULT_PORT}.
     * @return true if the Web Server is running on the default port
     */
    public boolean isServerRunning() {
	return webServerApiWrapper.isServerRunning();
    }

    /**
     * Says if the the embedded Web Server is running on the specified port.
     * @param port           the port of the Web Server
     * @return true if the Web Server is running on the specified port
     */
    public boolean isServerRunning(int port) {
	return webServerApiWrapper.isServerRunning(port);
    }

}

