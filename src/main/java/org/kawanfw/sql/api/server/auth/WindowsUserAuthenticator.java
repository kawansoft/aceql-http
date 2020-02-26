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

package org.kawanfw.sql.api.server.auth;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import org.kawanfw.sql.api.server.util.WindowsLogin;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.tomcat.TomcatStarterUtil;

/**
 * A concrete {@code UserAuthenticator} that extends allows zero-code remote
 * client {@code (username, password)} authentication against a Web Service.
 *
 * @author Nicolas de Pomereu
 *
 */
public class WindowsUserAuthenticator implements UserAuthenticator {

    private Properties properties = null;

    /**
     * Constructor. {@code UserAuthenticator} implementation must have no
     * constructor or a unique no parameters constructor.
     */
    public WindowsUserAuthenticator() {

    }

    @Override
    public boolean login(String username, char[] password, String database, String ipAddress)
	    throws IOException, SQLException {

	if (properties == null) {
	    File file = ServerSqlManager.getAceqlServerProperties();
	    properties = TomcatStarterUtil.getProperties(file);
	}

	String domain = properties.getProperty("windowsUserAuthenticator.domain");

	boolean authenticated = WindowsLogin.login(username, domain, new String(password));
	return authenticated;
    }
}
