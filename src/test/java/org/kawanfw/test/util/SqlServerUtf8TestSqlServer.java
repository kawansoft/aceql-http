/*
 * Copyright (c)2022 KawanSoft S.A.S.
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-30
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.test.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.kawanfw.test.sql.metadata.aceql.ConnectionParms;

public class SqlServerUtf8TestSqlServer {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	
	System.out.println(new Date() + " Insert Begin...");
	Connection connection = ConnectionParms.getSQLServerConnection();

	deleteTest1(connection);
	testSqlServerSoredProcedure(connection);
    }

    
    public static void testSqlServerSoredProcedure(Connection connection) throws SQLException {
	
	
	System.out.println(new Date() + " Stored Procedure Begin...");
	String parm1 = "टेस�?ट";
	
	CallableStatement callableStatement = connection.prepareCall("{call spAddNvarchar(?) }");
	callableStatement.setNString(1, parm1);
	callableStatement.executeUpdate();

	System.out.println(new Date() + " Stored Procedure End.");

    }


    /**
     * @param connection
     * @throws SQLException
     */
    public static void useStatements(Connection connection) throws SQLException {
	String sql = "insert into test1 values (?)";
	PreparedStatement preparedStatement = connection.prepareStatement(sql);

	String parm1 = "टेस�?ट";
	int j = 1;
	preparedStatement.setString(j, parm1);
	preparedStatement.executeUpdate();
	
	System.out.println(new Date() + " Insert Done.");
	
	System.out.println();
	System.out.println(new Date() + " Select Begin...");
	select(connection);
	System.out.println();
	System.out.println(new Date() + " Select Done.");
    }

    private static void select(Connection connection) throws SQLException {
	String sql = "select*  from test1";
	PreparedStatement preparedStatement = connection.prepareStatement(sql);
	ResultSet rs = preparedStatement.executeQuery();
	
	while (rs.next()) {
	    System.out.println(rs.getString(1));
	}
	
    }

    private static void deleteTest1(Connection connection) throws SQLException {
	String sql = "delete from test1";
	PreparedStatement preparedStatement = connection.prepareStatement(sql);
	preparedStatement.executeUpdate();
    }
}
