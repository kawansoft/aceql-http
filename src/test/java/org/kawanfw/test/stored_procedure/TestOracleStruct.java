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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.kawanfw.sql.metadata.util.GsonWsUtil;

/**
 * @author Nicolas de Pomereu
 *
 */
public class TestOracleStruct {


    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	Connection connection = TestStoredProcedureOracleLocal.getOracleDatabaseConnection();

	DatabaseMetaData data = connection.getMetaData();
	System.out.println("Db Engine: " + data.getDatabaseProductName());
	
	testStruct(connection);
    }
    
    
    public static void testStruct(Connection connection)
	    throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
	System.out.println();

	Statement stmt = connection.createStatement();
	ResultSet rs = stmt.executeQuery("SELECT * FROM struct_table");

	ResultSetMetaData metadata = rs.getMetaData();
	System.out.println("metadata.getColumnClassName(0): " + metadata.getColumnClassName(1));
	System.out.println("metadata.getColumnClassName(1): " + metadata.getColumnClassName(2));

	while (rs.next()) {
	    java.sql.Struct jdbcStruct = (java.sql.Struct)rs.getObject(1);

	    System.out.println("jdbcStruct.getSQLTypeName(): " + jdbcStruct.getSQLTypeName());
	    System.out.println("oracleStruct.getAttributes():");
	    Object[] objects = jdbcStruct.getAttributes();

	    for (int i = 0; i < objects.length; i++) {
		System.out.print(objects[i].toString() + " / " + objects[i].getClass().getCanonicalName() + " ");

//		if (i == 0) {
//		    Class<?> clazz = Class.forName("java.math.BigDecimal");
//		    Object obj = clazz.cast(objects[i]);
//		    System.out.print("bigDecimal: " + obj.toString() + " ! " + obj.getClass().getCanonicalName());
//		}
//		System.out.print(" - ");
//		if (i == 1) {
//		    java.sql.Timestamp timestamp = (java.sql.Timestamp) objects[i];
//		    System.out.print("timestamp: " + timestamp);
//		}
	    }

	    System.out.println();
	    boolean moreDisplay = true;
	    if (moreDisplay) {
		String jsonString = GsonWsUtil.getJSonString(objects);
		System.out.println("jsonString: " + jsonString);

		System.out.println();
		String typeName = jdbcStruct.getSQLTypeName();
		System.out.println("oracleStruct.getSQLTypeName(): " + typeName);
		System.out.println();

		Object[] objects2 = GsonWsUtil.fromJson(jsonString, Object[].class);
		for (Object object2 : objects2) {
		    System.out.print(object2.toString() + " " + object2.getClass().getCanonicalName() + " ");
		}

		/*
		 * System.out.println(); Object objBigDecimal=
		 * Class.forName("java.math.BigDecimal").newInstance(); objBigDecimal = 20;
		 * System.out.print("bigDecimal2 " + objBigDecimal.toString() + " ! " +
		 * objBigDecimal.getClass().getCanonicalName());
		 */
	    }

	    /**
	     * oracle.jdbc.OracleTypeMetaData oracleTypeMetaData =
	     * oracleStruct.getOracleMetaData();
	     * System.out.println("oracleTypeMetaData.getName(): " +
	     * oracleTypeMetaData.getName()); System.out.println();
	     */

	}

	System.out.println();
	BigDecimal myBigDecimal = new BigDecimal(20);
	String jsonString = GsonWsUtil.getJSonString(myBigDecimal);
	System.out.println("jsonString: " + jsonString);

	Class<?> clazz = Class.forName("java.math.BigDecimal");

	Object obj2 = GsonWsUtil.fromJson(jsonString, clazz);
	System.out.println("obj2: " + obj2.toString() + " " + obj2.getClass().getCanonicalName());

    }


}
