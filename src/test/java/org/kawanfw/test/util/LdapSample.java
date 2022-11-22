/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.test.util;

import java.util.Hashtable;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

/**
 * @author Nicolas de Pomereu
 *
 */
public class LdapSample {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

//	String server = "www.zflexldap.com";
//	int port = 389;
//	String distinguishedName = "cn=ro_admin, ou=sysadmins, dc=zflexsoftware, dc=com"; /* "uid=guest1,ou=users,ou=guests,dc=zflexsoftware,dc=com"; */
//	String password = "guest1password";

	String server = "ldap.forumsys.com";
	int port = 389;
	String distinguishedName = "cn=read-only-admin,dc=example,dc=com";
	String password = "password";

	// Set up the environment for creating the initial context
	Hashtable<String, String> env = new Hashtable<>();
	env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, "ldap://" + server + ":" + port);

	// Authenticate
	//env.put(Context.SECURITY_AUTHENTICATION, "simple");
	env.put(Context.SECURITY_PRINCIPAL, distinguishedName);
	env.put(Context.SECURITY_CREDENTIALS, password);

	// Create the initial context
	DirContext ctx = null;

	try {
	    // If we pass this, we are authenticated
	    ctx = new InitialDirContext(env);
	    //System.out.println(ctx.getEnvironment());
	} catch (CommunicationException e) {
	    throw new CommunicationException("Impossible to connect to server: " + server);
	} catch (NamingException e) {
	    throw new NamingException("Unable to authenticate user: " + distinguishedName);
	} finally {
	    if (ctx != null) {
		ctx.close();
	    }

	}

	System.out.println("DONE!");
    }

}
