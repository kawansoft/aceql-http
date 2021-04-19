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
import java.util.Hashtable;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.kawanfw.sql.api.server.DefaultDatabaseConfigurator;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.tomcat.TomcatStarterUtilProperties;
import org.kawanfw.sql.util.Tag;

/**
 * A concrete {@link UserAuthenticator} that allows zero-code remote client
 * {@code (username, password)} authentication against a LDAP server. <br>
 * <br>
 * The LDAP server that authenticates the users is defined in the
 * {@code ldapUserAuthenticator.url} property in the
 * {@code aceql-server.properties} file.
 *
 * @author Nicolas de Pomereu
 * @since 5.0
 */
public class LdapUserAuthenticator implements UserAuthenticator {

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
	    File file = ServerSqlManager.getAceqlServerProperties();
	    properties = TomcatStarterUtilProperties.getProperties(file);
	}

	String url = properties.getProperty("ldapUserAuthenticator.url");
	Objects.requireNonNull(url, getInitTag() + "The ldapUserAuthenticator.url property cannot be null!");

	// Set up the environment for creating the initial context
	Hashtable<String, String> env = new Hashtable<>();
	env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, url);

	// Authenticate
	// env.put(Context.SECURITY_AUTHENTICATION, "simple");
	env.put(Context.SECURITY_PRINCIPAL, username);
	env.put(Context.SECURITY_CREDENTIALS, new String(password));

	// Create the initial context
	DirContext ctx = null;

	if (logger == null) {
	    logger = new DefaultDatabaseConfigurator().getLogger();
	}

	try {
	    // If we pass this, we are authenticated
	    ctx = new InitialDirContext(env);
	    // System.out.println(ctx.getEnvironment());
	} catch (CommunicationException e) {
	    throw new IOException(getInitTag() + "Impossible to connect to server: " + url);
	} catch (NamingException e) {
	    logger.log(Level.WARNING,
		    getInitTag() + LdapUserAuthenticator.class.getName() + " Unable to authenticate user: " + username);
	    return false;
	} finally {
	    if (ctx != null) {
		try {
		    ctx.close();
		} catch (NamingException e) {
		    logger.log(Level.WARNING, getInitTag() + LdapUserAuthenticator.class.getName()
			    + " InitialDirContext.close() Exception: " + e);
		}
	    }
	}

	return true;

    }

    /**
     * @return the beginning of the log line
     */
    private String getInitTag() {
	return Tag.PRODUCT + " " + LdapUserAuthenticator.class.getSimpleName() + ": ";
    }

}
