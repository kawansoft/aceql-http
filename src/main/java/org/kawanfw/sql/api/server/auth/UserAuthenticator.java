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
package org.kawanfw.sql.api.server.auth;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Interface that defines how to authenticate a remote client that wants to
 * create an AceQL session. <br>
 * <br>
 * Following built-in and ready to use classes are provided. No coding is required.
 * See each Javadoc for more details:
 * <br>
 * <ul>
 * <li>{@link LdapUserAuthenticator}: authenticates the remote client (username,
 * password) against a LDAP server.</li>
 * <li>{@link SshUserAuthenticator}: authenticates the remote client (username,
 * password) against a SSH server.</li>
 * <li>{@link WebServiceUserAuthenticator}: authenticates the remote client
 * (username, password) against a Web service.</li>
 * <li>{@link WindowsUserAuthenticator}: authenticates the remote client
 * (username, password) against the Windows server on which the AceQL server is
 * running.</li>
 *
 *
 * </ul>
 *
 * @author Nicolas de Pomereu
 * @since 5.0
 */
public interface UserAuthenticator {

    /**
     * Allows to authenticate the remote {@code (username, password)} couple sent by
     * the client side.
     * <p>
     * The AceQL HTTP Server will call the method in order to grant or not client
     * access.
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
     * @return <code>true</code> if the (username, password) couple is
     *         correct/valid. If false, the client side will not be authorized to
     *         send any command.
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     */
    public boolean login(String username, char[] password, String database, String ipAddress)
	    throws IOException, SQLException;

}
