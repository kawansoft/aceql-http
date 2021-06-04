package org.kawanfw.test.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.kawanfw.test.sql.metadata.aceql.ConnectionParms;

public class SqlServerUtf8TestSqlServer {

    public SqlServerUtf8TestSqlServer() {
	// TODO Auto-generated constructor stub Value = "टेस्ट"

    }

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
	String parm1 = "टेस्ट";
	
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

	String parm1 = "टेस्ट";
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
