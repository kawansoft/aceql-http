/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.api.server;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.kawanfw.sql.api.server.util.UsernameConverter;
import org.kawanfw.sql.api.server.util.VerySimpleFormatter;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileUtil;
import org.kawanfw.sql.tomcat.TomcatSqlModeStore;
import org.kawanfw.sql.util.Tag;
import org.kawanfw.sql.util.log.FlattenLogger;

/**
 * Default implementation of server side configuration for AceQL.
 * <p>
 * The two fully functional and secured methods are:
 * <ul>
 * <li>{@link #getConnection(String)} that extracts {@code Connections} from a
 * Tomcat JDBC Connection Pool.</li>
 * <li>{@link #close(Connection)} that closes the {@code Connection} and thus
 * releases it into the pool.</li>
 * </ul>
 *
 * @author Nicolas de Pomereu
 */
public class DefaultDatabaseConfigurator implements DatabaseConfigurator {

    /**
     * If {@code true}, allows to "flatten" the log messages to make sure each log entry/message has only 
     * one line (CR/LF of the message will be suppressed). See {@link #getLogger()} code.
     */
    protected boolean flattenLogMessages = true;
    
    /** The map of (database, data sources) to use for connection pooling */
    private Map<String, DataSource> dataSourceSet = new ConcurrentHashMap<>();

    private Properties properties = null;

    private static Logger ACEQL_LOGGER = null;

    /**
     * Returns a {@code Connection} from
     * <a href="http://tomcat.apache.org/tomcat-9.0-doc/jdbc-pool.html" >Tomcat JDBC
     * Connection Pool</a>.<br>
     * <br>
     * the {@code Connection} is extracted from the {@code DataSource} created by
     * the embedded Tomcat JDBC Pool. The JDBC parameters used to create the
     * {@code DataSource} are defined in the properties file passed at start-up of
     * AceQL.
     *
     * @param database the database name to extract the {@code Connection} for.
     *
     * @return the {@code Connection} extracted from Tomcat JDBC Connection Pool.
     */
    @Override
    public Connection getConnection(String database) throws SQLException {

	DataSource dataSource = dataSourceSet.get(database);

	if (dataSource == null) {

	    dataSource = TomcatSqlModeStore.getDataSource(database);

	    if (dataSource == null) {

		if (TomcatSqlModeStore.isTomcatEmbedded()) {

		    String message = Tag.PRODUCT_USER_CONFIG_FAIL
			    + " the \"driverClassName\" property is not defined in the properties file for database "
			    + database + " or the Db Vendor is not supported in this version.";
		    // ServerLogger.getLogger().log(Level.WARNING, message);
		    throw new SQLException(message);
		} else {
		    String message = Tag.PRODUCT_USER_CONFIG_FAIL
			    + " the \"driverClassName\" property is not defined in the properties file for database "
			    + database + " or the servlet name does not match the url pattern in your web.xml";
		    // ServerLogger.getLogger().log(Level.WARNING, message);
		    throw new SQLException(message);
		}
	    }

	    dataSourceSet.put(database, dataSource);

	}

	Connection connection = dataSource.getConnection();
	return connection;
    }

    /**
     * Closes the connection acquired by
     * {@link DatabaseConfigurator#getConnection(String)} with a call to
     * <code>Connection.close()</code>. <br>
     * Note that Exceptions are trapped to avoid client failure. Stack trace is
     * printed on standard error stream.
     */
    @Override
    public void close(Connection connection) throws SQLException {

	try {
	    if (connection != null) {
		connection.close();
	    }
	} catch (Exception e) {
	    try {
		getLogger().log(Level.WARNING, e.toString());
	    } catch (Exception io) {
		// Should never happen
		io.printStackTrace();
	    }
	}

    }

    /**
     * @return the value of the property {@code defaultDatabaseConfigurator.maxRows} defined in the
     *         {@code aceql-server.properties} file at server startup. If property
     *         does not exist, returns 0.
     */
    @Override
    public int getMaxRows(String username, String database) throws IOException, SQLException {

	int maxRows = 0;
	setProperties();

	String maxRowsStr = properties.getProperty("defaultDatabaseConfigurator.maxRows");

	// No limit if not set
	if (maxRowsStr == null) {
	    return 0;
	}

	if (!StringUtils.isNumeric(maxRowsStr)) {
	    throw new IllegalArgumentException(
		    "The defaultDatabaseConfigurator.maxRows property is not numeric: " + maxRowsStr);
	}

	maxRows = Integer.parseInt(maxRowsStr);

	return maxRows;
    }

    /**
     * @return <code>user.home/.aceql-server-root/username</code>. (
     *         {@code user.home} is the one of the servlet container).
     */
    @Override
    public File getBlobsDirectory(final String username) throws IOException, SQLException {
	String userHome = System.getProperty("user.home");
	if (!userHome.endsWith(File.separator)) {
	    userHome += File.separator;
	}

	// Escape invalid chars, mostly for Windows
	String usernameNew = UsernameConverter.fromSpecialChars(username);

	userHome += ".aceql-server-root" + File.separator + usernameNew;
	File userHomeDir = new File(userHome);
	userHomeDir.mkdirs();
	return userHomeDir;
    }

    /**
     * Creates a static {@code Logger} instance.
     *
     * @return a static {@code Logger} with properties:
     *         <ul>
     *         <li>Name: {@code "DefaultDatabaseConfigurator"}.</li>
     *         <li>Output file pattern:
     *         {@code user.home/.kawansoft/log/AceQL.log}.</li>
     *         <li>Formatter: {@code SimpleFormatter}.</li>
     *         <li>Limit: 200Mb.</li>
     *         <li>Count (number of files to use): 2.</li>
     *         </ul>
     */
    @Override
    public Logger getLogger() throws IOException {
	if (ACEQL_LOGGER != null) {
	    return ACEQL_LOGGER;
	}

	File logDir = new File(SystemUtils.USER_HOME + File.separator + ".kawansoft" + File.separator + "log");
	logDir.mkdirs();

	String pattern = logDir.toString() + File.separator + "AceQL.log";

	Logger logger = Logger.getLogger(DefaultDatabaseConfigurator.class.getName());

	if (flattenLogMessages) {
	    ACEQL_LOGGER = new FlattenLogger(logger.getName(), logger.getResourceBundleName());
	} else {
	    ACEQL_LOGGER = logger;
	}

	Handler fh = new FileHandler(pattern, 200 * 1024 * 1024, 2, true);
	fh.setFormatter(new VerySimpleFormatter());
	ACEQL_LOGGER.addHandler(fh);
	return ACEQL_LOGGER;

    }

    /**
     * Sets in memory the Properties of the used {@code aceql-server.properties}
     * file.
     * 
     * @throws IOException
     * @throws DatabaseConfigurationException
     */
    private void setProperties() throws IOException, DatabaseConfigurationException {
	if (properties == null) {
	    File file = PropertiesFileStore.get();
	    properties = PropertiesFileUtil.getProperties(file);
	}
    }

}
