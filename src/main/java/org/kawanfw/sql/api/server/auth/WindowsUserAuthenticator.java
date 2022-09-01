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
package org.kawanfw.sql.api.server.auth;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kawanfw.sql.api.server.DefaultDatabaseConfigurator;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileUtil;
import org.kawanfw.sql.util.Tag;

import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * A concrete {@code UserAuthenticator} that allows zero-code remote client
 * {@code (username, password)} authentication against the Windows machine on
 * which the AceQL instance is running. <br>
 * (There a no mandatory properties to define in the
 * {@code aceql-server.properties} file.)
 *
 * @see UserAuthenticator
 * @author Nicolas de Pomereu
 * @since 5.0
 *
 */
public class WindowsUserAuthenticator implements UserAuthenticator {

    private Logger logger = null;
    private Properties properties = null;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.kawanfw.sql.api.server.auth.UserAuthenticator#login(java.lang.String,
     * char[], java.lang.String, java.lang.String)
     */
    @Override
    public boolean login(String username, char[] password, String database, String ipAddress)
	    throws IOException, SQLException {

	if (properties == null) {
	    File file = PropertiesFileStore.get();
	    properties = PropertiesFileUtil.getProperties(file);
	}

	String domain = properties.getProperty("windowsUserAuthenticator.domain");

	try {
	    WindowsAuthProviderImpl windowsAuthProviderImpl = new WindowsAuthProviderImpl();
	    windowsAuthProviderImpl.logonDomainUser(username, domain, new String(password));
	    return true;
	} catch (com.sun.jna.platform.win32.Win32Exception Wwn32Exception) {
	    if (logger == null) {
		logger = new DefaultDatabaseConfigurator().getLogger();
	    }
	    logger.log(Level.WARNING, getInitTag() + "WindowsLogin.login refused for " + username);

	    return false;

	} catch (Exception exception) {

	    if (logger == null) {
		logger = new DefaultDatabaseConfigurator().getLogger();
	    }

	    // Better to trace stack trace in case of Waffle problem...
	    logger.log(Level.WARNING,
		    getInitTag() + "AceQL WindowsLogin.login call failure (Waffle Library): " + exception.toString());

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
