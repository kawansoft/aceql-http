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
package org.kawanfw.sql.api.server.auth;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.util.auth.JdbcUserAuthenticatorUtil;
import org.kawanfw.sql.servlet.injection.classes.InjectedClasses;
import org.kawanfw.sql.servlet.injection.classes.InjectedClassesStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileUtil;
import org.kawanfw.sql.util.SqlTag;

/**
 * A concrete {@code UserAuthenticator} that allows zero-code remote client
 * {@code (username, password)} authentication using a JDBC query run against an
 * SQL table.<br>
 * <br>
 * The request that is executed is defined in the
 * {@code jdbcUserAuthenticator.authenticationQuery} property in the
 * {@code aceql-server.properties} file. <br>
 * <br>The default SQL table to create and populate is defined by the
 * {@code jdbcUserAuthenticator.authenticationQuery} value:
 * <code>SELECT encrypted_password FROM users WHERE username = ?</code> ans is
 * thus in the format :
 * 
 * <pre>
 * <code>
CREATE TABLE users
(               
  username              varchar(300)    not null,     
  encrypted_password    varchar(300)    not null,
        PRIMARY KEY (username)
);
 * </code>
 * </pre>
 * 
 * The hash encryption algorithm, iterations & salt may be set using the
 * following properties:
 * <ul>
 * <li>{@code jdbcUserAuthenticator.hashAlgorithm}
 * <li>{@code jdbcUserAuthenticator.hashIterations}
 * <li>{@code jdbcUserAuthenticator.salt}
 * </ul>
 * 
 * @see UserAuthenticator
 * @author Nicolas de Pomereu
 * @since 10.1
 */
public class JdbcUserAuthenticator implements UserAuthenticator {

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

	String authenticationQuery = properties.getProperty("jdbcUserAuthenticator.authenticationQuery");
	if (authenticationQuery == null || authenticationQuery.isEmpty()) {
	    authenticationQuery = JdbcUserAuthenticatorUtil.DEFAULT_AUTHENTICATION_QUERY;
	}

	String authenticationDatabase = properties.getProperty("jdbcUserAuthenticator.database");
	if (authenticationDatabase == null || authenticationDatabase.isEmpty()) {
	    authenticationDatabase = getFirtDatabase();
	}

	DatabaseConfigurator databaseConfigurator = InjectedClassesStore.get().getDatabaseConfigurators()
		.get(authenticationDatabase);
	try (Connection connection = databaseConfigurator.getConnection(database);) {

	    ConfigurablePasswordEncryptor passwordEncryptor = JdbcUserAuthenticatorUtil
		    .getConfigurablePasswordEncryptor(properties);

	    String encryptedPassword = null;

	    try {
		encryptedPassword = getEncryptedPassword(authenticationQuery, username, connection);
	    } catch (SQLException exception) {
		throw new SQLException(SqlTag.USER_CONFIGURATION
			+ " The dbcUserAuthenticator.authenticationQuery triggers an SQLException: " + exception);
	    }

	    return passwordEncryptor.checkPassword(new String(password), encryptedPassword.toLowerCase());
	}

    }

    /**
     * Execute the authenticationQuery SQL Query that returns the encrypte
     * 
     * @param authenticationQuery the query to execute
     * @param username            the client side username
     * @param connection          the JDBC Connection
     * @return the encrypted/hashed password
     * @throws SQLException
     */
    private static String getEncryptedPassword(String authenticationQuery, String username, Connection connection)
	    throws SQLException {
	Objects.requireNonNull(authenticationQuery, "authenticationQuery cannot be null!");
	Objects.requireNonNull(username, "username cannot be null!");
	Objects.requireNonNull(connection, "connection cannot be null!");

	PreparedStatement prepStatement = connection.prepareStatement(authenticationQuery);
	prepStatement.setString(1, username);

	ResultSet rs = prepStatement.executeQuery();

	if (rs.next()) {
	    return rs.getString(1);
	} else {
	    return null;
	}
    }

    /**
     * Returns the first database defined in the "databases" properties in
     * aceql-server.properties file
     * 
     * @return the first database defined in the "databases" properties in
     *         aceql-server.properties file
     */
    private static String getFirtDatabase() {
	InjectedClasses injectedClasses = InjectedClassesStore.get();
	Map<String, DatabaseConfigurator> map = injectedClasses.getDatabaseConfigurators();
	Set<String> databases = map.keySet();
	String database = databases.iterator().next();
	return database;
    }

}
