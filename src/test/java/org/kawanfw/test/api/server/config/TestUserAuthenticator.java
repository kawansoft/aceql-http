/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2021,  KawanSoft SAS
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

import org.kawanfw.sql.api.server.DefaultDatabaseConfigurator;
import org.kawanfw.sql.api.server.auth.UserAuthenticator;
import org.kawanfw.sql.api.server.util.SimpleSha1;

public class TestUserAuthenticator implements UserAuthenticator {

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

	DefaultDatabaseConfigurator defaultDatabaseConfigurator = new DefaultDatabaseConfigurator();

	try (// Extract a Connection from our Pool
		Connection connection = defaultDatabaseConfigurator.getConnection(database);) {

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
}
