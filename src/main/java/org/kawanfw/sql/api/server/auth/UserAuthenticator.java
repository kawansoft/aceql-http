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
package org.kawanfw.sql.api.server.auth;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Interface that defines how to authenticate a remote client that wants to
 * create an AceQL session. <br>
 * <br>
 * Following built-in and ready to use classes are provided. No coding is
 * required. See each Javadoc for more details: <br>
 * <ul>
 * <li>{@link JdbcUserAuthenticator}: authenticates the remote client (username,
 * password) against a SQL table using a JDBC query.</li>
 * <li>{@link LdapUserAuthenticator}: authenticates the remote client (username,
 * password) against a LDAP server.</li>
 * <li>{@link SshUserAuthenticator}: authenticates the remote client (username,
 * password) against a SSH server.</li>
 * <li>{@link WebServiceUserAuthenticator}: authenticates the remote client
 * (username, password) against a Web service.</li>
 * <li>{@link WindowsUserAuthenticator}: authenticates the remote client
 * (username, password) against the Windows server on which the AceQL server is
 * running.</li>
 * </ul>
 *
 * @author Nicolas de Pomereu
 * @since 5.0
 */
public interface UserAuthenticator {

    /**
     * Allows to authenticate the remote {@code (username, password)} couple sent by
     * the client side.
     * 
     * <p>
     * The AceQL HTTP Server will call the method in order to grant or not client
     * access.
     * 
     * <p>
     * Typical usage would be to check the (username, password) couple against a
     * LDAP server or against a SSH server, etc.
     *
     * The method allows to retrieve:
     * <ul>
     * <li>The name of the database to which the client wants to connect.</li>
     * <li>The IP address of the client.</li>
     * </ul>
     *
     * @param username  the username sent by the client
     * @param password  the password to connect to the server
     * @param database  the database name to which the client wants to connect
     * @param ipAddress the IP address of the client user
     * @return true if the client is authenticated by the method. If false, the
     *         client side will not be authorized to send any command.
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     */
    public boolean login(String username, char[] password, String database, String ipAddress)
	    throws IOException, SQLException;

}
