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
package org.kawanfw.test.api.server.config;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.kawanfw.sql.api.server.StatementAnalyzer;
import org.kawanfw.sql.api.server.firewall.DefaultSqlFirewallManager;

/**
 * Class that show hot to analyze a client SQL statement and authorize it or
 * not.
 *
 * @author Nicolas de Pomereu
 *
 */
public class MySqlFirewallManager extends DefaultSqlFirewallManager {

    /**
     * Allows, for the passed client username, to analyze the string
     * representation of the SQL statement that is received on the server. <br>
     * If the analysis defined by the method returns false, the SQL statement
     * won't be executed.
     *
     * @param username
     *            the client username to check the rule for.
     * @param database
     *            the database name as defined in the JDBC URL field
     * @param connection
     *            The current SQL/JDBC <code>Connection</code>
     *            the database name as defined in the JDBC URL field
     * @param ipAddress
     *            the IP address of the client user
     * @param isPreparedStatement
     *            Says if the statement is a prepared statement
     * @param sql
     *            the SQL statement
     * @param parameterValues
     *            the parameter values of a prepared statement in the natural
     *            order, empty list for a (non prepared) statement
     * @return <code><b>true</b></code> if all following requirements are met:
     *         <ul>
     *         <li>username does not exists in applicative SQL table
     *         BANNED_USERNAMES.</li>
     *         <li>SQL string must *not* contain SQL comments.</li>
     *         <li>SQL string must *not* contain any semicolon.</li>
     *         <li>SQL string statement must be a DML: DELETE / INSERT / SELECT
     *         / UPDATE.</li>
     *         <li>DELETE / UPDATE statements that are not a PreparedStatement
     *         or that have no parameters are rejected.</li>
     *         <li>Any UPDATE on the USER_LOGIN and PRODUCT_ORDER tables
     *         requires that USERNAME value is the last parameter of the
     *         PreparedStatement.</li>
     *         <li>If an illegitimate SQL statement is detected, discard the
     *         username and log his IP as a banned IP.</li>
     *         </ul>
     *
     * @throws IOException
     *             if an IOException occurs
     * @throws SQLException
     *             if a SQLException occurs
     */
    @Override
    public boolean allowSqlRunAfterAnalysis(String username, String database,
	    Connection connection, String ipAddress,
	    String sql, boolean isPreparedStatement, List<Object> parameterValues)
		    throws IOException, SQLException {

	// First thing is to test if the username has previously been stored in
	// our applicative BANNED_USERNAME table
	String sqlOrder = "SELECT USERNAME FROM BANNED_USERNAMES WHERE USERNAME = ?";

	PreparedStatement prepStatement = connection.prepareStatement(sqlOrder);
	prepStatement.setString(1, username);
	ResultSet rs = prepStatement.executeQuery();

	boolean usernameBanned = rs.next();
	prepStatement.close();
	rs.close();

	if (usernameBanned) {
	    return false;
	}

	// We will start statement analysis on the SQL string.
	StatementAnalyzer statementAnalyzer = new StatementAnalyzer(sql,
		parameterValues);

	// SQL string must *not* contain SQL comments ==> Possible security
	// hole.
	if (statementAnalyzer.isWithComments()) {
	    return false;
	}

	// SQL string must *not* contain ";" ==> Possible security hole.
	if (statementAnalyzer.isWithSemicolons()) {
	    return false;
	}

	// SQL string statement must be a DML: DELETE / INSERT / SELECT /
	// UPDATE.
	if (!statementAnalyzer.isDml()) {
	    return false;
	}

	// Any UPDATE on the USER_LOGIN and PRODUCT_ORDER tables requires that
	// USERNAME value is the last parameter of the PreparedStatement

	if (statementAnalyzer.isUpdate() || statementAnalyzer.isDelete()) {

	    List<String> tables = statementAnalyzer.getTables();
	    if (tables.isEmpty()) {
		return false;
	    }

	    String table = tables.get(0);

	    if (!isPreparedStatement) {
		return false;
	    }

	    if (table.equalsIgnoreCase("USER_LOGIN")
		    || table.equalsIgnoreCase("PRODUCT_ORDER")) {
		String lastParamValue = null;

		lastParamValue = statementAnalyzer.getLastParameter()
			.toString();

		if (!lastParamValue.equals(username)) {
		    return false;
		}
	    }
	}

	// OK, accept the statement!
	return true;
    }

    /**
     * Insert the username that made an illegal SQL call and it's IP address
     * into the BANNED_USERNAMES table. From now on, the username will not be
     * able to do any further AceQL HTTP calls.
     *
     * @param username
     *            the discarded client username
     * @param database
     *            the database name as defined in the JDBC URL field
     * @param connection
     *            The current SQL/JDBC <code>Connection</code>
     * @param ipAddress
     *            the IP address of the client user
     * @param isMetadataQuery Says if the call is an AceQL Metadata Query API call.
     * @param sql
     *            the SQL statement
     * @param parameterValues
     *            the parameter values of a prepared statement in the natural
     *            order, empty list for a (non prepared) statement
     *
     * @throws IOException
     *             if an IOException occurs
     * @throws SQLException
     *             if a SQLException occurs
     */
    @Override
    public void runIfStatementRefused(String username, String database,
	    Connection connection, String ipAddress,
	    boolean isMetadataQuery, String sql, List<Object> parameterValues)
		    throws IOException, SQLException {

	// Call the parent method that logs the event:
	super.runIfStatementRefused(username, database, connection, ipAddress,
		isMetadataQuery, sql, parameterValues);

	System.err.println("Statement refused by MySqlFirewallManager: " + sql);

	// Insert the username & its IP into the banned usernames table
	String sqlOrder = "INSERT INTO BANNED_USERNAMES VALUES (?)";

	PreparedStatement prepStatement = connection.prepareStatement(sqlOrder);
	prepStatement.setString(1, username);
	try {
	    prepStatement.executeUpdate();
	} catch (SQLException e) {
	    // Case the instance already exists
	    System.out.println("Ignore: " + e.toString());
	}
	prepStatement.close();

    }

}
