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
package com.mycompany;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.kawanfw.sql.api.server.auth.UserAuthenticator;

/**
 * UserAuthenticator simple concrete implementation that allows to authenticate
 * users against the ldap://ldap.forumsys.com:389 LDAP server.
 */
public class MyLdapUserAuthenticator implements UserAuthenticator {

    @Override
    public boolean login(String username, char[] password, String database, String ipAddress)
	    throws IOException, SQLException {

	String contextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
	String url = "ldap://ldap.forumsys.com:389";

	// Set up the environment for creating the initial context
	Hashtable<String, String> env = new Hashtable<>();
	env.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
	env.put(Context.PROVIDER_URL, url);

	// The client-side user credentials:
	env.put(Context.SECURITY_PRINCIPAL, username);
	env.put(Context.SECURITY_CREDENTIALS, new String(password));

	DirContext ctx = null;
	try {
	    // If we successfully pass this line, user is authenticated:
	    ctx = new InitialDirContext(env);
	    return true;
	} catch (CommunicationException e) {
	    throw new IOException("Unable to connect to server: " + url);
	} catch (NamingException e) {
	    System.err.println("Unable to authenticate user: " + username);
	    return false;
	} finally {
	    // Safely closes the DirContext
	    MyLdapUserAuthenticatorUtil.closeDirContext(ctx); 
	}
    }

}
