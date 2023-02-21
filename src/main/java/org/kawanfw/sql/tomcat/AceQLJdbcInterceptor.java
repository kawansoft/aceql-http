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
package org.kawanfw.sql.tomcat;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.JdbcInterceptor;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.kawanfw.sql.api.server.connectionstore.ConnectionKey;
import org.kawanfw.sql.servlet.connection.ConnectionStore;
import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesUtil;
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
	if (con != null) {
	    Connection connection = con.getConnection();
	    debug("AceQLJdbcInterceptor. reset call: connection borrowed from pool: " + connection);
	}
	else {
	    debug("AceQLJdbcInterceptor. reset call: connection borrowed from pool. con is null!");	    
	}
    }

    @Override
    public void disconnected(ConnectionPool parent, PooledConnection con, boolean finalizing) {

	try {
	    // No clean of course in stateless mode!
	    if (ConfPropertiesUtil.isStatelessMode()) {
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

		Connection unwrappedStoredConnection = getUnwrappedConnection(entry.getValue());
		
		if (unwrappedStoredConnection != null) {
		    ConnectionKey connectionKey = entry.getKey();
		    
		    debug("AceQLJdbcInterceptor. JdbcInterceptorTest connection.toString(): " + connection.toString());
		    debug("AceQLJdbcInterceptor. unwrappedStoredConnection.toString() : " + unwrappedStoredConnection.toString());
		    
		    if (connection.equals(unwrappedStoredConnection)) {
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
     * @throws SQLException 
     */
    public Connection getUnwrappedConnection(Connection connection) throws SQLException {
	
	// Try to unwrap following https://tomcat.apache.org/tomcat-9.0-doc/jdbc-pool.html.
	// Purpose is to be able to compare the two Connections...
	if (connection instanceof javax.sql.PooledConnection) {
	    Connection actual = ((javax.sql.PooledConnection)connection).getConnection(); 
	    return actual;	    
	}
	else {
	    return connection;
	}

    }

    private void debug(String s) {
	if (DEBUG) {
	    System.err.println(new Date() + " " + s);
	}
    }

}
