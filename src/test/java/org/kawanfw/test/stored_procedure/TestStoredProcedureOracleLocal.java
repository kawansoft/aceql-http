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
package org.kawanfw.test.stored_procedure;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
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
	Connection connection = getOracleDatabaseConnection();

	DatabaseMetaData data = connection.getMetaData();
	System.out.println("Db Engine: " + data.getDatabaseProductName());

	TestStoredProcedureCommons.selectCustomerExecute(connection);
	testFunctionInOut(connection);
	testStoredProcedureInOut(connection);
	testStoredProcedureInOut(connection);

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
    public static Connection getOracleDatabaseConnection()
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {
	ConnectionLoaderJdbcInfo connectionLoaderJdbcInfo = new ConnectionLoaderJdbcInfo("Oracle");
	String driverClassName = connectionLoaderJdbcInfo.getDriverClassName();
	String url = connectionLoaderJdbcInfo.getUrl();

	Class<?> c = Class.forName(driverClassName);
	Constructor<?> constructor = c.getConstructor();
	Driver driver = (Driver) constructor.newInstance();

	// String username = "sys as sysdba";
	// String password = "327qm9y4";

	String username = "C##_KAWAN";
	String password = "327qm9y4";

	Properties properties = new Properties();
	properties.put("user", username);
	properties.put("password", password);
	Connection connection = driver.connect(url, properties);

	if (connection == null) {
	    System.err.println("driverClassName: " + driverClassName);
	    System.err.println("url            : " + url);
	    throw new IllegalArgumentException("Connection is null after driver.connect(url, properties)!");
	}
	return connection;
    }


    public static void testFunctionInOut(Connection connection) throws SQLException {
	/**
	 * <code>
	CREATE OR REPLACE FUNCTION FUNCTION1 (PARAM1 number, PARAM2 VARCHAR)
	RETURN VARCHAR2 AS 
	BEGIN
	  RETURN TO_CHAR(PARAM1 * 2) || ' ' || PARAM2 || ' 42!' ;
	END FUNCTION1;
	</code>
	 */

	CallableStatement cs = connection.prepareCall("begin ? := FUNCTION1(?, ?); end;");
	cs.registerOutParameter(1, Types.VARCHAR);
	cs.setInt(2, 12);
	cs.setString(3, "Meaning of life is:");
	cs.execute();
	String result = cs.getString(1);

	System.out.println();
	System.out.println("testFunctionInOut:");
	System.out.println("result: " + result);
	System.out.println();
    }

    public static void testStoredProcedureSelectCustomer(Connection connection) throws SQLException {

	/**
	 * <code>
	create or replace PROCEDURE ORACLE_SELECT_CUSTOMER 
	    (p_customer_id IN OUT NUMBER, p_customer_name VARCHAR, p_rc OUT sys_refcursor) AS 
	BEGIN
	    OPEN p_rc
	    For select customer_id, lname from customer where customer_id > p_customer_id
	    and lname <> p_customer_name;
	END ORACLE_SELECT_CUSTOMER;
	</code>
	 */

	// Calling the ORACLE_SELECT_CUSTOMER stored procedure.
	// Native Oracle JDBC syntax using an Oracle JDBC Driver:
	CallableStatement callableStatement = connection.prepareCall("{ call ORACLE_SELECT_CUSTOMER(?, ?, ?) }");
	callableStatement.setInt(1, 2);
	callableStatement.setString(2, "Doe3");
	callableStatement.registerOutParameter(1, java.sql.Types.INTEGER);
	callableStatement.registerOutParameter(3, OracleTypes.CURSOR);
	callableStatement.executeQuery();

	ResultSet rs = (ResultSet) callableStatement.getObject(3);

	while (rs.next()) {
	    System.out.println(rs.getInt(1) + " " + rs.getString(2));
	}

	int out = callableStatement.getInt(1);
	System.out.println("out: " + out);

	callableStatement.close();

	System.out.println("Done ORACLE_SELECT_CUSTOMER!");
	System.out.println();

    }

    public static void testStoredProcedureInOut(Connection connection) throws SQLException {

	/**
	 * <code>
	   create or replace PROCEDURE ORACLE_IN_OUT
	   (
	     PARAM1 IN NUMBER 
	   , PARAM2 IN OUT NUMBER
	   , PARAM3 IN OUT VARCHAR 
	   ) AS 
	   BEGIN
	     param2 := param1 * param2;
	     param3 := param3 || ' ' || TO_CHAR(param2);
	   END ORACLE_IN_OUT;
	 </code>
	 */

	// Calling the ORACLE_IN_OUT stored procedure.
	// Native Oracle JDBC syntax using an Oracle JDBC Driver:
	CallableStatement callableStatement = connection.prepareCall("{ call ORACLE_IN_OUT(?, ?, ?) }");
	callableStatement.setInt(1, 6);
	callableStatement.setInt(2, 7);
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

	System.out.println("Done ORACLE_IN_OUT!");
	System.out.println();
    }

}
