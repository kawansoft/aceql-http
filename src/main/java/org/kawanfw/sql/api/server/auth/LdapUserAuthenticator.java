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
import java.util.Hashtable;
import java.util.Objects;
import java.util.Properties;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.servlet.injection.classes.InjectedClassesStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileUtil;
import org.kawanfw.sql.util.Tag;
import org.slf4j.Logger;

/**
 * A concrete {@code UserAuthenticator} that allows zero-code remote client
 * {@code (username, password)} authentication against a LDAP server. <br>
 * <br>
 * The LDAP server that authenticates the users is defined in the
 * {@code ldapUserAuthenticator.url} property in the
 * {@code aceql-server.properties} file.
 *
 * @see UserAuthenticator
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
	    File file = PropertiesFileStore.get();
	    properties = PropertiesFileUtil.getProperties(file);
	}

	String url = properties.getProperty("ldapUserAuthenticator.url");
	Objects.requireNonNull(url, getInitTag() + "The ldapUserAuthenticator.url property cannot be null!");
	
	String securityAuth = properties.getProperty("ldapUserAuthenticator.securityAuthentication");
	String securityProtocol = properties.getProperty("ldapUserAuthenticator.securityProtocol");
	
	Hashtable<String, String> env = buildHashtable(username, password, url, securityAuth, securityProtocol);
		
	// Create the initial context
	DirContext ctx = null;

	if (logger == null) {
	    DatabaseConfigurator databaseConfigurator = InjectedClassesStore.get().getDatabaseConfigurators()
		    .get(database);
	    logger = databaseConfigurator.getLogger();
	}

	try {
	    // If we pass this, we are authenticated
	    ctx = new InitialDirContext(env);
	    // System.out.println(ctx.getEnvironment());
	} catch (CommunicationException e) {
	    throw new IOException(getInitTag() + "Impossible to connect to server: " + url);
	} catch (NamingException e) {
	    logger.info(
		    getInitTag() + LdapUserAuthenticator.class.getName() + " Unable to authenticate user: " + username);
	    return false;
	} finally {
	    if (ctx != null) {
		try {
		    ctx.close();
		} catch (NamingException e) {
		    logger.info(getInitTag() + LdapUserAuthenticator.class.getName()
			    + " InitialDirContext.close() Exception: " + e);
		}
	    }
	}

	return true;

    }

    /** 
     * Builds the Hashtable of the elements of the environment
     * @param username	the LDAP username
     * @param password	the LDAP password
     * @param url	the LDAP server URL 
     * @param securityAuth	the security level to use
     * @param securityProtocol	the security protocol to use
     * @return the built Hashtable
     */
    private Hashtable<String, String> buildHashtable(String username, char[] password, String url, String securityAuth,
	    String securityProtocol) {
	// Set up the environment for creating the initial context
	Hashtable<String, String> env = new Hashtable<>();
	env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, url);

	if (securityAuth != null && ! securityAuth.isEmpty()) {
	    env.put(Context.SECURITY_AUTHENTICATION, securityAuth);
	}
	if (securityProtocol != null && ! securityProtocol.isEmpty()) {
	    env.put(Context.SECURITY_PROTOCOL, securityProtocol);
	}

	// Authenticate
	env.put(Context.SECURITY_PRINCIPAL, username);
	env.put(Context.SECURITY_CREDENTIALS, new String(password));
	return env;
    }

    /**
     * @return the beginning of the log line
     */
    private String getInitTag() {
	return Tag.PRODUCT + " " + LdapUserAuthenticator.class.getSimpleName() + ": ";
    }

}
