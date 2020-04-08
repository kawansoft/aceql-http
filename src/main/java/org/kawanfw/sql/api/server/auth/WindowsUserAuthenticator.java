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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kawanfw.sql.api.server.DefaultDatabaseConfigurator;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.tomcat.TomcatStarterUtil;
import org.kawanfw.sql.util.Tag;

import waffle.windows.auth.impl.WindowsAuthProviderImpl;


/**
 * A concrete {@code UserAuthenticator} that allows zero-code remote
 * client {@code (username, password)} authentication against the Windows
 * machine on which the AceQL instance is running.
 *
 * @author Nicolas de Pomereu
 * @since 5.0
 *
 */
public class WindowsUserAuthenticator implements UserAuthenticator {

    private Logger logger = null;
    private Properties properties = null;

    /**
     * Constructor. {@code UserAuthenticator} implementation must have no
     * constructor or a unique no parameters constructor.
     */
    public WindowsUserAuthenticator() {

    }

    /* (non-Javadoc)
     * @see org.kawanfw.sql.api.server.auth.UserAuthenticator#login(java.lang.String, char[], java.lang.String, java.lang.String)
     */
    @Override
    public boolean login(String username, char[] password, String database, String ipAddress)
	    throws IOException, SQLException {

	if (properties == null) {
	    File file = ServerSqlManager.getAceqlServerProperties();
	    properties = TomcatStarterUtil.getProperties(file);
	}

	String domain = properties.getProperty("windowsUserAuthenticator.domain");

	try {
	    WindowsAuthProviderImpl windowsAuthProviderImpl = new WindowsAuthProviderImpl();
	    windowsAuthProviderImpl.logonDomainUser(username, domain, new String(password));
	    return true;
	} catch (Exception exception) {

	    if (logger == null) {
		logger = new DefaultDatabaseConfigurator().getLogger();
	    }

	    if (exception instanceof com.sun.jna.platform.win32.Win32Exception) {
		logger.log(Level.WARNING, getInitTag() + "WindowsLogin.login refused for " + username);
	    } else {
		// Better to trace stack trace in case of Waffle problem...
		logger.log(Level.WARNING, getInitTag() + "AceQL WindowsLogin.login call failure (Waffle Library): " + exception.toString());
	    }

	    return false;
	}

    }

    /**
     * @return the beginning of the log line
     */
    private String getInitTag() {
	return Tag.PRODUCT + " " + WindowsUserAuthenticator.class.getSimpleName() + ": ";
    }
}
