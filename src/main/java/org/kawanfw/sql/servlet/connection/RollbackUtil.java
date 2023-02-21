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
package org.kawanfw.sql.servlet.connection;

import java.sql.Connection;

/**
 * @author Nicolas de Pomereu
 *
 */
public class RollbackUtil {

    public static void rollback(Connection connection) {
	
	if (connection == null) {
	    return;
	}
	
	try {
	    if (! connection.getAutoCommit()) {
	        connection.rollback();
	    }
	} catch (Exception e) {
	    System.out.println("RollbackUtil Exception thrown:");
	    e.printStackTrace(System.out);
	}
    }

}
