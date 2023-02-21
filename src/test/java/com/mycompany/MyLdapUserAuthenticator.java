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
