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
package org.kawanfw.sql.tomcat;

import static org.kawanfw.sql.servlet.ServerSqlManager.DATABASE_CONFIGURATOR_CLASS_NAME;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.Servlet;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.servlet.sql.DbEngineManager;
import org.kawanfw.sql.tomcat.util.LinkedProperties;
import org.kawanfw.sql.util.SqlTag;

/**
 * @author Nicolas de Pomereu
 *
 *         Utility classes called at Tomcat startup
 */
public class TomcatStarterUtil {

    /** Universal and clean line separator */
    private static String CR_LF = System.getProperty("line.separator");

    private static final String ERROR_MESSAGE = "D" + "b" + " V" + "e" + "n"
	    + "d" + "or" + " is" + " " + "no" + "t" + " sup" + "po" + "rt"
	    + "ed" + " in" + " this " + "ver" + "si" + "on " + "fo" + "r Dr"
	    + "iv" + "er: ";



    /**
     * protected constructor
     */
    protected TomcatStarterUtil() {

    }

    /**
     * If the user has created a driverClassName property in the properties
     * file: we create a Tomcat JDBC Pool from the properties
     *
     * @param properties
     *            properties extracted from the properties file
     * @throws DatabaseConfigurationException
     */
    public static void createAndStoreDataSources(Properties properties)
	    throws DatabaseConfigurationException {

	if (properties == null) {
	    throw new IllegalArgumentException("properties is null");
	}

	Set<String> databases = getDatabaseNames(properties);

	for (String database : databases) {
	    createAndStoreDataSource(properties, database.trim());
	}

    }

    public static void testConfigurators(Properties properties) {

	if (properties == null) {
	    throw new IllegalArgumentException("properties is null");
	}

	System.out.println(
		SqlTag.SQL_PRODUCT_START + " Testing Declared Configurators:");

	Set<String> databases = getDatabaseNames(properties);
	for (String database : databases) {
	    // Database configurator
	    String databaseConfiguratorClassName = properties.getProperty(
		    database + "." + DATABASE_CONFIGURATOR_CLASS_NAME);

	    if (databaseConfiguratorClassName != null) {
		loadInstance(databaseConfiguratorClassName);

		System.out.println(SqlTag.SQL_PRODUCT_START + "  -> " + database
			+ " Database Configurator " + CR_LF
			+ SqlTag.SQL_PRODUCT_START + "     "
			+ databaseConfiguratorClassName + " OK.");
	    }
	}

	String className = properties.getProperty(
		ServerSqlManager.BLOB_DOWNLOAD_CONFIGURATOR_CLASS_NAME);

	if (className != null) {
	    loadInstance(className);

	    System.out.println(SqlTag.SQL_PRODUCT_START + "  -> Configurator "
		    + className + " OK.");
	}

	className = properties.getProperty(
		ServerSqlManager.BLOB_UPLOAD_CONFIGURATOR_CLASS_NAME);

	if (className != null) {
	    loadInstance(className);

	    System.out.println(SqlTag.SQL_PRODUCT_START + "  -> Configurator "
		    + className + " OK.");
	}

	className = properties
		.getProperty(ServerSqlManager.SESSION_CONFIGURATOR_CLASS_NAME);

	if (className != null) {
	    loadInstance(className);

	    System.out.println(SqlTag.SQL_PRODUCT_START + "  -> Configurator "
		    + className + " OK.");
	}

    }

    private static void loadInstance(String configuratorClassName) {
	Class<?> c = null;

	try {
	    c = Class.forName(configuratorClassName);

	    // @SuppressWarnings("unused")
	    // Object theObject = c.newInstance();
	    Constructor<?> constructor = c.getConstructor();
	    @SuppressWarnings("unused")
	    Object theObject = constructor.newInstance();

	} catch (Exception e) {
	    throw new IllegalArgumentException(
		    "Exception when loading Configurator "
			    + configuratorClassName + ": " + e.toString() + ". "
			    + SqlTag.PLEASE_CORRECT,
		    e);
	}

    }

    public static void addServlets(Properties properties, Context rootCtx) {

	if (properties == null) {
	    throw new IllegalArgumentException("properties is null");
	}

	Set<String> servlets = getServlets(properties);

	if (servlets.isEmpty()) {
	    return;
	}

	System.out.println(SqlTag.SQL_PRODUCT_START + " Loading servlets:");

	for (String servlet : servlets) {

	    String servletClassName = properties
		    .getProperty(servlet + "." + "class");

	    if (servletClassName == null || servletClassName.isEmpty()) {
		throw new IllegalArgumentException(
			servlet + ".class" + " property not found for servlet "
				+ servlet + ". " + SqlTag.PLEASE_CORRECT);
	    }

	    servletClassName = servletClassName.trim();

	    String servletUrl = properties
		    .getProperty(servlet + "." + "url-pattern");

	    if (servletUrl == null || servletUrl.isEmpty()) {
		throw new IllegalArgumentException(servlet + ".url-pattern"
			+ " property not found for servlet " + servlet + ". "
			+ SqlTag.PLEASE_CORRECT);
	    }

	    servletUrl = servletUrl.trim();

	    Class<?> c = null;
	    Servlet servletInstance = null;

	    try {
		c = Class.forName(servletClassName);

		// servletInstance = (Servlet) c.newInstance();
		Constructor<?> constructor = c.getConstructor();
		servletInstance = (Servlet) constructor.newInstance();

	    } catch (Exception e) {
		throw new IllegalArgumentException("Exception when loading "
			+ servletClassName + " (servlet " + servlet + "): "
			+ e.toString() + ". " + SqlTag.PLEASE_CORRECT, e);
	    }

	    System.out.println(SqlTag.SQL_PRODUCT_START + "  -> Servlet "
		    + servlet + " [url-pattern: " + servletUrl
		    + "] successfully loaded.");

	    @SuppressWarnings("unused")
	    Wrapper wrapper = Tomcat.addServlet(rootCtx, servlet,
		    servletInstance);

	    rootCtx.addServletMappingDecoded(servletUrl, servlet);

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
    public static Set<String> getDatabaseNames(Properties properties)
	    throws DatabaseConfigurationException {

	if (properties == null) {
	    throw new IllegalArgumentException("properties is null");
	}

	String databases = properties.getProperty("databases");

	if (databases == null || databases.isEmpty()) {
	    throw new DatabaseConfigurationException(
		    "the databases property is not set in properties file. "
			    + SqlTag.PLEASE_CORRECT);
	}

	String[] databaseArray = databases.split(",");

	Set<String> databaseSet = new HashSet<>();
	for (int i = 0; i < databaseArray.length; i++) {
	    databaseSet.add(databaseArray[i].trim());
	}
	return databaseSet;
    }

    /**
     * If the user has created a driverClassName property in the properties
     * file: we create a Tomcat JDBC Pool from the properties
     *
     * @param properties
     *            properties extracted from the properties file
     * @param database
     *            the database name for which to set the properties
     * @throws DatabaseConfigurationException
     */
    public static void createAndStoreDataSource(Properties properties,
	    String database) throws DatabaseConfigurationException {

	if (properties == null) {
	    throw new IllegalArgumentException("properties is null");
	}

	if (database == null) {
	    throw new IllegalArgumentException("database is null");
	}

	database = database.trim();
	String driverClassName = properties
		.getProperty(database + "." + "driverClassName");

	if (driverClassName == null || driverClassName.isEmpty()) {
	    System.err.println(SqlTag.SQL_PRODUCT_START
		    + " WARNING: driverClassName"
		    + " property not found for database " + database + "! ");
	    System.err.println(SqlTag.SQL_PRODUCT_START
		    + "          Connection management must be defined in DatabaseConfigurator.getConnection(String database)");
	    return;
	}

	String url = properties.getProperty(database + "." + "url");

	if ((url == null) || url.isEmpty()) {
	    throw new DatabaseConfigurationException(
		    "the url property is not set in properties file for driverClassName "
			    + driverClassName + ". " + SqlTag.PLEASE_CORRECT);
	}

	String username = properties.getProperty(database + "." + "username");
	if ((username == null) || username.isEmpty()) {
	    throw new DatabaseConfigurationException(
		    "the username property is not set in properties file for driverClassName "
			    + driverClassName + ". " + SqlTag.PLEASE_CORRECT);
	}

	String password = properties.getProperty(database + "." + "password");
	if ((password == null) || password.isEmpty()) {
	    throw new DatabaseConfigurationException(
		    "the password property is not set in properties file for driverClassName "
			    + driverClassName + ". " + SqlTag.PLEASE_CORRECT);
	}

	System.out.println(SqlTag.SQL_PRODUCT_START
		+ " Setting Tomcat JDBC Pool attributes for " + database
		+ " database:");

	// OK! create and test the DataSource
	PoolPropertiesCreator poolPropertiesCreator = new PoolPropertiesCreator(
		properties, database);
	PoolProperties poolProperties = null;

	try {
	    poolProperties = poolPropertiesCreator.create();
	} catch (Exception e) {
	    throw new DatabaseConfigurationException(e.getMessage());
	}

	DataSource dataSource = new DataSource();
	dataSource.setPoolProperties(poolProperties);

	Connection connection = null;

	try {
	    try {
		System.out.println(SqlTag.SQL_PRODUCT_START
			+ " Testing DataSource.getConnection() for " + database
			+ " database:");
		connection = dataSource.getConnection();

		// Connection connection2 = dataSource.getConnection();
		// System.out.println("dataSource.getActive()): " +
		// dataSource.getActive());
		// connection2.close();

		if (connection == null) {
		    throw new DatabaseConfigurationException(
			    "Connection is null. Please verify all the values in properties file.");
		}

		// Futur usage
		// Checks that DB Vendor is supported
		boolean isOk = DbEngineManager.checkDb(properties, connection);
		if (!isOk) {
		    System.err.println(ERROR_MESSAGE + driverClassName);
		    TomcatSqlModeStore.setDataSource(database, null);
		    return;
		}

		System.out.println(
			SqlTag.SQL_PRODUCT_START + "  -> Connection OK!");

	    } catch (SQLException e) {
		throw new DatabaseConfigurationException(
			e.getMessage() + " " + e.getCause());
	    }
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
     * Returns the Properties extracted from a file.
     *
     * @param file
     *            the file containing the properties
     * @return the Properties extracted from the file
     *
     * @throws IOException
     * @throws DatabaseConfigurationException
     */
    public static Properties getProperties(File file)
	    throws IOException, DatabaseConfigurationException {

	if (file == null) {
	    throw new IllegalArgumentException("file can not be null!");
	}

	if (!file.exists()) {
	    throw new DatabaseConfigurationException(
		    "properties file not found: " + file);
	}

	// Get the properties with order of position in file:
	Set<String> linkedProperties = LinkedProperties
		.getLinkedPropertiesName(file);

	// Create the ordered properties
	Properties properties;

	try (InputStream in = new FileInputStream(file);) {

	    properties = new LinkedProperties(linkedProperties);
	    properties.load(in);
	}

	return properties;
    }

    /**
     * Set the servlet parameters store with the values extracted from the
     * Properties.
     *
     * @param properties
     *            Properties extracted from the server-sql.properties files
     * @throws IllegalArgumentException
     */
    public static void setInitParametersInStore(Properties properties)
	    throws IllegalArgumentException {

	ServletParametersStore.init(); // Set back to null static values

	String aceQLManagerServletCallName = TomcatStarterUtil
		.getAceQLManagerSevletName(properties);

	ServletParametersStore.setServletName(aceQLManagerServletCallName);
	Set<String> databases = getDatabaseNames(properties);
	ServletParametersStore.setDatabaseNames(databases);

	for (String database : databases) {
	    // Set the configurator to use for this database
	    String databaseConfiguratorClassName = TomcatStarterUtil
		    .trimSafe(properties.getProperty(
			    database + "." + DATABASE_CONFIGURATOR_CLASS_NAME));

	    if (databaseConfiguratorClassName != null
		    && !databaseConfiguratorClassName.isEmpty()) {
		ServletParametersStore.setInitParameter(database,
			new InitParamNameValuePair(
				DATABASE_CONFIGURATOR_CLASS_NAME,
				databaseConfiguratorClassName));
	    }

	    String sqlFirewallClassNameArray = TomcatStarterUtil
		    .trimSafe(properties.getProperty(
			    database + "." + ServerSqlManager.SQL_FIREWALL_MANAGER_CLASS_NAMES));

	    if (sqlFirewallClassNameArray != null && ! sqlFirewallClassNameArray.isEmpty()) {
		List<String> sqlFirewallClassNames = TomcatStarterUtilFirewall.getList(sqlFirewallClassNameArray);
		ServletParametersStore.setSqlFirewallClassNames(database, sqlFirewallClassNames);
	    }
	    else {
		ServletParametersStore.setSqlFirewallClassNames(database, new ArrayList<String>());
	    }
	}

	String blobDownloadConfiguratorClassName = TomcatStarterUtil
		.trimSafe(properties.getProperty(
			ServerSqlManager.BLOB_DOWNLOAD_CONFIGURATOR_CLASS_NAME));
	ServletParametersStore.setBlobDownloadConfiguratorClassName(
		blobDownloadConfiguratorClassName);

	String blobUploadConfiguratorClassName = TomcatStarterUtil
		.trimSafe(properties.getProperty(
			ServerSqlManager.BLOB_UPLOAD_CONFIGURATOR_CLASS_NAME));
	ServletParametersStore.setBlobUploadConfiguratorClassName(
		blobUploadConfiguratorClassName);

	String sessionConfiguratorClassName = TomcatStarterUtil
		.trimSafe(properties.getProperty(
			ServerSqlManager.SESSION_CONFIGURATOR_CLASS_NAME));
	ServletParametersStore
		.setSessionConfiguratorClassName(sessionConfiguratorClassName);

	String jwtSessionConfiguratorSecretValue = TomcatStarterUtil
		.trimSafe(properties.getProperty(
			ServerSqlManager.JWT_SESSION_CONFIGURATOR_SECRET));

	ServletParametersStore.setJwtSessionConfiguratorSecretValue(
		jwtSessionConfiguratorSecretValue);
    }

    /**
     * Safely trim a String
     *
     * @param s
     *            the String to trim
     * @return
     */
    public static String trimSafe(String s) {
	if (s != null) {
	    s = s.trim();
	}

	return s;
    }

    /**
     * Checks to see if a specific port is available.
     *
     * @param port
     *            the port to check for availability
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

    static String getAceQLManagerSevletName(Properties properties) {
	String aceQLManagerServletCallName = properties
		.getProperty("aceQLManagerServletCallName");

	// Support old name:
	if (aceQLManagerServletCallName == null
		|| aceQLManagerServletCallName.isEmpty()) {
	    aceQLManagerServletCallName = properties
		    .getProperty("serverSqlManagerServletName");
	}

	if (aceQLManagerServletCallName == null
		|| aceQLManagerServletCallName.isEmpty()) {
	    throw new DatabaseConfigurationException(
		    "aceQLManagerServletCallName property is null. "
			    + SqlTag.PLEASE_CORRECT);
	}

	if (aceQLManagerServletCallName.contains("/")) {
	    throw new DatabaseConfigurationException(
		    "aceQLManagerServletCallName property can not contain \"/\" separator. "
			    + SqlTag.PLEASE_CORRECT);
	}

	aceQLManagerServletCallName = aceQLManagerServletCallName.trim();
	return aceQLManagerServletCallName;
    }

}
