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
import java.util.Date;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.DefaultDatabaseConfigurator;
import org.kawanfw.sql.api.server.util.SimpleSha1;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * @author Nicolas de Pomereu
 *
 *         DatabaseConfigurator implementation. Its extends the default
 *         configuration and provides a security mechanism for login.
 */

public class TestDatabaseConfigurator extends DefaultDatabaseConfigurator
	implements DatabaseConfigurator {

    /** Debug info */
    private static boolean DEBUG = FrameworkDebug
	    .isSet(TestDatabaseConfigurator.class);

    /**
     * Default constructor
     */
    public TestDatabaseConfigurator() {

    }

    /**
     * Our own Acme Company authentication of remote client users. This methods
     * overrides the {@link DefaultDatabaseConfigurator#login} method. <br>
     * The (username, password) values are checked against the user_login table.
     *
     * @param username
     *            the username sent by AceQL client side
     * @param password
     *            the user password sent by AceQL client side
     * @param database
     *            the database name to which the client wants to connect
     * @param ipAddress
     *            the IP address of the client user
     * @return true if access is granted, else false
     */
    @Override
    public boolean login(String username, char[] password, String database,
	    String ipAddress) throws IOException, SQLException {

	System.out.println("database : " + database);
	System.out.println("ipAddress: " + ipAddress);

	PreparedStatement prepStatement = null;
	ResultSet rs = null;

	// Always close the Connection so that it is put
	// back into the pool for another user at end of call.

	try (// Extract a Connection from our Pool
		Connection connection = super.getConnection(database);) {

	    String hashPassword = null;

	    try {
		hashPassword = SimpleSha1.sha1(new String(password), true);
	    } catch (Exception e) {
		throw new IOException("Unexpected Sha1 failure", e);
	    }

	    // Check (username, password) existence in user_login table
	    String sql = "SELECT username FROM user_login "
		    + "WHERE username = ? AND hash_password = ?";
	    prepStatement = connection.prepareStatement(sql);
	    prepStatement.setString(1, username);
	    prepStatement.setString(2, hashPassword);

	    rs = prepStatement.executeQuery();

	    boolean ok = false;
	    if (rs.next()) {

		ok = true; // Yes! (username, password) are authenticated
	    }

	    prepStatement.close();
	    rs.close();
	    return ok;
	}
    }

//    /**
//     * @return <code><b>true</b></code> if all following requirements are met:
//     *         <ul>
//     *         <li>Statement does not contain SQL comments.</li>
//     *         <li>Statement does not contain ";" character.</li>
//     *         <li>Statement is a DML statement: DELETE / INSERT / SELECT /
//     *         UPDATE.</li>
//     *         <li>Table must be: CUSTOMER / ORDERLOG / USER_LOGIN</li>
//     *         </ul>
//     */
//
//    @Override
//    public boolean allowSqlRunAfterAnalysis(String username,
//	    Connection connection, String ipAddress, String sql,
//	    boolean isPreparedStatement, List<Object> parameterValues)
//	    throws IOException, SQLException {
//
//	debug("Begin allowSqlRunAfterAnalysis");
//	debug("sql            : " + sql);
//	debug("parameterValues: " + parameterValues);
//
//	// We will start statement analysis on the sql string.
//	StatementAnalyzer statementAnalyzer = new StatementAnalyzer(sql,
//		parameterValues);
//
//	List<String> tables = statementAnalyzer.getTables();
//	if (! tables.isEmpty() && tables.get(0)
//		.equalsIgnoreCase("DUSTOMER")) {
//	    return false;
//	}
//
//	// No comments
//	if (statementAnalyzer.isWithComments()) {
//	    return false;
//	}
//
//	// No DDL
//	if (statementAnalyzer.isDdl()) {
//	    return false;
//	}
//
//	// No DCL
//	if (statementAnalyzer.isDcl()) {
//	    return false;
//	}
//
//	// ok, accept statement
//	return true;
//    }

    // @Override
    // public boolean encryptResultSet() throws IOException ,SQLException
    // {
    // return true;
    // }
    //

    // /**
    // * @return 50
    // */
    // @Override
    // public int getMaxRowsToReturn() throws IOException, SQLException {
    // return maxRowToReturn;
    // }

    @SuppressWarnings("unused")
    private int maxRowToReturn = 0;

    /**
     * @param maxRowToReturn
     *            the maxRowToReturn to set
     */
    public void setMaxRowToReturn(int maxRowToReturn) {
	this.maxRowToReturn = maxRowToReturn;
    }

    /**
     * @param s
     *            the content to log/debug
     */
    @SuppressWarnings("unused")
    private void debug(String s) {
	if (DEBUG)
	    System.out.println(
		    this.getClass().getName() + " " + new Date() + " " + s);
    }

}
