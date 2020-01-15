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

    /**
     * 
     */
    public TestTomcatJdbcPoolBehavior() {
	// TODO Auto-generated constructor stub
    }

    public static void createPool() throws Exception {
	// Modify this code in order to extract a Connection from your
	// connection pooling system:

	// Driver parameters to use:
	String driverClassName = "org.postgresql.Driver";
	String url = "jdbc:postgresql://localhost:5432/kawansoft_example";
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
