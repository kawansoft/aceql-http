/*
 * Copyright (c)2023 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-02-21
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.api.server.auth;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.servlet.injection.classes.InjectedClassesStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileUtil;
import org.kawanfw.sql.servlet.util.logging.LoggerWrapper;
import org.kawanfw.sql.util.Tag;
import org.slf4j.Logger;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * A concrete {@code UserAuthenticator} that allows zero-code remote client
 * {@code (username, password)} authentication against a SSH server. <br>
 * <br>
 * The SSH server that authenticates the users is defined in the
 * {@code sshUserAuthenticator.host} property in the
 * {@code aceql-server.properties} file.
 * 
 * @see UserAuthenticator
 * @author Nicolas de Pomereu
 * @since 5.0
 */
public class SshUserAuthenticator implements UserAuthenticator {

    private Logger logger = null;
    private Properties properties = null;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.kawanfw.sql.api.server.auth.UserAuthenticator#login(java.lang.String,
     * char[], java.lang.String, java.lang.String)
     */
    @Override
    public boolean login(String username, char[] password, String database, String ipAddress)
	    throws IOException, SQLException {

	if (properties == null) {
	    File file = PropertiesFileStore.get();
	    properties = PropertiesFileUtil.getProperties(file);
	}

	String host = properties.getProperty("sshUserAuthenticator.host");
	String portStr = properties.getProperty("sshUserAuthenticator.port");

	Objects.requireNonNull(host, getInitTag() + "The sshUserAuthenticator.host property is null!");

	if (portStr == null) {
	    portStr = "22";
	}

	if (!StringUtils.isNumeric(portStr)) {
	    throw new IllegalArgumentException(
		    getInitTag() + "The sshUserAuthenticator.port property is not numeric: " + portStr);
	}

	int port = Integer.parseInt(portStr);

	// Create a JSch Session with passed values
	JSch jsch = new JSch();
	Session session = null;

	try {
	    session = jsch.getSession(username, host, port);
	} catch (JSchException e) {
	    if (logger == null) {
		DatabaseConfigurator databaseConfigurator = InjectedClassesStore.get().getDatabaseConfigurators()
			.get(database);
		logger = databaseConfigurator.getLogger();
	    }
	    logger.error( getInitTag() + "username: " + username + " or host:" + host + " is invalid.");
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
	    if (logger == null) {
		DatabaseConfigurator databaseConfigurator = InjectedClassesStore.get().getDatabaseConfigurators()
			.get(database);
		logger = databaseConfigurator.getLogger();
	    }
	    LoggerWrapper.log(logger, getInitTag() + "SSH connection impossible for " + username + "@" + host + ":"
		    + port + ".", e);
	}

	return connected;
    }

    /**
     * @return the beginning of the log line
     */
    private String getInitTag() {
	return Tag.PRODUCT + " " + SshUserAuthenticator.class.getSimpleName() + ": ";
    }
}
