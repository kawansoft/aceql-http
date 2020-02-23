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

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.api.server.UserAuthenticator;
import org.kawanfw.sql.api.server.util.SshLogin;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.tomcat.TomcatStarterUtil;

/**
 * A concrete {@code UserAuthenticator} that extends allows zero-code remote
 * client {@code (username, password)} authentication against a SSH server.
 *
 * @author Nicolas de Pomereu
 *
 */
public class SshUserAuthenticator implements UserAuthenticator {

    private Properties properties = null;

    /**
     * Constructor. {@code UserAuthenticator} implementation must have no
     * constructor or a unique no parameters constructor.
     */
    public SshUserAuthenticator() {

    }

    @Override
    public boolean login(String username, char[] password, String database, String ipAddress)
	    throws IOException, SQLException {

	if (properties == null) {
	    File file = ServerSqlManager.getAceqlServerProperties();
	    properties = TomcatStarterUtil.getProperties(file);
	}

	String host = properties.getProperty("sshUserAuthenticator.host");
	String portStr = properties.getProperty("sshUserAuthenticator.port");

	if (host == null) {
	    throw new NullPointerException("The sshUserAuthenticator.host property is null!");
	}

	if (portStr == null) {
	    portStr = "22";
	}

	if (!StringUtils.isNumeric(portStr)) {
	    throw new IllegalArgumentException("The sshUserAuthenticator.port property is not numeric: " + portStr);
	}

	int port = Integer.parseInt(portStr);

	boolean authenticated = SshLogin.login(host, port, username, password);
	return authenticated;
    }
}
