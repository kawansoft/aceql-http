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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.util.FrameworkFileUtil;
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
     * Tries to open a SSH session on a host for authentication.
     * <ul>
     * <li>If the {@code user.home/.kawansoft/sshAuth.properties} file exists:
     * <br>
     * the {@code (username, password)} couple is checked against the SSH server
     * of the host defined with the properties {@code host} for the hostname and
     * {@code port} for the port in the
     * {@code user.home/.kawansoft/sshAuth.properties} file.</li>
     * <li>If {@code sshAuth.properties} file does not exists: <br>
     * the host IP is used as hostname value and port is 22.</li>
     * </ul>
     * {@code user.home} is the one of the running servlet container.
     * <p>
     * The internal SSH client Java library used is
     * <a href="http://www.jcraft.com/jsch/">JSch</a>. <br>
     * Note that there is no host key checking ({@code "StrictHostKeyChecking"}
     * is set to {@code "no"}).
     *
     * @param username
     *            the username sent by the client login
     * @param password
     *            the password to connect to the server
     *
     * @return <code>true</code> if the user is able to open a SSH session with
     *         the passed parameters
     *
     * @throws IOException
     *             if a {@code host} or {@code port} property can not be found
     *             in the {@code sshAuth.properties} or error reading property
     *             file or IP address of the host can not be accessed.
     * @throws NumberFormatException
     *             if the {@code port} property is no numeric
     *
     */
    public static boolean login(String username, char[] password)
	    throws IOException, NumberFormatException {
	String host = null;
	int port = -1;

	String userHomeKawanSoft = FrameworkFileUtil
		.getUserHomeDotKawansoftDir();
	File file = new File(
		userHomeKawanSoft + File.separator + "sshAuth.properties");

	if (file.exists()) {
	    Properties prop = new Properties();

	    try (InputStream in = new FileInputStream(file);) {
		prop.load(in);
	    }

	    host = prop.getProperty("host");
	    String portStr = prop.getProperty("port");

	    if (host == null) {
		throw new IOException(Tag.PRODUCT
			+ " property host not found in sshAuth.properties file.");
	    }

	    if (portStr == null) {
		throw new IOException(Tag.PRODUCT
			+ " property port not found in sshAuth.properties file.");
	    }

	    port = Integer.parseInt(portStr);

	} else {
	    host = getIpAddress();
	    port = 22;

	    if (host.equals(UNKNOWN_IP_ADDRESS)) {
		throw new IOException(Tag.PRODUCT
			+ " Can not retrieve IP address of the host.");
	    }
	}

	return login(host, port, username, password);
    }

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
