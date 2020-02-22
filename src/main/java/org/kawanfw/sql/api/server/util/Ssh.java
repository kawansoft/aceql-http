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
package org.kawanfw.sql.api.server.util;

import java.io.IOException;
import java.net.InetAddress;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.util.Tag;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 *
 * This class provides static methods for SSH authentication to be used directly
 * in {@link DatabaseConfigurator#login(String, char[], String, String)}
 * implementations.
 *
 * @see org.kawanfw.sql.api.server.SshAuthDatabaseConfigurator
 *
 * @author Nicolas de Pomereu
 */
public class Ssh {

    private static final String UNKNOWN_IP_ADDRESS = "unknown_ip_address";

    /** The IP address */
    private static String ipAddress = null;

    /**
     * Tries to open a SSH session on a passed host for authentication.
     * <p>
     * The internal SSH client Java library used is
     * <a href="http://www.jcraft.com/jsch/">JSch</a>. <br>
     * Note that there is no host key checking ( {@code "StrictHostKeyChecking"}
     * is set to {@code "no"}).
     *
     * @param host
     *            the host name or IP address of the SSH server
     * @param port
     *            the port number of the SSH server
     * @param username
     *            the user name for authentication
     * @param password
     *            the password for authentication
     *
     * @return <code>true</code> if the user is able to open a SSH session with
     *         the passed parameters
     *
     * @throws IOException
     *             if <code>username</code> or <code>host</code> are invalid.
     *
     */
    public static boolean login(String host, int port, String username,
	    char[] password) throws IOException {
	// Create a JSch Session with passed values
	JSch jsch = new JSch();
	Session session = null;

	try {
	    session = jsch.getSession(username, host, port);
	} catch (JSchException e) {
	    throw new IOException(Tag.PRODUCT + " username or host is invalid.",
		    e);
	}

	session.setPassword(new String(password));
	session.setConfig("StrictHostKeyChecking", "no");

	// Ok try to connect
	boolean connected = false;
	try {
	    session.connect();
	    connected = true;
	    session.disconnect();
	} catch (JSchException e) {
	    System.err.println("SSH connection impossible for " + username + "@"
		    + host + ":" + port + ". (" + e.toString() + ")");
	}

	return connected;
    }

    /**
     * Returns the computer IP address in 192.168.1.146 format.
     *
     * @return the name or <b><code>unknown_ip_address</code></b> if the IP
     *         address cannot be found
     */

    public static String getIpAddress() {
	try {
	    if (ipAddress == null) {
		InetAddress ip = InetAddress.getLocalHost();

		ipAddress = ip.getHostAddress();
	    }
	} catch (Exception e) {
	    ipAddress = UNKNOWN_IP_ADDRESS;
	    e.printStackTrace(System.out);
	}

	return ipAddress;
    }
}
