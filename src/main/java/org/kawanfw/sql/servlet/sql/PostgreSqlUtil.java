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
package org.kawanfw.sql.servlet.sql;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.kawanfw.sql.api.server.StatementAnalyzer;
import org.kawanfw.sql.api.util.SqlUtil;
import org.kawanfw.sql.util.FrameworkDebug;
import org.postgresql.PGResultSetMetaData;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

/**
 * Dedicated method for PostgreSQL Large Objects treatment
 *
 * @author Nicolas de Pomereu
 *
 */
public class PostgreSqlUtil {

    private static boolean DEBUG = FrameworkDebug.isSet(PostgreSqlUtil.class);

    protected PostgreSqlUtil() {

    }

    /**
     * Returns for PostgreSQL the table for a column
     *
     * @param rs
     *            the ResultSet to analyze
     * @param columnIndex
     *            the column index
     * @return the table name for this column index
     * @throws SQLException
     */
    public static String getTableName(ResultSet rs, int columnIndex)
	    throws SQLException {
	PGResultSetMetaData meta = (PGResultSetMetaData) rs.getMetaData();
	String tableName = meta.getBaseTableName(columnIndex);
	return tableName;
    }

    /**
     * Says if the database is PostgreSQL AND there is an OID column for large
     * file storage
     *
     * @param connection
     *            the JDBC Connection
     * @param sql
     *            the sql order
     * @return true if the database is PostgreSQL AND there is a OID column for
     *         large file storage
     */
    public static boolean isPostgreSqlStatementWithOID(Connection connection,
	    String sql) throws SQLException, IOException {

	debug("before new SqlUtil(connection).isPostgreSQL()");
	if (!new SqlUtil(connection).isPostgreSQL()) {
	    return false;
	}

	String catalog = null;
	String schema = null;
	ResultSet rs = null;

	StatementAnalyzer statementAnalyzer = new StatementAnalyzer(sql,
		new Vector<Object>());
	List<String> tables = statementAnalyzer.getTables();
	if (tables.isEmpty()) {
	    return false;
	}

	String table = tables.get(0);
	table = table.toLowerCase();

	debug("table: " + table);

	DatabaseMetaData databaseMetaData = connection.getMetaData();

	try {
	    rs = databaseMetaData.getColumns(catalog, schema, table, null);
	    debug("Before rs.next");
	    while (rs.next()) {
		int columnType = rs.getInt(5);

		if (columnType == Types.BIGINT) {
		    return true;
		}
	    }
	} finally {
	    if (rs != null) {
		rs.close();
	    }
	}

	return false;
    }

    /**
     * Returns all the column names that are Types.BIGINT
     *
     * @param connection
     * @return the column names that are Types.BIGINT
     * @throws SQLException
     */
    public static Set<String> getTypeBigIntColumnNames(Connection connection)
	    throws SQLException {

	if (connection == null) {
	    throw new IllegalArgumentException("connection is null!");
	}

	DatabaseMetaData databaseMetaData = connection.getMetaData();

	String catalog = null;
	String schema = "public";
	String table = null;

	Set<String> typeBigIntColumnNames = new TreeSet<String>();
	ResultSet rs = null;
	try {
	    rs = databaseMetaData.getColumns(catalog, schema, table, null);
	    debug("Before rs.next");
	    while (rs.next()) {
		int columnType = rs.getInt(5);

		if (columnType == Types.BIGINT) {

		    if (DEBUG) {
			System.out.println();
			System.out.println(rs.getString(1));
			System.out.println(rs.getString(2));
			System.out.println(rs.getString(4));
		    }

		    String columnName = rs.getString(4).toLowerCase();
		    typeBigIntColumnNames.add(columnName);
		}
	    }
	} finally {
	    if (rs != null) {
		rs.close();
	    }
	}

	return typeBigIntColumnNames;
    }

    /**
     * Extract the Large Object Input Stream from PostgreSQL
     *
     * @param resultSet
     *            the Result Set to extract the blob from
     * @param columnIndex
     *            the index of column
     * @return the Large Object Input Stream from PostgreSQL
     * @throws SQLException
     */
    public static InputStream getPostgreSqlnputStream(ResultSet resultSet,
	    int columnIndex) throws SQLException {
	InputStream in;
	Statement statement = resultSet.getStatement();
	Connection conn = statement.getConnection();

	// Get the Large Object Manager to perform operations with
	LargeObjectManager lobj = ((org.postgresql.PGConnection) conn)
		.getLargeObjectAPI();
	long oid = resultSet.getLong(columnIndex);

	if (oid < 1) {
	    return null;
	}

	LargeObject obj = lobj.open(oid, LargeObjectManager.READ);

	in = obj.getInputStream();
	return in;
    }

    /**
     * Create a Large Object to set the PostgreSQL OID with
     *
     * @param preparedStatement
     *            the Prepared Statement
     * @param parameterIndex
     *            the parameter index
     * @param in
     *            The Input Stream to use
     * @param connection
     *            the JDBC Connection
     * @throws SQLException
     * @throws IOException
     */
    public static void setPostgreSqlParameterWithLargeObject(
	    PreparedStatement preparedStatement, int parameterIndex,
	    InputStream in, Connection connection)
	    throws SQLException, IOException {
	// Get the Large Object Manager to perform operations with
	LargeObjectManager lobj = ((org.postgresql.PGConnection) connection)
		.getLargeObjectAPI();

	// Create a new large object
	long oid = lobj
		.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);

	// Open the large object for writing
	LargeObject obj = lobj.open(oid, LargeObjectManager.WRITE);

	try (OutputStream out = obj.getOutputStream();) {
	    IOUtils.copy(in, out);
	} finally {
	    // IOUtils.closeQuietly(out);
	    // Close the large object
	    obj.close();
	}

	preparedStatement.setLong(parameterIndex, oid);
    }

    /**
     * @param s
     */

    protected static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
