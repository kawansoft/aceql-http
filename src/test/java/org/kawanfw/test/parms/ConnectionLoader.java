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
package org.kawanfw.test.parms;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.kawanfw.sql.api.util.SqlUtil;
import org.kawanfw.test.util.UserPrefManager;

/**
 * @author Kawan Softwares S.A.S
 *
 *         Allows to create an AceQL Connection to the remote server, or a local
 *         Connection to the local JDBC Driver
 *
 */

public class ConnectionLoader extends SqlTestParms {

    public static String sqlEngine = null;

    /**
     * Protected constructor
     */
    protected ConnectionLoader() {
    }

    /**
     * @return a Connection to a remote server
     * @throws Exception
     * @throws SQLException
     */
    public static Connection getAceqlConnection()
	    throws SQLException, Exception {

	return SqlTestParms.getConnectionWithUrl(SqlTestParms.ACEQL_URL);
    }

    /**
     * @return a Connection to a remote server
     * @throws Exception
     * @throws SQLException
     */
    public static Connection getAceqlConnection2()
	    throws SQLException, Exception {

	return SqlTestParms.getConnectionWithUrl(SqlTestParms.ACEQL_URL_2);
    }

    /**
     *
     * @return a direct jdbc connection to the local database
     *
     * @throws SQLException
     *             if a SQLException is raised
     * @throws Exception
     *             if the Driver can not be loaded
     */
    public static Connection getLocalConnection()
	    throws SQLException, Exception {


	if (sqlEngine == null) {
	    sqlEngine = UserPrefManager.getSqlEngineToUse();
	}

	ConnectionLoaderJdbcInfo connectionLoaderJdbcInfo = new ConnectionLoaderJdbcInfo(sqlEngine);
	String driverClassName = connectionLoaderJdbcInfo.getDriverClassName();
	String url = connectionLoaderJdbcInfo.getUrl();


	System.err.println();
	System.err.println(
		"WARNING: the Connection is a local JDBC Connection! ");
	System.err.println();

	Class<?> c = Class.forName(driverClassName);

	// Driver driver = (Driver) c.newInstance();

	Constructor<?> constructor = c.getConstructor();
	Driver driver = (Driver) constructor.newInstance();

	String username = JdbcDriverParms.USERNAME;
	String password = JdbcDriverParms.PASSWORD;

	if (sqlEngine.equals(SqlUtil.TERADATA)) {
	    username = "dbc";
	    password = "dbc";
	}

	if (sqlEngine.equals(SqlUtil.HSQLDB)) {
	    username = username.toUpperCase();
	}

	if (sqlEngine.equals(SqlUtil.DB2)) {
	    username = "db2admin";
	    password = "*2loveme$db2";
	}

	// if (sqlEngine.equals(SqlUtil.SQL_ANYWHERE)) {
	// username = "DBA";
	// password = "sql";
	// }

	if (sqlEngine.equals(SqlUtil.ADAPTIVE_SERVER_ENTERPRISE)) {
	    username = "sa";
	    password = "327qm9y3";
	}

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
}
