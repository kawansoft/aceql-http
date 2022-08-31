/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.test.sql.metadata.aceql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Simple tool to get a Connection.
 *
 * @author Nicolas de Pomereu
 *
 */
public class ConnectionParms {

    public static final int POSTGRES_CONNECTION = 1;
    public static final int MYSQL_CONNECTION = 2;
    public static final int SQL_SERVER_CONNECTION = 3;
    public static final int DB2_CONNECTION = 4;
    public static final int ORACLE_CONNECTION = 5;

    private static int DEFAULT = 3;

    public static Connection getConnection(int typeConnection)
	    throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {

	Connection con = null;

	if (typeConnection == POSTGRES_CONNECTION) {
	    con = getPostgresConnection();
	} else if (typeConnection == MYSQL_CONNECTION) {
	    con = getMysqlConnection();
	} else if (typeConnection == SQL_SERVER_CONNECTION) {
	    con = getSQLServerConnection();
	} else if (typeConnection == DB2_CONNECTION) {
	    con = getDb2Connection();
	} else if (typeConnection == ORACLE_CONNECTION) {
	    con = getOracleConnection();
	} else {
	    throw new IllegalArgumentException("Unknown connection type: " + typeConnection);
	}

	return con;
    }

    /**
     *
     * @return the JDBC Connection depending on environment
     * @throws Exception
     */
    public static Connection getConnection() throws Exception {

	return getConnection(DEFAULT);
    }

    /**
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection getMysqlConnection()
	    throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
	Connection con = null;
	Class.forName("org.gjt.mm.mysql.Driver").newInstance();

	con = DriverManager.getConnection("jdbc:mysql://localhost:3306/sampledb", "user1", "password1");
	return con;
    }

    /**
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection getPostgresConnection()
	    throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
	Connection con = null;
	Class.forName("org.postgresql.Driver").newInstance();

	con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/sampledb", "postgres", "327qm9y3");
	return con;
    }

    /**
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection getSQLServerConnection()
	    throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
	Connection con = null;
	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();

	con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;database=sampledb", "user1",
		"password1");
	return con;
    }

    private static Connection getOracleConnection()
	    throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
	Connection con = null;
	Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();

	con = DriverManager.getConnection("jdbc:oracle:thin:sampledb@//localhost:1521/XE", "user1",
		"password1");

	return con;

    }

    private static Connection getDb2Connection() throws SQLException, ClassNotFoundException {
	String url = "jdbc:db2://127.0.0.1:50000";
	String database = "Books";
	String user = "db2admin";
	String password = "db2admin*$123";

	Class.forName("com.ibm.db2.jcc.DB2Driver");
	Connection con = DriverManager.getConnection(url + "/" + database, user, password);
	return con;
    }

}
