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

import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.JdbcInterceptor;
import org.apache.tomcat.jdbc.pool.PooledConnection;

/**
 * Simple test JdbcInterceptor to be sure it is loaded along user defined in  aceql-server.properties
 * @author Nicolas de Pomereu
 *
 */
public class JdbcInterceptorTest extends JdbcInterceptor {

    public JdbcInterceptorTest() {
	System.err.println("JdbcInterceptorTest Creation.");
    }

    @Override
    public void reset(ConnectionPool parent, PooledConnection con) {
	System.err.println("JdbcInterceptorTest reset call.");	
    }

}
