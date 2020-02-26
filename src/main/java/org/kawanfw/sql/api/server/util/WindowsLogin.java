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
package org.kawanfw.sql.api.server.util;

import org.kawanfw.sql.api.server.auth.UserAuthenticator;

import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 *
 * This class provides static methods for Windows authentication to be used
 * directly in {@link UserAuthenticator#login(String, char[], String, String)}
 * implementations.
 *
 * @see org.kawanfw.sql.api.server.auth.WindowsUserAuthenticator
 *
 * @author Nicolas de Pomereu
 */
public class WindowsLogin {

    /** In order to activate debug on Waffle library */
    public static boolean DEBUG = false;

    /**
     * Authenticates the passed username & password against the Windows machine on
     * which is running the current AceQL instance.
     *
     * @param username the user name for authentication
     * @param domain   the domain to use, may be null.
     * @param password the password for authentication
     * @return {@code true} if the user is authenticated on the Windows machine on
     *         which is running the current AceQL instance, else {@code false}
     */
    public static boolean login(String username, String domain, String password) {
	try {
	    WindowsAuthProviderImpl windowsAuthProviderImpl = new WindowsAuthProviderImpl();
	    windowsAuthProviderImpl.logonDomainUser(username, domain, password);
	    return true;
	} catch (Exception exception) {
	    if (exception instanceof com.sun.jna.platform.win32.Win32Exception) {
		if (DEBUG) {
		    System.err.println("WindowsLogin.login refused for " + username);
		}

	    } else {
		// Better to trace stack trace in case of Waffle problem...
		System.err.println("AceQL WindowsLogin.login call failure (Waffle Library): ");
		exception.printStackTrace();
	    }

	    return false;
	}
    }

}
