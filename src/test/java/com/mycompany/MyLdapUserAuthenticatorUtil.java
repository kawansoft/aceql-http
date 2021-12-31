package com.mycompany;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

public class MyLdapUserAuthenticatorUtil {

    /**
     * Closes the DirContext
     * 
     * @param ctx the DirContext to close
     */
    public static void closeDirContext(DirContext ctx) {
	try {
	    ctx.close();
	} catch (NamingException e) {
	    System.err.println("InitialDirContext.close() Exception: " + e);
	}
    }
    
}
