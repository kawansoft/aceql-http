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
package org.kawanfw.test.api.server;

import java.sql.Connection;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 * @author Nicolas de Pomereu
 *
 */
public class TestTomcatJdbcPoolBehavior {

    /** The data source to use for connection pooling */
    private static DataSource dataSource = null;

    public static void createPool() throws Exception {
	// Modify this code in order to extract a Connection from your
	// connection pooling system:

	// Driver parameters to use:
	String driverClassName = "org.postgresql.Driver";
	String url = "jdbc:postgresql://localhost:5432/sampledb";
	String username = "user1";
	String password = "password1";

	// Creating the DataSource bean and populating the values:
	// Mandatory values to set
	PoolProperties p = new PoolProperties();
	p.setDriverClassName(driverClassName);
	p.setUrl(url);
	p.setUsername(username);
	p.setPassword(password);

	// Other possible values to set
	p.setTestOnBorrow(true);
	p.setValidationQuery("SELECT 1");
	p.setTestOnReturn(false);
	p.setValidationInterval(30000);
	p.setTimeBetweenEvictionRunsMillis(30000);
	p.setMaxActive(2);
	p.setInitialSize(2);
	p.setMaxWait(10000);
	p.setRemoveAbandonedTimeout(60);
	p.setMinEvictableIdleTimeMillis(30000);
	p.setMinIdle(2);
	p.setMaxIdle(2);
	p.setLogAbandoned(true);
	p.setRemoveAbandoned(true);
	p.setJdbcInterceptors("ConnectionState;StatementFinalizer");

	dataSource = new DataSource();
	dataSource.setPoolProperties(p);

    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

	createPool();

	Connection con1 = dataSource.getConnection();
	Connection con2 = dataSource.getConnection();

	System.out.println("getActive() : " + dataSource.getActive());

	System.out.println("con1.isReadOnly: " + con1.isReadOnly());
	System.out.println("con2.isReadOnly: " + con1.isReadOnly());

	System.out.println("getActive() : " + dataSource.getActive());
	System.out.println();

	con1.setReadOnly(true);
	con2.setReadOnly(true);

	System.out.println("con1.isReadOnly: " + con1.isReadOnly());
	System.out.println("con2.isReadOnly: " + con1.isReadOnly());

	con1.close();
	System.out.println("getActive() : " + dataSource.getActive());
	con2.close();

	System.out.println();
	System.out.println("after close");
	System.out.println();

	Connection con3 = dataSource.getConnection();
	Connection con4 = dataSource.getConnection();

	System.out.println("con3.isReadOnly: " + con3.isReadOnly());
	System.out.println("con4.isReadOnly: " + con4.isReadOnly());
	System.out.println("getActive() : " + dataSource.getActive());
    }

}
