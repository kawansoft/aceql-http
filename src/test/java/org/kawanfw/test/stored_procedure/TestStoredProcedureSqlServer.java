/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.test.stored_procedure;

import java.lang.reflect.Constructor;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.kawanfw.test.parms.ConnectionLoaderJdbcInfo;

/**
 * @author Nicolas de Pomereu
 *
 */
public class TestStoredProcedureSqlServer {
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	ConnectionLoaderJdbcInfo connectionLoaderJdbcInfo = new ConnectionLoaderJdbcInfo("SQL Server-ms");
	String driverClassName = connectionLoaderJdbcInfo.getDriverClassName();
	String url = connectionLoaderJdbcInfo.getUrl();
	
	Class<?> c = Class.forName(driverClassName);
	Constructor<?> constructor = c.getConstructor();
	Driver driver = (Driver) constructor.newInstance();
		
	String username = "user1";
	String password = "password1";
	
	Properties properties = new Properties();
	properties.put("user", username);
	properties.put("password", password);
	Connection connection = driver.connect(url, properties);

	if (connection == null) {
	    System.err.println("driverClassName: " + driverClassName);
	    System.err.println("url            : " + url);
	    throw new IllegalArgumentException(
		    "Connection is null after driver.connect(url, properties)!");
	}
	
	DatabaseMetaData data = connection.getMetaData();
	System.out.println("Db Engine: " + data.getDatabaseProductName());
	System.out.println();
	
	TestStoredProcedureCommons.selectCustomerExecute(connection);
	testStoredProcedureSelectCustomer(connection);
	testStoredProcedureInOut(connection);
    }
    

    public static void testStoredProcedureSelectCustomer(Connection connection) throws SQLException {
	
	// Calling the dbo.spSelectCustomer stored procedure.
	// Native JDBC syntax using a SQL Server JDBC Driver:
	CallableStatement callableStatement 
		= connection.prepareCall("{ call dbo.spSelectCustomer(?, ?) }");
	callableStatement.setInt(1, 2);
	callableStatement.setString(2, "Doe3");
	ResultSet rs = callableStatement.executeQuery();
	
	System.out.println();
	while (rs.next()) {
	    System.out.println(rs.getInt(1) + " "+ rs.getString(2));
	}

	callableStatement.close();

	System.out.println("Done dbo.spSelectCustomer!");
	System.out.println();

    }
    
    public static void testStoredProcedureInOut(Connection connection) throws SQLException {
	
	System.out.println();
		
	// Calling the dbo.spInOut stored procedure.
	// Native JDBC syntax using a SQL Server  Driver:
	CallableStatement callableStatement 
		= connection.prepareCall("{ call dbo.spInOut(?, ?, ?)  }");
	callableStatement.setInt(1, 4);
	callableStatement.setInt(2, 3);
	callableStatement.registerOutParameter(2, java.sql.Types.INTEGER);
	callableStatement.setString(3, "Meaning of life is:");
	callableStatement.registerOutParameter(3, java.sql.Types.VARCHAR);
	@SuppressWarnings("unused")
	int n = callableStatement.executeUpdate();
	
	int out2 = callableStatement.getInt(2);
	System.out.println("out2: " + out2);
	String out3 = callableStatement.getString(3);
	System.out.println("out3: " + out3);
	
	callableStatement.close();
	
	System.out.println("Done ORACLE_IN_OUT_2!");
	System.out.println();
    }
    
 

}
