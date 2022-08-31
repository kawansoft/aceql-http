/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.tomcat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.servlet.injection.properties.ConfProperties;
import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesManager;
import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileUtil;
import org.kawanfw.sql.tomcat.util.PortSemaphoreFile;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.SqlTag;

/**
 * Configures Tomcat from the properties file and start it.
 *
 * @author Nicolas de Pomereu
 *
 */
public class TomcatStarter {

    private static boolean DEBUG = FrameworkDebug.isSet(TomcatStarter.class);

    public static String CR_LF = System.getProperty("line.separator");

    /** The file containing all the properties for the Server Session */
    private File propertiesFile = null;

    /** The host of the Web Server */
    private String host = null;

    /** The port of the Web Server */
    private int port = -1;

    /** To use to replace the display of a password */
    public static final String MASKED_PASSWORD = "********";

    /**
     * Constructor
     *
     * @param host           the host of the Web Server
     * @param port           the port of the Web Server
     * @param propertiesFile properties file to use for configuration of the Web
     *                       Server
     *
     */
    public TomcatStarter(String host, int port, File propertiesFile) {

	if (host == null) {
	    throw new IllegalArgumentException("Server host is null!");
	}

	if (port <= 0) {
	    throw new IllegalArgumentException("Server port <= 0!");
	}

	if (propertiesFile == null) {
	    throw new IllegalArgumentException("Server properties file is null!");
	}

	this.host = host;
	this.port = port;
	this.propertiesFile = propertiesFile;
    }

    /**
     * Start the server
     *
     * @throws IOException
     * @throws LifecycleException
     * @throws ConnectException
     * @throws DatabaseConfigurationException
     * @throws SQLException
     */
    public void startTomcat()
	    throws IOException, ConnectException, DatabaseConfigurationException, LifecycleException, SQLException {

	Tomcat tomcat = new Tomcat();
	try {
	    startTomcat(tomcat);
	} finally {
	    try {
		tomcat.stop();
		tomcat.destroy();
	    } catch (Exception e) {
		e.printStackTrace(System.out);
		e.printStackTrace();
	    }
	}

    }

    private void startTomcat(Tomcat tomcat) throws IOException, ConnectException, LifecycleException,
	    MalformedURLException, DatabaseConfigurationException, SQLException {

	TomcatSqlModeStore.setTomcatEmbedded(true);
	
	// To be done at first, everything depends on ir.
	PropertiesFileStore.set(propertiesFile);

	TomcatStarterMessages.printBeginMessage();

	System.out.println(TomcatStarterUtil.getJavaInfo());
	System.out.println(SqlTag.SQL_PRODUCT_START + " " + "Using Properties File: ");
	System.out.println(SqlTag.SQL_PRODUCT_START + "  -> " + propertiesFile);

	Properties properties = PropertiesFileUtil.getProperties(propertiesFile);

	debug("");
	if (DEBUG) {
	    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
		String key = (String) entry.getKey();
		String value = (String) entry.getValue();
		
		if (key.contains("password")) {
		    debug(" In startTomcat --> key / value: " + key + " / " + value);
		}
	    }
	}
	
	String tomcatLoggingLevel = properties.getProperty("tomcatLoggingLevel");

	String level = "SEVERE";
	if (tomcatLoggingLevel != null && !tomcatLoggingLevel.isEmpty()) {
	    level = tomcatLoggingLevel;
	}

	java.util.logging.Logger.getLogger("org.apache").setLevel(Level.parse(level));

	String flushEachResultSetRow = properties.getProperty("flushEachResultSetRow");
	if (flushEachResultSetRow == null || flushEachResultSetRow.isEmpty()) {
	    flushEachResultSetRow = "true";
	}

	System.out.println(SqlTag.SQL_PRODUCT_START + " " + "Setting Internal Properties: ");
	System.out.println(SqlTag.SQL_PRODUCT_START + "  -> tomcatLoggingLevel = " + level);
	System.out.println(SqlTag.SQL_PRODUCT_START + "  -> flushEachResultSetRow = "
		+ Boolean.parseBoolean(flushEachResultSetRow));

	// System.out.println("TomcatEmbedUtil.available(" + port + "): " +
	// TomcatEmbedUtil.available(port));

	// Says to server that we are in a standalone mode:
	TomcatSqlModeStore.setTomcatEmbedded(true);

	// Define Tomcat instance properties
	tomcat.setSilent(true);
	tomcat.setBaseDir(getBaseDir().getAbsolutePath());
	tomcat.setHostname(host);
	tomcat.setPort(port);

	tomcatBeforeStartSetConnectors(tomcat, properties);

	Context rootCtx = tomcatBeforeStartSetContext(tomcat, properties);

	// Create the dataSources if necessary
	TomcatStarterUtil.createAndStoreDataSources(properties);
	TomcatStarterUtil.addServlets(properties, rootCtx);

	// ..and we are good to go
	tomcat.start();

	tomcatAfterStart(tomcat, properties);
    }

    /**
     * @param tomcat
     * @param properties
     * @throws MalformedURLException
     * @throws IOException
     */
    private void tomcatAfterStart(Tomcat tomcat, Properties properties)
	    throws MalformedURLException, IOException, SQLException {
	// System.out.println(SqlTag.SQL_PRODUCT_START);
	Connector defaultConnector = tomcat.getConnector();

	boolean result = testServlet(properties, defaultConnector.getScheme());
	if (!result) {
	    throw new IOException(SqlTag.SQL_PRODUCT_START_FAILURE + " " + "Can not call the AceQL ManagerServlet");
	}

	TomcatStarterMessages.printFinalOkMessage(port);

	// System.out
	// .println(SqlTag.SQL_PRODUCT_START
	// + " To close normally: java org.kawanfw.sql.WebServer -stop -port "
	// + port);

	// System.out.println(SqlTag.SQL_PRODUCT_START
	// + " From command line, use [Ctrl]+[C] to abort abruptly");

	// tomcat.getServer().await();

	// PortSemaphoreFile portSemaphoreFile = new PortSemaphoreFile(port);
	//
	// try {
	// if (!portSemaphoreFile.exists()) {
	// portSemaphoreFile.create();
	// }
	// } catch (IOException e) {
	// throw new IOException("Web server can not start. Impossible to create the
	// semaphore file: "
	// + portSemaphoreFile.getSemaphoreFile() + CR_LF
	// + "Create manually the semapahore file to start the Web server on port " +
	// port + ".", e);
	// }

	// Loop to serve requests
	while (true) {

	    try {
		Thread.sleep(2000);
	    } catch (InterruptedException e) {

	    }

	    // Thread run until terminated by a stop request that creates
	    PortSemaphoreFile portSemaphoreFile = new PortSemaphoreFile(port);
	    if (!portSemaphoreFile.exists()) {
		return;
	    }
	}
    }

    /**
     * Future usage.
     */
    @SuppressWarnings("unused")
    private static void queryExecutorHook() {

	List<String> classNames = new ArrayList<>();
	System.out.println(SqlTag.SQL_PRODUCT_START + " Allowed ServerQueryExecutor: ");
	for (String className : classNames) {
	    System.out.println(SqlTag.SQL_PRODUCT_START + "   -> " + className);
	}

    }

    /**
     * @param tomcat
     * @param properties
     * @return
     * @throws IOException
     */
    private Context tomcatBeforeStartSetContext(Tomcat tomcat, Properties properties) throws IOException, SQLException {
	// Set up context,
	// "" indicates the path of the ROOT context
	Context rootCtx = tomcat.addContext("", getBaseDir().getAbsolutePath());

	// Set the Context
	// TomcatContextUpdater tomcatContextUpdater = new TomcatContextUpdater(
	// rootCtx, properties);
	// tomcatContextUpdater.setContextvalues();

	// Code to force https
	// SecurityConstraint securityConstraint = new SecurityConstraint();
	// securityConstraint.setUserConstraint("CONFIDENTIAL");
	// SecurityCollection collection = new SecurityCollection();
	// collection.addPattern("/*");
	// securityConstraint.addCollection(collection);
	// rootCtx.addConstraint(securityConstraint);

	// Add a predefined Filter
	TomcatFilterUtil.addFilterToContext(rootCtx);

	// Add first servlet with no index
	addAceqlServlet(properties, rootCtx);
	return rootCtx;
    }

    /**
     * @param tomcat
     * @param properties
     * @throws DatabaseConfigurationException
     * @throws ConnectException
     * @throws SQLException
     */
    private void tomcatBeforeStartSetConnectors(Tomcat tomcat, Properties properties)
	    throws DatabaseConfigurationException, ConnectException, SQLException {
	// NO: do in the Creators in org.kawanfw.sql.servlet.injection.classes.creator
	// package
	// TomcatStarterUtil.testConfigurators(properties);

	// Very important to allow port reuse without System.exit()
	// See
	// https://stackoverflow.com/questions/16526027/port-not-getting-free-on-removing-connector-in-embedded-tomcat-7
	tomcat.getConnector().setProperty("bindOnInit", "false"); // HACK

	// Set the System properties
	SystemPropUpdater systemPropUpdater = new SystemPropUpdater(properties);
	systemPropUpdater.update();

	// HACK NDP
	// ProEditionThreadPoolExecutorBuilder threadPoolExecutorStore = new
	// ProEditionThreadPoolExecutorBuilder(properties);
	// threadPoolExecutorStore.create();

	// Set & create connectors
	TomcatConnectorsUpdater tomcatConnectorsUpdater = new TomcatConnectorsUpdater(tomcat, properties);

	tomcatConnectorsUpdater.updateToHttp2Protocol();

	// Set the supplementary default connector values
	tomcatConnectorsUpdater.setConnectorValues();

	// Set the supplementary ssl connector values on the default connector
	tomcatConnectorsUpdater.setDefaultConnectorSslValues();

	// Connector connector = tomcat.getConnector();
	// SSLHostConfig sslHostConfig = new SSLHostConfig();
	// sslHostConfig.setCertificateKeyAlias(certificateKeyAlias);

	// Code to redirect http to https
	// tomcat.getConnector().setRedirectPort(sslPort);
    }

    /**
     * Add a Servlet using properties with the index
     *
     * @param properties the properties than contain all servlet & configurators
     *                   info
     * @param rootCtx    the tomcat root context
     * @throws IOException
     */
    public void addAceqlServlet(Properties properties, Context rootCtx) throws IOException, SQLException {

	if (properties == null) {
	    throw new IllegalArgumentException("properties can not be null");
	}

	// String aceQLManagerServletCallName =
	// TomcatStarterUtil.getAceQLManagerSevletName(properties);
	ServletAceQLCallNameGetter servletAceQLCallNameGetter = AceQLServletCallNameGetterCreator.createInstance();
	String aceQLManagerServletCallName = servletAceQLCallNameGetter.getName();

	// Add the ServerSqlManager servlet to the context
	org.apache.catalina.Wrapper wrapper = Tomcat.addServlet(rootCtx, aceQLManagerServletCallName,
		new ServerSqlManager());
	wrapper.setAsyncSupported(true);
	rootCtx.addServletMappingDecoded("/*", aceQLManagerServletCallName);

	// Create all configuration properties from the Properties and store
	ConfPropertiesManager confPropertiesManager = new ConfPropertiesManager(properties);
	ConfProperties confProperties = confPropertiesManager.createConfProperties();
	ConfPropertiesStore.set(confProperties);

	// Unnecessary because we must start at / because of ou Rest API
	// String serverSqlManagerUrlPattern = serverSqlManagerServletName;
	// if (!serverSqlManagerUrlPattern.startsWith("/")) {
	// serverSqlManagerUrlPattern = "/" + serverSqlManagerUrlPattern;
	// }

    }

    /**
     * Test the servlet
     *
     * @param properties the properties than contain all servlet & configurators
     *                   info
     * @param sslScheme  the ssl scheme
     * @return the status
     *
     * @throws MalformedURLException
     * @throws IOException
     */
    public boolean testServlet(Properties properties, String scheme)
	    throws MalformedURLException, IOException, SQLException {

	// String aceQLManagerServletCallName =
	// TomcatStarterUtil.getAceQLManagerSevletName(properties);
	ServletAceQLCallNameGetter servletAceQLCallNameGetter = AceQLServletCallNameGetterCreator.createInstance();
	String aceQLManagerServletCallName = servletAceQLCallNameGetter.getName();

	String serverSqlManagerUrlPattern = aceQLManagerServletCallName;
	serverSqlManagerUrlPattern = serverSqlManagerUrlPattern.trim();

	if (!serverSqlManagerUrlPattern.startsWith("/")) {
	    serverSqlManagerUrlPattern = "/" + serverSqlManagerUrlPattern;
	}

	String url = scheme + "://" + host + ":" + port + serverSqlManagerUrlPattern;

	String loadAceQLManagerServletOnStartup = properties.getProperty("loadAceQLManagerServletOnStartup", "true");

	if (loadAceQLManagerServletOnStartup == null || loadAceQLManagerServletOnStartup.isEmpty()
		|| !Boolean.parseBoolean(loadAceQLManagerServletOnStartup)) {
	    System.out.println(SqlTag.SQL_PRODUCT_START + " URL for client side: " + url);
	    return true;
	}

	// If asked! Call the ServerSqlManagerServlet to test everything is OK.
	String serverSqlManagerstatus = callServerSqlManagerServlet(url);

	if (serverSqlManagerstatus.contains("\"OK\"")) {
	    System.out.println(SqlTag.SQL_PRODUCT_START + " URL for client side (tested): " + url);
	    return true;
	}

	return false;
    }

    /**
     * Call the Server SQL Manager Servlet to test everything is OK.
     *
     * @param url the url of the servlet
     *
     * @return the return status. "Should be OK.
     * @throws MalformedURLException
     * @throws IOException
     */
    private String callServerSqlManagerServlet(String url) throws MalformedURLException, IOException {
	URL theUrl = new URL(url);
	URLConnection urLconnection = theUrl.openConnection();

	BufferedReader br = new BufferedReader(new InputStreamReader(urLconnection.getInputStream()));
	String inputLine;

	String serverSqlManagerstatus = "";

	// There is only one line:
	while ((inputLine = br.readLine()) != null) {
	    serverSqlManagerstatus += inputLine + CR_LF;
	}
	br.close();
	return serverSqlManagerstatus;
    }

    /**
     * Create a user.home/.kawansoft/tomcat-embedded-temp directory This will be
     * used by Tomcat for temporary files.
     *
     * @return user.home/.kawansoft/tomcat-embedded-temp directory
     */
    private File getBaseDir() {

	String userHome = System.getProperty("user.home");
	if (!userHome.endsWith(File.separator)) {
	    userHome += File.separator;
	}

	File baseDir = new File(userHome + ".kawansoft" + File.separator + "tomcat-embedded-temp");
	baseDir.mkdirs();

	return baseDir;
    }

    /**
     * debug
     */
    public static void debug(String s) {
	if (DEBUG) {
	    System.out.println(s);
	}
    }

}
