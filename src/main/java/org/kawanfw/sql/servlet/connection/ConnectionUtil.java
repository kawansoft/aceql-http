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
package org.kawanfw.sql.servlet.connection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 
 * Utilities for Connection
 * 
 * @author Nicolas de Pomereu
 *
 */
public class ConnectionUtil {

    /**
     * Protected constructor
     */
    protected ConnectionUtil() {
    }

    /**
     * Put the Connection in auto-commit mode and in read only false
     * 
     * @param connection
     *            the JDBC Connection to init
     * @throws SQLException
     *             if any SQL Exception occurs
     */
    public static void connectionInit(Connection connection)
	    throws SQLException {

	// Make sure Connection extracted from the pool is always on autocommit
	// mode
	// This avoid for client side to send a connection.getAutoCommit()
	// before
	// starting working.
	// This is anyway mandatory for C# as all Connections are per default
	// auto commit mode.
	if (!connection.getAutoCommit()) {
	    connection.rollback();
	    connection.setAutoCommit(true);
	}

	// Make sure we are not in read only. Don't trap Exception because of
	// Drivers not supporting this call
	if (connection.isReadOnly()) {
	    try {
		connection.setReadOnly(false);
	    } catch (Exception e) {
		// Ignore
		System.err.println(e.toString());
	    }
	}
    }

}
