
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
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.api.util.webserver.WebServerApiWrapper;
import org.kawanfw.sql.servlet.injection.classes.WebServerStarter;
import org.kawanfw.sql.servlet.injection.classes.creator.WebServerStarterCreator;

/**
*
* APIs to start and stop the embedded Web Server from a Java program. <br>
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
	
	WebServerStarterCreator webServerStarterCreator = new WebServerStarterCreator();
	WebServerStarter webServerStarter = webServerStarterCreator.createInstance();
	webServerStarter.startServer(webServerApiWrapper, host, port, propertiesFile);
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

