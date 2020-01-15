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
package org.kawanfw.sql.api.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * 
 * Wrapper class for all kind of prepared statement: query or update <br>
 * Update and Delete are safe because the WHERE clause is mandatory to prevent
 * dramatic errors. <br>
 * <br>
 * Example: <blockquote>
 * 
 * <pre>
 * // Get a JDBC Connection
 * Connection connection = ...
 * &nbsp;
 * int customerId = 1;
 * String sql = &quot;select * from customer where customer_id = ?&quot;;
 * &nbsp;
 * // Create the PreparedStatementRunner instance
 * PreparedStatementRunner preparedStatementRunner = new PreparedStatementRunner(
 * 	connection, sql, customerId);
 * &nbsp;
 * // Execute a query
 * ResultSet rs = preparedStatementRunner.executeQuery();
 * ...
 * ...
 * // close the underlying ResultSet and PreparedStatement
 * preparedStatementRunner.close();
 * </pre>
 * 
 * </blockquote>
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */
public class PreparedStatementRunner {
    /** Debug Value */
    private static boolean DEBUG = FrameworkDebug
	    .isSet(PreparedStatementRunner.class);

    private static String CR_LF = System.getProperty("line.separator");

    private static final String WHERE = "where";
    private static final String INSERT = "insert";
    private static final String DELETE = "delete";
    private static final String UPDATE = "update";
    private static final String SELECT = "select";

    /** The JDBC connection to database */
    private Connection connection = null;

    /** The Prepared Statement to execute */
    private PreparedStatement prepStatement = null;

    /** The Result Set after execution */
    private ResultSet rs = null;

    /** The sql order in raw presentation with the '?' */
    private String sql = null;

    /** The '?' parameters */
    private List<Object> params = new Vector<Object>();

    /** the developed query */
    private String developedQuery = null;

    /**
     * Constructor.
     * 
     * @param connection
     *            the JDBC Connection instance
     * @param sql
     *            the prepared statement base SQL request with all the '?'
     * @param params
     *            the prepared statement parameters value in the awaited order
     */

    public PreparedStatementRunner(Connection connection, String sql,
	    Object... params) {
	if (connection == null) {
	    throw new IllegalArgumentException("connection can\'t be null");
	}

	if (sql == null) {
	    throw new IllegalArgumentException(
		    "sql preparement statement string can\'t be null");
	}

	sql = sql.trim();

	while (sql.endsWith(";")) {
	    sql = StringUtils.removeEnd(sql, ";");
	}

	this.sql = sql;
	developedQuery = sql;

	// Build the string of the developed query
	for (int i = 0; i < params.length; i++) {
	    this.params.add(params[i]);

	    String strParam = params[i].toString();

	    developedQuery = developedQuery.replaceFirst("\\?", strParam);
	}

	this.connection = connection;
    }

    /**
     * Executes a SQL prepared statement for a query.
     * 
     * @return the result set of the prepared statement
     * 
     * @throws SQLException
     *             if a SQL Exception is raised
     */
    public ResultSet executeQuery() throws SQLException {
	prepStatement = connection.prepareStatement(sql,
		ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

	int numberOfIntMarks = StringUtils.countMatches(sql, "?");
	int numberOfParams = params.size();

	if (numberOfIntMarks != numberOfParams) {
	    throw new SQLException(
		    "sql statement numbers of \"?\" do no match number of parameters: "
			    + numberOfIntMarks + " and " + numberOfParams);
	}

	for (int i = 0; i < params.size(); i++) {
	    int j = i + 1;
	    prepStatement.setObject(j, params.get(i));
	}

	rs = null;

	if (sql.toLowerCase().startsWith(SELECT)) {
	    debug("sql query for prepStatement.executeQuery(): " + CR_LF + sql);
	    rs = prepStatement.executeQuery();
	} else {
	    throw new SQLException("sql string is not a query: " + sql);
	}

	return rs;
    }

    /**
     * Executes a SQL prepared statement for an update.
     * 
     * @return the return code of the prepared statement
     * 
     * @throws SQLException
     *             if a SQL Exception is raised
     */
    public int executeUpdate() throws SQLException {
	prepStatement = connection.prepareStatement(sql);

	int numberOfIntMarks = StringUtils.countMatches(sql, "?");
	int numberOfParams = params.size();

	if (numberOfIntMarks != numberOfParams) {
	    throw new SQLException(
		    "sql statement numbers of \"?\" do no match number of parameters: "
			    + numberOfIntMarks + " and " + numberOfParams);
	}

	for (int i = 0; i < params.size(); i++) {
	    int j = i + 1;
	    prepStatement.setObject(j, params.get(i));
	}

	int rc = -1;

	String sqlLower = sql.toLowerCase();

	if (sqlLower.startsWith(SELECT)) {
	    throw new SQLException("sql string is not an update: " + sql);
	}

	if (sqlLower.startsWith(UPDATE) || sqlLower.startsWith(DELETE)) {
	    if (sqlLower.indexOf(" " + WHERE + " ") == 0) {
		throw new SQLException(
			"update and delete are not permitted without a WHERE clause: "
				+ sql);
	    }
	}

	if (sqlLower.startsWith(UPDATE) || sqlLower.startsWith(DELETE)
		|| sqlLower.startsWith(INSERT)) {
	    rc = prepStatement.executeUpdate();
	} else {
	    throw new SQLException(
		    "Statement is not INSERT / UPDATE / DELETE: " + sql);
	}

	debug(this.toString());
	return rc;
    }

    /**
     * Returns the developedQuery with substituted '?' by the passed values as
     * parameters.
     * 
     * @return the developedQuery with substituted '?' by the passed values as
     *         parameters
     */
    public String getDevelopedQuery() {
	return developedQuery;
    }

    /**
     * Returns a clean representation of the
     * <code>PreparedStatementRunner</code> instance.
     * 
     * @return a clean representation of the
     *         <code>PreparedStatementRunner</code> instance
     */
    @Override
    public String toString() {
	String string = "Initial query..: " + sql + CR_LF + "Parameters.....: "
		+ params.toString() + CR_LF + "DevelopedQuery : " + CR_LF
		+ developedQuery;

	return string;
    }

    /**
     * Closes the PreparedStatementRunner. This call is recommended. It will
     * close the underlying prepared statement & result set.
     */
    public void close() {
	try {
	    if (prepStatement != null) {
		prepStatement.close();
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	try {
	    if (rs != null) {
		rs.close();
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	}

    }

    private static void debug(String s) {
	if (DEBUG) {
	    System.out.println(s);
	}
    }

}
