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

package org.kawanfw.sql.tomcat;

import java.sql.Connection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.JdbcInterceptor;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.kawanfw.sql.api.server.connectionstore.ConnectionKey;
import org.kawanfw.sql.servlet.connection.ConnectionStore;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * Allows to clean our ConnectionStore when a Connection is removed by Tomcat
 * JDBC Pool...
 * 
 * @author Nicolas de Pomereu
 *
 */
public class AceQLJdbcInterceptor extends JdbcInterceptor {

    public static boolean DEBUG = FrameworkDebug.isSet(AceQLJdbcInterceptor.class);

    /**
     * Constructor. Just for debug.
     */
    public AceQLJdbcInterceptor() {
	debug("AceQLJdbcInterceptor instance creation.");
    }

    @Override
    public void reset(ConnectionPool parent, PooledConnection con) {
	// We do nothing. Just to implement as it is mandatory
    }

    @Override
    public void disconnected(ConnectionPool parent, PooledConnection con, boolean finalizing) {

	try {
	    // No clean of course in stateless mode!
	    if (ServletParametersStore.isStatelessMode()) {
		debug("AceQLJdbcInterceptor. Stateless mode. Nothing to do.");
		return;
	    }

	    if (con == null) {
		debug("Can not intercept Connection to use for cleaning our ConnectionStore: PooledConnection is null!");
	    }

	    debug("AceQLJdbcInterceptor. Clean ConnectionStore for intercepted connection...");
	    Connection connection = con.getConnection();

	    if (connection == null) {
		debug("AceQLJdbcInterceptor. Current Connection is null! Nothing to do.");
		return;
	    }

	    // private static Map<ConnectionKey, Connection> connectionMap
	    Map<ConnectionKey, Connection> map = ConnectionStore.getConnectionMap();
	    Set<ConnectionKey> set = map.keySet();

	    if (set == null) {
		debug("AceQLJdbcInterceptor. ConnectionStore Set<ConnectionKey> is null. Nothing to do.");
		return;
	    }

	    if (set.isEmpty()) {
		debug("AceQLJdbcInterceptor. ConnectionStore Set<ConnectionKey> is empty. Nothing to do.");
		return;
	    }

	    for (Map.Entry<ConnectionKey, Connection> entry : map.entrySet()) {

		Connection unwrappedConnection = getUnwrappedConnection(entry.getValue());
		
		if (unwrappedConnection != null) {
		    ConnectionKey connectionKey = entry.getKey();
		    
		    debug("AceQLJdbcInterceptor. connection.toString()         : " + connection.toString());
		    debug("AceQLJdbcInterceptor. unwrappedConnection.toString(): " + unwrappedConnection.toString());
		    
		    if (connection.toString().equals(unwrappedConnection.toString())) {
			ConnectionStore.remove(connectionKey);
			debug("AceQLJdbcInterceptor. ConnectionStore all removed for connectionKey: " + connectionKey);
		    } else {
			debug("AceQLJdbcInterceptor. Current connection does not correspond to connectionKey: "
				+ connectionKey);
		    }
		} else {
		    debug("AceQLJdbcInterceptor. connectionKey is null!");
		}
	    }

	} catch (Exception e) {
	    // NO EXCEPTION THROW ALLOWED!
	    System.err.println(new Date() + " AceQLJdbcInterceptor Failure:");
	    e.printStackTrace();
	}

    }

    /**
     * Unwraps the Connection if it's a PooledConnection
     * @param connection the wrapped Connection
     * @return the unwrapped Connection
     */
    public Connection getUnwrappedConnection(Connection connection) {
	
	// Try to unwrap following https://tomcat.apache.org/tomcat-9.0-doc/jdbc-pool.html.
	try {
	    Connection actual = ((javax.sql.PooledConnection)connection).getConnection(); 
	    return actual;
	}
	catch (Exception e) {
	   return connection;
	}

    }

    private void debug(String s) {
	if (DEBUG) {
	    System.err.println(new Date() + " " + s);
	}
    }

}
