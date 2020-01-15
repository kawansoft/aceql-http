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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.tomcat.util.PortSemaphoreFile;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.SqlTag;
import org.kawanfw.sql.version.Version;

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
     * @param host
     *            the host of the Web Server
     * @param port
     *            the port of the Web Server
     * @param propertiesFile
     *            properties file to use for configuration of the Web Server
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
	    throw new IllegalArgumentException(
		    "Server properties file is null!");
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
     */
    public void startTomcat() throws IOException,
	    ConnectException, DatabaseConfigurationException, LifecycleException {

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

    private void startTomcat(Tomcat tomcat) throws IOException,
	    ConnectException, LifecycleException, MalformedURLException {
	System.out.println(SqlTag.SQL_PRODUCT_START + " Starting "
		+ Version.PRODUCT.NAME + " Web Server...");
	System.out.println(
		SqlTag.SQL_PRODUCT_START + " " + Version.getServerVersion());
	System.out.println(
		SqlTag.SQL_PRODUCT_START + " " + "Using properties file: ");
	System.out.println(SqlTag.SQL_PRODUCT_START + "  -> " + propertiesFile);

	Properties properties = TomcatStarterUtil.getProperties(propertiesFile);

	// System.out.println("TomcatEmbedUtil.available(" + port + "): " +
	// TomcatEmbedUtil.available(port));

	// Says to server that we are in a standalone mode:
	TomcatSqlModeStore.setTomcatEmbedded(true);

	// Define Tomcat instance properties
	tomcat.setSilent(true);
	tomcat.setBaseDir(getBaseDir().getAbsolutePath());
	tomcat.setHostname(host);
	tomcat.setPort(port);

	TomcatStarterUtil.testConfigurators(properties);

	// Very important to allow port reuse without System.exit()
	// See
	// https://stackoverflow.com/questions/16526027/port-not-getting-free-on-removing-connector-in-embedded-tomcat-7
	tomcat.getConnector().setProperty("bindOnInit", "false"); // HACK

	// Set the System properties
	SystemPropUpdater systemPropUpdater = new SystemPropUpdater(properties);
	systemPropUpdater.update();

	ThreadPoolExecutorStore threadPoolExecutorStore= new ThreadPoolExecutorStore(properties);
	threadPoolExecutorStore.create();

	// Set & create connectors
	TomcatConnectorsUpdater tomcatConnectorsUpdater = new TomcatConnectorsUpdater(
		tomcat, properties);

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

	// Create the dataSources if necessary
	TomcatStarterUtil.createAndStoreDataSources(properties);

	TomcatStarterUtil.addServlets(properties, rootCtx);

	// ..and we are good to go
	tomcat.start();

	// System.out.println(SqlTag.SQL_PRODUCT_START);
	Connector defaultConnector = tomcat.getConnector();
	@SuppressWarnings("unused")
	String result = testServlet(properties, defaultConnector.getScheme());

	String runningMessage = SqlTag.SQL_PRODUCT_START + " "
		+ Version.PRODUCT.NAME + " Web Server OK. Running on port "
		+ port;

	System.out.println(runningMessage);
	System.out.println();

	// System.out
	// .println(SqlTag.SQL_PRODUCT_START
	// + " To close normally: java org.kawanfw.sql.WebServer -stop -port "
	// + port);

	// System.out.println(SqlTag.SQL_PRODUCT_START
	// + " From command line, use [Ctrl]+[C] to abort abruptly");

	// tomcat.getServer().await();

	// Loop to serve requests
	while (true) {

	    try {
		Thread.sleep(2000);
	    } catch (InterruptedException e) {

	    }

	    // Thread run until terminated by a stop request that creates
	    // PortSemaphoreFile
	    PortSemaphoreFile portSemaphoreFile = new PortSemaphoreFile(port);
	    if (!portSemaphoreFile.exists()) {
		return;
	    }
	}
    }

    /**
     * Add a Servlet using properties with the index
     * 
     * @param properties
     *            the properties than contain all servlet & configurators info
     * @param rootCtx
     *            the tomcat root context
     */
    public void addAceqlServlet(Properties properties, Context rootCtx) {

	if (properties == null) {
	    throw new IllegalArgumentException("properties can not be null");
	}

	String aceQLManagerServletCallName = TomcatStarterUtil
		.getAceQLManagerSevletName(properties);

	// Add the ServerSqlManager servlet to the context
	@SuppressWarnings("unused")
	org.apache.catalina.Wrapper wrapper = Tomcat.addServlet(rootCtx,
		aceQLManagerServletCallName, new ServerSqlManager());
	wrapper.setAsyncSupported(true); 
	rootCtx.addServletMappingDecoded("/*", aceQLManagerServletCallName);

	TomcatStarterUtil.setInitParametersInStore(properties);

	// Unecessary because we must start at / because of ou Rest API
	// String serverSqlManagerUrlPattern = serverSqlManagerServletName;
	// if (!serverSqlManagerUrlPattern.startsWith("/")) {
	// serverSqlManagerUrlPattern = "/" + serverSqlManagerUrlPattern;
	// }

    }

    /**
     * Test the servlet
     * 
     * @param properties
     *            the properties than contain all servlet & configurators info
     * @param sslScheme
     *            the ssl scheme
     * @return the status
     * 
     * @throws MalformedURLException
     * @throws IOException
     */
    public String testServlet(Properties properties, String scheme)
	    throws MalformedURLException, IOException {

	String aceQLManagerServletCallName = TomcatStarterUtil
		.getAceQLManagerSevletName(properties);

	String serverSqlManagerUrlPattern = aceQLManagerServletCallName;
	serverSqlManagerUrlPattern = serverSqlManagerUrlPattern.trim();

	if (!serverSqlManagerUrlPattern.startsWith("/")) {
	    serverSqlManagerUrlPattern = "/" + serverSqlManagerUrlPattern;
	}

	String url = scheme + "://" + host + ":" + port
		+ serverSqlManagerUrlPattern;

	// Call the ServerSqlManagerServlet to test everything is OK.
	String serverSqlManagerstatus = callServerSqlManagerServlet(url);

	if (serverSqlManagerstatus.contains("\"OK\"")) {
	    System.out.println(
		    SqlTag.SQL_PRODUCT_START + " URL for client side: " + url);
	}

	return serverSqlManagerstatus;
    }

    /**
     * Call the Server SQL Manager Servlet to test everything is OK.
     * 
     * @param url
     *            the url of the servlet
     * 
     * @return the return status. "Should be OK.
     * @throws MalformedURLException
     * @throws IOException
     */
    private String callServerSqlManagerServlet(String url)
	    throws MalformedURLException, IOException {
	URL theUrl = new URL(url);
	URLConnection urLconnection = theUrl.openConnection();

	BufferedReader br = new BufferedReader(
		new InputStreamReader(urLconnection.getInputStream()));
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

	File baseDir = new File(userHome + ".kawansoft" + File.separator
		+ "tomcat-embedded-temp");
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
