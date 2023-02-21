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
