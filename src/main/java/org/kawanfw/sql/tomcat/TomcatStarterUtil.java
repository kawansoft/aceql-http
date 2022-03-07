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
package org.kawanfw.sql.tomcat;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.lang3.SystemUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.servlet.connection.RollbackUtil;
import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesUtil;
import org.kawanfw.sql.util.SqlTag;

/**
 * @author Nicolas de Pomereu
 *
 *         Utility classes called at Tomcat startup
 */
public class TomcatStarterUtil {

    /** Universal and clean line separator */
    static String CR_LF = System.getProperty("line.separator");

    @SuppressWarnings("unused")
    private static final String ERROR_MESSAGE = "D" + "b" + " V" + "e" + "n" + "d" + "or" + " is" + " " + "no" + "t"
	    + " sup" + "po" + "rt" + "ed" + " in" + " this " + "ver" + "si" + "on " + "fo" + "r Dr" + "iv" + "er: ";

    /**
     * protected constructor
     */
    protected TomcatStarterUtil() {

    }

    /**
     * If the user has created a driverClassName property in the properties file: we
     * create a Tomcat JDBC Pool from the properties
     *
     * @param properties properties extracted from the properties file
     * @throws DatabaseConfigurationException
     * @throws SQLException
     * @throws IOException
     */
    public static void createAndStoreDataSources(Properties properties)
	    throws DatabaseConfigurationException, IOException, SQLException {

	if (properties == null) {
	    throw new IllegalArgumentException("properties is null");
	}

	Set<String> databases = getDatabaseNames(properties);

	for (String database : databases) {
	    createAndStoreDataSource(properties, database.trim());
	}
	
    }

    public static void addServlets(Properties properties, Context rootCtx) throws IOException {

	if (properties == null) {
	    throw new IllegalArgumentException("properties is null");
	}

	//Set<String> servlets = getServlets(properties);
	
	ServletNamesGetter servletNamesGetter = ServletsNamesGetterCreator.createInstance();
	Set<String> servlets= servletNamesGetter.getServlets(properties);
	
	if (servlets.isEmpty()) {
	    return;
	}

	System.out.println(SqlTag.SQL_PRODUCT_START + " Loading servlets:");

	for (String servlet : servlets) {

	    String servletClassName = properties.getProperty(servlet + "." + "class");

	    if (servletClassName == null || servletClassName.isEmpty()) {
		throw new IllegalArgumentException(servlet + ".class" + " property not found for servlet " + servlet
			+ ". " + SqlTag.PLEASE_CORRECT);
	    }

	    servletClassName = servletClassName.trim();

	    String servletUrl = properties.getProperty(servlet + "." + "url-pattern");

	    if (servletUrl == null || servletUrl.isEmpty()) {
		throw new IllegalArgumentException(servlet + ".url-pattern" + " property not found for servlet "
			+ servlet + ". " + SqlTag.PLEASE_CORRECT);
	    }

	    servletUrl = servletUrl.trim();

	    @SuppressWarnings("unused")
	    Wrapper wrapper = Tomcat.addServlet(rootCtx, servlet, servletClassName);
	    rootCtx.addServletMappingDecoded(servletUrl, servlet);

	    System.out.println(SqlTag.SQL_PRODUCT_START + "  -> Servlet " + servlet + " [url-pattern: " + servletUrl
		    + "] successfully loaded.");

	}

    }

    /**
     * Returns the servlets names from the properties
     *
     * @param properties
     * @return servlets names
     */
    public static Set<String> getServlets(Properties properties) {

	String servlets = properties.getProperty("servlets");

	if (servlets == null || servlets.isEmpty()) {
	    return new HashSet<>();
	}

	String[] servletArray = servlets.split(",");

	Set<String> servletSet = new HashSet<>();
	for (int i = 0; i < servletArray.length; i++) {
	    servletSet.add(servletArray[i].trim());
	}
	return servletSet;
    }

    /**
     * Returns the database names from the properties
     *
     * @param properties
     * @return the database names
     * @throws DatabaseConfigurationException
     */
    public static Set<String> getDatabaseNames(Properties properties) throws DatabaseConfigurationException {

	if (properties == null) {
	    throw new IllegalArgumentException("properties is null");
	}

	String databases = properties.getProperty("databases");

	if (databases == null || databases.isEmpty()) {
	    throw new DatabaseConfigurationException(
		    "the databases property is not set in properties file. " + SqlTag.PLEASE_CORRECT);
	}

	String[] databaseArray = databases.split(",");

	Set<String> databaseSet = new HashSet<>();
	for (int i = 0; i < databaseArray.length; i++) {
	    databaseSet.add(databaseArray[i].trim());
	}
	return databaseSet;
    }

    /**
     * If the user has created a driverClassName property in the properties file: we
     * create a Tomcat JDBC Pool from the properties
     *
     * @param properties properties extracted from the properties file
     * @param database   the database name for which to set the properties
     * @throws DatabaseConfigurationException
     * @throws SQLException
     * @throws IOException
     */
    public static void createAndStoreDataSource(Properties properties, String database)
	    throws DatabaseConfigurationException, IOException, SQLException {
	Objects.requireNonNull(properties, "properties cannot be null!");
	Objects.requireNonNull(database, "database cannot be null!");

	String driverClassName = properties.getProperty(database + "." + "driverClassName");
	String url = properties.getProperty(database + "." + "url");
	String username = properties.getProperty(database + "." + "username");
	String password = properties.getProperty(database + "." + "password");

	if (driverClassName == null || driverClassName.isEmpty()) {
	    System.err.println(SqlTag.SQL_PRODUCT_START + " WARNING: driverClassName"
		    + " property not found for database " + database + "! ");
	    System.err.println(SqlTag.SQL_PRODUCT_START
		    + "          Connection management must be defined in DatabaseConfigurator.getConnection(String database)");
	    return;
	}

	checkParameters(database, driverClassName, url, username, password);

	PoolProperties poolProperties = createPoolProperties(properties, database);
	poolProperties = addOurJdbcInterceptor(poolProperties);
	
	DataSource dataSource = new DataSource();
	dataSource.setPoolProperties(poolProperties);

	Connection connection = null;
	try {
	    System.out.println(
		    SqlTag.SQL_PRODUCT_START + " Testing DataSource.getConnection() for " + database + " database:");
	    connection = dataSource.getConnection();
	    
	    if (connection == null) {
		throw new DatabaseConfigurationException(
			"Connection is null. Please verify all the values in properties file.");
	    }
	    
	    if( ConfPropertiesUtil.isStatelessMode() && ! connection.getAutoCommit()) {
		throw new DatabaseConfigurationException("Server is in Stateless Mode: Connection pool must be in default auto commit. Please fix configuration.");
	    }

	    System.out.println(SqlTag.SQL_PRODUCT_START + "  -> Connection OK!");

	} catch (Exception e) {
	    RollbackUtil.rollback(connection);

	    throw new DatabaseConfigurationException(e.getMessage() + " " + e.getCause());
	} finally {
	    if (connection != null) {
		try {
		    connection.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}

	TomcatSqlModeStore.setDataSource(database, dataSource);
    }

    /**
     * Add our AceQLJdbcInterceptor.class to the list of set JdbcInterceptors by the user.
     * @param poolProperties
     */
    public static PoolProperties addOurJdbcInterceptor(PoolProperties poolProperties) {
	String existingJdbcInterceptors = poolProperties.getJdbcInterceptors();
	String jdbcInterceptors = "org.kawanfw.sql.tomcat.AceQLJdbcInterceptor";
	if (existingJdbcInterceptors != null && ! existingJdbcInterceptors.isEmpty()) {
	    jdbcInterceptors+= ";" + existingJdbcInterceptors;
	}

	poolProperties.setJdbcInterceptors(jdbcInterceptors);
	return poolProperties;
    }

    /**
     * @param properties
     * @param database
     * @return
     * @throws DatabaseConfigurationException
     */
    private static PoolProperties createPoolProperties(Properties properties, String database)
	    throws DatabaseConfigurationException {
	// OK! create and test the DataSource
	PoolPropertiesCreator poolPropertiesCreator = new PoolPropertiesCreator(properties, database);
	PoolProperties poolProperties = null;

	try {
	    poolProperties = poolPropertiesCreator.create();
	} catch (Exception e) {
	    throw new DatabaseConfigurationException(e.getMessage());
	}
	return poolProperties;
    }

    /**
     * @param database
     * @param driverClassName
     * @param url
     * @param username
     * @param password
     * @throws DatabaseConfigurationException
     */
    private static void checkParameters(String database, String driverClassName, String url, String username,
	    String password) throws DatabaseConfigurationException, IOException, SQLException {
	if ((url == null) || url.isEmpty()) {
	    throw new DatabaseConfigurationException(
		    "the url property is not set in properties file for driverClassName " + driverClassName + ". "
			    + SqlTag.PLEASE_CORRECT);
	}

	if (username == null || username.isEmpty()) {
	    throw new DatabaseConfigurationException(
		    "the username property is not set in properties file for driverClassName " + driverClassName + ". "
			    + SqlTag.PLEASE_CORRECT);
	}

	// Maybe password is set using an JdbcPasswordManagers implementation
	if (password == null || password.isEmpty()) {
	    throw new DatabaseConfigurationException(
		    "the password property is not set in properties file for driverClassName " + driverClassName + ". "
			    + SqlTag.PLEASE_CORRECT);
	}

	System.out.println(
		SqlTag.SQL_PRODUCT_START + " Setting Tomcat JDBC Pool attributes for " + database + " database:");

    }

    
    /**
     * Safely trim a String
     *
     * @param s the String to trim
     * @return
     */
    public static String trimSafe(final String s) {
	String sNew = s;
	if (sNew != null) {
	    sNew = sNew.trim();
	}

	return sNew;
    }

    /**
     * Checks to see if a specific port is available.
     *
     * @param port the port to check for availability
     */
    public static boolean available(int port) {

	ServerSocket ss = null;
	DatagramSocket ds = null;
	try {
	    ss = new ServerSocket(port);
	    ss.setReuseAddress(true);
	    ds = new DatagramSocket(port);
	    ds.setReuseAddress(true);
	    return true;
	} catch (IOException e) {
	    // e.printStackTrace();
	} finally {
	    if (ds != null) {
		ds.close();
	    }

	    if (ss != null) {
		try {
		    ss.close();
		} catch (IOException e) {
		    /* should not be thrown */
		}
	    }
	}

	return false;
    }

    /**
     * Returns the Java Info at startup
     * 
     * @return the Java info
     */
    public static String getJavaInfo() {
	return SqlTag.SQL_PRODUCT_START + " Java Info: " + SystemUtils.JAVA_VENDOR + " / "
		+ SystemUtils.JAVA_RUNTIME_NAME + " / " + SystemUtils.JAVA_VERSION;
    }

}
