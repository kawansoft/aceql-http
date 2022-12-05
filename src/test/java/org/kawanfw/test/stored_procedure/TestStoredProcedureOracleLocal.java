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

import oracle.jdbc.OracleTypes;

/**
 * @author Nicolas de Pomereu
 *
 */
public class TestStoredProcedureOracleLocal {
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	ConnectionLoaderJdbcInfo connectionLoaderJdbcInfo = new ConnectionLoaderJdbcInfo("Oracle");
	String driverClassName = connectionLoaderJdbcInfo.getDriverClassName();
	String url = connectionLoaderJdbcInfo.getUrl();
	
	Class<?> c = Class.forName(driverClassName);
	Constructor<?> constructor = c.getConstructor();
	Driver driver = (Driver) constructor.newInstance();
	
	//String username = "sys as sysdba";
	//String password = "327qm9y4";
	
	String username = "C##_KAWAN";
	String password = "327qm9y4";
	
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
	    
	TestStoredProcedureCommons.selectCustomerExecute(connection);
	testStoredProcedureSelectCustomer_2(connection);
	testStoredProcedureInOut_2(connection);
    }
        
    public static void testStoredProcedureSelectCustomer(Connection connection) throws SQLException {
	
	// Calling the ORACLE_SELECT_CUSTOMER stored procedure.
	// Native Oracle JDBC syntax using an Oracle JDBC Driver:
	CallableStatement callableStatement 
		= connection.prepareCall("{ call ORACLE_SELECT_CUSTOMER(?, ?) }");
	callableStatement.setInt(1, 2);
	callableStatement.registerOutParameter(2, OracleTypes.CURSOR);
	callableStatement.executeQuery();
	
	ResultSet rs= (ResultSet) callableStatement.getObject(2);
	
	while (rs.next()) {
	    System.out.println(rs.getInt(1));
	}

	callableStatement.close();

	System.out.println("Done ORACLE_SELECT_CUSTOMER!");
	System.out.println();

    }
    
    public static void testStoredProcedureSelectCustomer_2(Connection connection) throws SQLException {
	
	// Calling the ORACLE_SELECT_CUSTOMER stored procedure.
	// Native Oracle JDBC syntax using an Oracle JDBC Driver:
	CallableStatement callableStatement 
		= connection.prepareCall("{ call ORACLE_SELECT_CUSTOMER_2(?, ?, ?) }");
	callableStatement.setInt(1, 2);
	callableStatement.setString(2, "Doe3");
	callableStatement.registerOutParameter(3, OracleTypes.CURSOR);
	callableStatement.executeQuery();
	
	ResultSet rs= (ResultSet) callableStatement.getObject(3);
	
	while (rs.next()) {
	    System.out.println(rs.getInt(1) + " "+ rs.getString(2));
	}

	callableStatement.close();

	System.out.println("Done ORACLE_SELECT_CUSTOMER_2!");
	System.out.println();

    }
    
    public static void testStoredProcedureInOut_2(Connection connection) throws SQLException {
	
	// Calling the ORACLE_IN_OUT stored procedure.
	// Native Oracle JDBC syntax using an Oracle JDBC Driver:
	CallableStatement callableStatement 
		= connection.prepareCall("{ call ORACLE_IN_OUT_2(?, ?, ?) }");
	callableStatement.setInt(1, 1);
	callableStatement.setInt(2, 2);
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
    
    public static void testStoredProcedureInOut(Connection connection) throws SQLException {
	
	// Calling the ORACLE_IN_OUT stored procedure.
	// Native Oracle JDBC syntax using an Oracle JDBC Driver:
	CallableStatement callableStatement 
		= connection.prepareCall("{ call ORACLE_IN_OUT(?, ?) }");
	callableStatement.setInt(1, 1);
	callableStatement.setInt(2, 2);
	callableStatement.registerOutParameter(2, java.sql.Types.INTEGER);
	@SuppressWarnings("unused")
	int n = callableStatement.executeUpdate();
	
	int out2 = callableStatement.getInt(2);
	System.out.println("out2: " + out2);
	
	callableStatement.close();
	
	System.out.println("Done ORACLE_IN_OUT!");
	System.out.println();
    }

}
