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
package org.kawanfw.test.parms;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.kawanfw.sql.api.util.SqlUtil;
import org.kawanfw.sql.util.FrameworkFileUtil;
import org.kawanfw.sql.util.FrameworkSystemUtil;
import org.kawanfw.sql.util.JdbcUrlHeader;
import org.kawanfw.sql.util.Tag;
import org.kawanfw.test.util.MessageDisplayer;

/**
 * Defines the AceQL Connection parameters to test AceQL and get a Connection to
 * a remote platform.
 * 
 * @author Nicolas de Pomereu
 * 
 */
public class SqlTestParms {

    public static boolean USE_PROXY = true;

    /**
     * If true, ConnectionLoader will use a remote AceQL Connection. If false, a
     * local JDBC Connection
     */
    public static boolean REMOTE = true;

    public static boolean ACCEPT_ALL_SSL_CERTIFICATES = true;

    /** if true, request will be encrypted */
    public static final boolean USE_ENCRYPTION_PASSWORD = false;
    public static final String ENCRYPTION_PASSWORD = "encryption_password_123";

    /**
     * If you want to test only one database, set it here and call
     * SqlTestRunnerConsoleOneDatabase
     */
    public static final String ONE_DATATBASE_TO_TEST = SqlUtil.POSTGRESQL;

    /** SQL Engines */
    // SQL Server-jtds

    public static String SQLSERVER_JTDS_DRIVER = SqlUtil.SQL_SERVER + "-jtds";

    // SQL Server-ms
    public static String SQLSERVER_MS_DRIVER = SqlUtil.SQL_SERVER + "-ms";

    // SQL Server-ms
    public static String MARIADB = "MARIADB";

    /** Methods to load the driver */
    public static int DRIVER_LOADING_NEW_INSTANCE = 1;
    public static int DRIVER_LOADING_REGISTER = 2;

    /** If true, the AceQL Driver will be loaded **/
    public static int DRIVER_LOADING_METHOD = DRIVER_LOADING_REGISTER;

    /** If true, The test will run in loop mode (to detect memory leaks) */
    public static boolean LOOP_MODE = false;

    /** Number of rows to insert */
    public static int ROWS_TO_INSERT = 10;

    /** Update to define the engines to test */
    public static String[] SQL_ENGINES_TO_TEST = {
	    // SqlUtil.ACCESS, // We have only a 32 bits version...
	    // SqlUtil.ADAPTIVE_SERVER_ENTERPRISE, //Connection Hangs
	    // SqlUtil.SQL_ANYWHERE,
	    SqlUtil.DB2,
	    // SqlUtil.H2,
	    // SqlUtil.HSQLDB, // Forgot how to launch
	    // SqlUtil.INGRES,
	    // SqlUtil.INFORMIX,
	    MARIADB, SqlUtil.MYSQL, SqlUtil.POSTGRESQL, SQLSERVER_JTDS_DRIVER,
	    SQLSERVER_MS_DRIVER, SqlUtil.ORACLE,
	    // SqlUtil.TERADATA // Requires a separated VM Ware!
    };

    /** Remote JDBC parameters */

    public static String ACEQL_URL = JdbcUrlHeader.JDBC_URL_HEADER
	    + "http://localhost:8080/aceql-test/ServerSqlManager";
    public static String ACEQL_PRO_URL = JdbcUrlHeader.JDBC_URL_HEADER
	    + "http://localhost:8080/aceql-pro-test/ServerSqlManager";
    public static String ACEQL_URL_2 = JdbcUrlHeader.JDBC_URL_HEADER
	    + "http://localhost:8080/aceql-test/ServerSqlManager2";

    public static String ACEQL_URL_LINUX = JdbcUrlHeader.JDBC_URL_HEADER
	    + "http://www.aceql.com/aceql-test/ServerSqlManager";
    public static String ACEQL_URL_LINUX_SSL = JdbcUrlHeader.JDBC_URL_HEADER
	    + "https://www.aceql.com/aceql-test/ServerSqlManager";

    public static String ACEQL_URL_TOMCAT_EMBEDED_LINUX = JdbcUrlHeader.JDBC_URL_HEADER
	    + "http://www.aceql.com:9090/ServerSqlManager";

    public static String ACEQL_URL_TOMCAT_EMBEDED_LOCALHOST = JdbcUrlHeader.JDBC_URL_HEADER
	    + "http://localhost:9090/aceql";
    public static String ACEQL_URL_TOMCAT_EMBEDED_LOCALHOST_SSL = JdbcUrlHeader.JDBC_URL_HEADER
	    + "https://localhost:9443/ServerSqlManager";
    public static String ACEQL_URL_TOMCAT_EMBEDED_LOCALHOST_SSL_2 = JdbcUrlHeader.JDBC_URL_HEADER
	    + "https://localhost:9443/ServerSqlManager2";

    public static String ACEQL_URL_TOMCAT_EMBEDED_REMOTE = JdbcUrlHeader.JDBC_URL_HEADER
	    + "http://www.aceql.com:9090/ServerSqlManager";
    public static String ACEQL_URL_TOMCAT_EMBEDED_REMOTE_SSL = JdbcUrlHeader.JDBC_URL_HEADER
	    + "https://www.aceql.com:9443/ServerSqlManager";
    public static String ACEQL_URL_TOMCAT_EMBEDED_AMAZON_CLOUD = JdbcUrlHeader.JDBC_URL_HEADER
	    + "http://ec2-54-72-127-25.eu-west-1.compute.amazonaws.com:9090/ServerSqlManager";

    public static String REMOTE_USER = "username";
    public static String REMOTE_PASSWORD = "password";

    /** The image to use as blobs files in user.home/kawanfw-test directory */
    public static String BIG_BLOG_56_MB = "javadoc-java6.zip";
    public static String BIG_BLOG_20_MB = "src.zip";
    public static String BIG_BLOG_174_MB = "eclipse-SDK-3.7.1-win32-x86_64.zip";
    public static String BIG_BLOG_312_MB = "OracleXE112_Win32.zip";

    public static String TULIPS = "Tulips.jpg";
    public static String KOALA = "Koala.jpg";
    public static String RUSSIAN = "РAССЫЛОК.txt";

    public static String BLOB_FILE_1 = TULIPS;
    public static String BLOB_FILE_2 = KOALA;

    public static String TEXT_FILE_1 = "aceql-text-1.txt";
    public static String TEXT_FILE_2 = "aceql-text-2.txt";

    // Android Environment Class for file manipulation
    private static Class<?> environmentClass;

    // Method Environment Class Android class for file manipulation
    private static Method getExternalStorageDirectoryMethod;

    /**
     * @param imageFileName
     *            the name of the image file to use
     * @return The first image file to use as blob for insert and select
     */
    public static File getFileFromUserHome(String imageFileName) {
	String imageFileStr = FrameworkFileUtil.getUserHome() + File.separator
		+ "kawanfw-test" + File.separator + imageFileName;

	if (FrameworkSystemUtil.isAndroid()) {

	    // Android code made by reflection to avoid compilation issue
	    // File envStore = Environment.getExternalStorageDirectory();
	    // imageFileStr = envStore.getPath() + File.separator +
	    // imageFileName;
	    // MessageDisplayer.display("imageFileStr: " + imageFileStr);

	    try {
		environmentClass = Class.forName("android.os.Environment");
		getExternalStorageDirectoryMethod = environmentClass
			.getMethod("getExternalStorageDirectory");

		File envStore = (File) getExternalStorageDirectoryMethod
			.invoke(environmentClass);
		imageFileStr = envStore.getPath() + File.separator
			+ imageFileName;
		MessageDisplayer.display("imageFileStr: " + imageFileStr);

	    } catch (Exception e) {
		throw new IllegalArgumentException(Tag.PRODUCT_PRODUCT_FAIL
			+ "Impossible to load method environmentClass. Cause: "
			+ e.toString());
	    }

	}

	if (!new File(imageFileStr).exists()) {
	    MessageDisplayer.display(Tag.PRODUCT_WARNING
		    + "Blob/Clob file does not exists: " + imageFileStr);
	}

	return new File(imageFileStr);
    }

    /**
     * 
     * @return true if we use a remote connection
     */
    public static boolean isRemote() {
	return REMOTE;
    }

    /**
     * @return
     * @throws SQLException
     * @throws Exception
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */

    static Connection getConnectionWithUrl(String url)
	    throws SQLException, Exception, ClassNotFoundException,
	    InstantiationException, IllegalAccessException {

	// For a local connection
	if (!isRemote()) {
	    Connection connection = ConnectionLoader.getLocalConnection();
	    return connection;
	}

	Connection connection = null;

	if (DRIVER_LOADING_METHOD == DRIVER_LOADING_NEW_INSTANCE) {
	    System.out.println(
		    "Loading Driver with Class.forName(\"org.kawanfw.sql.api.client.RemoteDriver\")");
	    String driverClassName = "org.kawanfw.sql.api.client.RemoteDriver";

	    Class<?> c = Class.forName(driverClassName);

	    // Driver driver = (Driver) c.newInstance();
	    Constructor<?> constructor = c.getConstructor();
	    Driver driver = (Driver) constructor.newInstance();

	    Properties properties = new Properties();
	    properties.setProperty("user", REMOTE_USER);
	    properties.setProperty("password", REMOTE_PASSWORD);

	    if (USE_ENCRYPTION_PASSWORD) {
		properties.setProperty("encryptionPassword",
			ENCRYPTION_PASSWORD);
	    }

	    // properties.setProperty("maxLengthForString", ""
	    // + (DefaultParms.DEFAULT_MAX_LENGTH_FOR_STRING + 5));
	    // properties.setProperty("readBufferSize", ""
	    // + (DefaultParms.DEFAULT_READ_BUFFER_SIZE + 6));
	    // properties.setProperty("writeBufferSize", ""
	    // + (DefaultParms.DEFAULT_WRITE_BUFFER_SIZE + 7));
	    // properties.setProperty("acceptAllSslCertificates", "" + true);

	    properties.setProperty("http-client-http.socket.timeout",
		    "[java.lang.Integer, 1000000]");
	    properties.setProperty("http-client-http.useragent",
		    "AceQL User-Agent");

	    connection = driver.connect(url, properties);

	} else if (DRIVER_LOADING_METHOD == DRIVER_LOADING_REGISTER) {

	    /*
	     * ProxyLoader proxyLoader = new ProxyLoader(); Proxy proxy =
	     * proxyLoader.getProxy(); PasswordAuthentication
	     * passwordAuthentication = proxyLoader.getPasswordAuthentication();
	     * 
	     * System.out.println("proxy: " + proxy);
	     * System.out.println("passwordAuthentication: " +
	     * passwordAuthentication);
	     * 
	     * SessionParameters sessionParameters = new SessionParameters();
	     * 
	     * String proxyUsername = null; String proxyPassword = null;
	     * 
	     * if (passwordAuthentication != null) { proxyUsername =
	     * passwordAuthentication.getUserName(); proxyPassword = new
	     * String(passwordAuthentication.getPassword()); }
	     * 
	     * if (USE_ENCRYPTION_PASSWORD) { sessionParameters
	     * .setEncryptionPassword(ENCRYPTION_PASSWORD .toCharArray()); }
	     * 
	     * sessionParameters .setAcceptAllSslCertificates(SqlTestParms.
	     * ACCEPT_ALL_SSL_CERTIFICATES);
	     * 
	     * System.out
	     * .println("Loading Driver with DriverManager.getConnection(url, properties)"
	     * );
	     * 
	     * Properties properties = new Properties();
	     * properties.setProperty("user", REMOTE_USER);
	     * properties.setProperty("password", REMOTE_PASSWORD);
	     * 
	     * properties.setProperty("sessionParameters",
	     * sessionParameters.toString());
	     * 
	     * if (proxy != null) { properties.setProperty("proxy",
	     * proxy.toString()); }
	     * 
	     * if (passwordAuthentication != null) {
	     * properties.setProperty("proxyUsername", proxyUsername);
	     * properties.setProperty("proxyPassword", proxyPassword); }
	     * 
	     * // properties.setProperty("joinResultSetMetaData", "true");
	     * 
	     * // Required for Android, optional for others environments:
	     * Class.forName("org.kawanfw.sql.api.client.RemoteDriver");
	     * 
	     * connection = DriverManager.getConnection(url, properties);
	     */
	} else {
	    throw new IllegalArgumentException(
		    "Loading method not supported: " + DRIVER_LOADING_METHOD);
	}

	return connection;
    }

}
