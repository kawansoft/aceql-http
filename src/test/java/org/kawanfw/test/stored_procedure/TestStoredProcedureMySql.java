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
import java.lang.reflect.InvocationTargetException;
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
public class TestStoredProcedureMySql {
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	Connection connection = getMySqlConnection();
	
	DatabaseMetaData data = connection.getMetaData();
	System.out.println("Db Engine: " + data.getDatabaseProductName());
	System.out.println();
	
	TestStoredProcedureCommons.selectCustomerExecute(connection);
	testStoredProcedureSelectCustomer(connection);
    }


    /**
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws SQLException
     */
    public static Connection getMySqlConnection()
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {
	ConnectionLoaderJdbcInfo connectionLoaderJdbcInfo = new ConnectionLoaderJdbcInfo("MySQL");
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
	return connection;
    }
    

    public static void testStoredProcedureSelectCustomer(Connection connection) throws SQLException {
	
	/**
	 <code>
            CREATE DEFINER=`user1`@`%` PROCEDURE `SelectCustomer`(
            	INOUT p_customer_id INT,
                IN p_customer_name  INT
            )
            BEGIN
            	select customer_id, lname from customer where customer_id > @p_customer_id 
            	and  lname <> @p_customer_name;
            END
	 </code>
	 */
    	
	CallableStatement callableStatement 
		= connection.prepareCall("{ call SelectCustomer(?, ?) }");
	callableStatement.setInt(1, 3);
	callableStatement.setString(2, "Doe3");
	
	callableStatement.registerOutParameter(1, java.sql.Types.INTEGER);
	
	
	ResultSet rs = callableStatement.executeQuery();
	
	while (rs.next()) {
	    System.out.println(rs.getInt(1) + " "+ rs.getString(2));
	}
	
	int out = callableStatement.getInt(1);
	System.out.println("out: " + out);
	
	callableStatement.close();

	System.out.println("Done dbo.spSelectCustomer!");
	System.out.println();

    }
    
  
}
