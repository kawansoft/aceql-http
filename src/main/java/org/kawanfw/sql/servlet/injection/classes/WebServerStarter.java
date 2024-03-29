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
package org.kawanfw.sql.servlet.injection.classes;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.sql.SQLException;

import org.apache.catalina.LifecycleException;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.api.util.webserver.WebServerApiWrapper;

/**
 * @author Nicolas de Pomereu
 *
 */
public interface WebServerStarter {

    public void startServer(WebServerApiWrapper webServerApiWrapper, String host, int port, File propertiesFile)
	    throws ConnectException, DatabaseConfigurationException, IOException, SQLException, LifecycleException;

}
