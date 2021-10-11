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
