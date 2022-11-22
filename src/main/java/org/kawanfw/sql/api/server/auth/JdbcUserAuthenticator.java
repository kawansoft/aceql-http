/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
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
import org.kawanfw.sql.api.util.auth.ConfigurablePasswordEncryptorUtil;
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
 * <br>
 * The default SQL table to create and populate is defined by the
 * {@code jdbcUserAuthenticator.authenticationQuery} value:
 * <code>SELECT encrypted_password FROM aceql_user WHERE username = ?</code> and is
 * thus in the format:
 * 
 * <pre>
 * <code>
create table aceql_user
(               
  username              varchar(254)    not null,     
  encrypted_password    varchar(4000)   not null,
        primary key (username)
);
 * </code>
 * </pre>
 * 
 * The database that contains the users table should be defined in the
 * {@code aceql-server.properties} file with the property: <br>
 * {@code jdbcUserAuthenticator.database} <br>
 * <br>
 * The hash encryption algorithm, iterations and salt may be set using the
 * following properties:
 * <ul>
 * <li>{@code jdbcUserAuthenticator.hashAlgorithm}
 * <li>{@code jdbcUserAuthenticator.hashIterations}
 * <li>{@code jdbcUserAuthenticator.salt}
 * </ul>
 * Per default, if these 3 previous properties are not set: passwords contained
 * in users table must be encrypted with SHA-256 (with no supplemental
 * iterations and no salt). <br>
 * <br>
 * The {@link JdbcPasswordEncryptor} tooling class is provided for generating
 * encrypted passwords from their clear value. <br>
 * <br>
 * 
 * @see UserAuthenticator
 * @see JdbcPasswordEncryptor
 * @author Nicolas de Pomereu
 * @since 10.1
 */
public class JdbcUserAuthenticator implements UserAuthenticator {

    private Properties properties = null;
    private ConfigurablePasswordEncryptor passwordEncryptor;

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
	    authenticationQuery = ConfigurablePasswordEncryptorUtil.DEFAULT_AUTHENTICATION_QUERY;
	}

	String authenticationDatabase = properties.getProperty("jdbcUserAuthenticator.database");
	if (authenticationDatabase == null || authenticationDatabase.isEmpty()) {
	    authenticationDatabase = getFirtDatabase();
	}

	DatabaseConfigurator databaseConfigurator = InjectedClassesStore.get().getDatabaseConfigurators()
		.get(authenticationDatabase);
	try (Connection connection = databaseConfigurator.getConnection(database);) {

	    if (passwordEncryptor == null) {
		passwordEncryptor = ConfigurablePasswordEncryptorUtil.getConfigurablePasswordEncryptor(properties);
	    }

	    try {
		String encryptedPassword = getEncryptedPassword(authenticationQuery, username, connection);

		if (encryptedPassword == null) {
		    return false;
		}

		return passwordEncryptor.checkPassword(new String(password), encryptedPassword.toLowerCase());
	    } catch (SQLException exception) {
		throw new SQLException(SqlTag.USER_CONFIGURATION
			+ " The dbcUserAuthenticator.authenticationQuery triggers an SQLException: " + exception);
	    }
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
