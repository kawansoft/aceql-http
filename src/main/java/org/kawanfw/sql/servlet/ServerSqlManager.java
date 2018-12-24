/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP                                     
 * Copyright (C) 2018, KawanSoft SAS
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
package org.kawanfw.sql.servlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.DefaultDatabaseConfigurator;
import org.kawanfw.sql.api.server.blob.BlobDownloadConfigurator;
import org.kawanfw.sql.api.server.blob.BlobUploadConfigurator;
import org.kawanfw.sql.api.server.blob.DefaultBlobDownloadConfigurator;
import org.kawanfw.sql.api.server.blob.DefaultBlobUploadConfigurator;
import org.kawanfw.sql.api.server.session.DefaultSessionConfigurator;
import org.kawanfw.sql.api.server.session.SessionConfigurator;
import org.kawanfw.sql.servlet.sql.json_return.ExceptionReturner;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonOkReturn;
import org.kawanfw.sql.tomcat.ServletParametersStore;
import org.kawanfw.sql.tomcat.ThreadPoolExecutorStore;
import org.kawanfw.sql.tomcat.TomcatSqlModeStore;
import org.kawanfw.sql.tomcat.TomcatStarterUtil;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.SqlTag;
import org.kawanfw.sql.util.Tag;
import org.kawanfw.sql.version.Version;

/**
 * Http JDBC Server
 * 
 * @author Nicolas de Pomereu
 */

@SuppressWarnings("serial")
public class ServerSqlManager extends HttpServlet {

	private static boolean DEBUG = FrameworkDebug.isSet(ServerSqlManager.class);

	public static String CR_LF = System.getProperty("line.separator");

	public static final String DATABASE_CONFIGURATOR_CLASS_NAME = "databaseConfiguratorClassName";
	public static final String BLOB_DOWNLOAD_CONFIGURATOR_CLASS_NAME = "blobDownloadConfiguratorClassName";
	public static final String BLOB_UPLOAD_CONFIGURATOR_CLASS_NAME = "blobUploadConfiguratorClassName";
	public static final String SESSION_CONFIGURATOR_CLASS_NAME = "sessionConfiguratorClassName";
	public static final String JWT_SESSION_CONFIGURATOR_SECRET = "jwtSessionConfiguratorSecret";

	/** The map of (database, DatabaseConfigurator) */
	private static Map<String, DatabaseConfigurator> databaseConfigurators = new ConcurrentHashMap<>();

	/** The BlobUploadConfigurator instance */
	private static BlobUploadConfigurator blobUploadConfigurator = null;

	/**
	 * @return the blobUploadConfigurator
	 */
	public static BlobUploadConfigurator getBlobUploadConfigurator() {
		return blobUploadConfigurator;
	}

	/**
	 * @return the blobDownloadConfigurator
	 */
	public static BlobDownloadConfigurator getBlobDownloadConfigurator() {
		return blobDownloadConfigurator;
	}

	/** The BlobUploadConfigurator instance */
	private static BlobDownloadConfigurator blobDownloadConfigurator = null;

	/** The SessionConfigurator instance */
	private static SessionConfigurator sessionConfigurator = null;

	/**
	 * Getter to used in all classes to get the DatabaseConfigurator for the
	 * database name
	 * 
	 * @param database
	 *            the database to load the DatabaseConfigurator for the database
	 *            name
	 * @return
	 */
	public static DatabaseConfigurator getDatabaseConfigurator(String database) {
		return databaseConfigurators.get(database);
	}

	/**
	 * Getter to used in all classes to get the SessionConfigurator
	 * 
	 * @return the sessionConfigurator
	 */
	public static SessionConfigurator getSessionManagerConfigurator() {
		return sessionConfigurator;
	}

	/** The Exception thrown at init */
	private Exception exception = null;

	/** The init error message trapped */
	private String initErrrorMesage = null;

	/** The executor to use */
	private ThreadPoolExecutor threadPoolExecutor = null;

	

	/**
	 * Init
	 */

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		// Variable use to store the current name when loading, used to
		// detail
		// the exception in the catch clauses
		String classNameToLoad = null;
		String databaseConfiguratorClassName = null;

		// String servletName = this.getServletName();

		if (!TomcatSqlModeStore.isTomcatEmbedded()) {
			System.out.println(SqlTag.SQL_PRODUCT_START + " " + Version.getServerVersion());
		}

		// Test the only thing we can test in DatabaseConfigurator
		// getBlobsDirectory()

		try {

			// Previously created by Tomcat
			threadPoolExecutor = ThreadPoolExecutorStore.getThreadPoolExecutor();

			if (!TomcatSqlModeStore.isTomcatEmbedded()) {
				createDataSources(config);
			}

			Set<String> databases = ServletParametersStore.getDatabaseNames();

			for (String database : databases) {
				databaseConfiguratorClassName = ServletParametersStore.getInitParameter(database,
						DATABASE_CONFIGURATOR_CLASS_NAME);

				debug("databaseConfiguratorClassName    : " + databaseConfiguratorClassName);

				// Check spelling with first letter capitalized

				if (databaseConfiguratorClassName == null || databaseConfiguratorClassName.isEmpty()) {
					String capitalized = StringUtils.capitalize(DATABASE_CONFIGURATOR_CLASS_NAME);
					databaseConfiguratorClassName = ServletParametersStore.getInitParameter(database, capitalized);
				}

				// Call the specific Configurator class to use

				DatabaseConfigurator databaseConfigurator = null;

				classNameToLoad = databaseConfiguratorClassName;
				if (databaseConfiguratorClassName != null && !databaseConfiguratorClassName.isEmpty()) {
					Class<?> c = Class.forName(databaseConfiguratorClassName);

					// databaseConfigurator = (DatabaseConfigurator)
					// c.newInstance();
					Constructor<?> constructor = c.getConstructor();
					databaseConfigurator = (DatabaseConfigurator) constructor.newInstance();

					databaseConfigurators.put(database, databaseConfigurator);
				} else {
					databaseConfigurator = new DefaultDatabaseConfigurator();
					databaseConfiguratorClassName = databaseConfigurator.getClass().getName();
					classNameToLoad = databaseConfiguratorClassName;
					databaseConfigurators.put(database, databaseConfigurator);
				}

				// Gets the Logger to trap Exception if any
				try {
					@SuppressWarnings("unused")
					Logger logger = databaseConfigurator.getLogger();
				} catch (Exception e) {
					throw new DatabaseConfigurationException(Tag.PRODUCT_USER_CONFIG_FAIL
							+ " Impossible to get the Logger from DatabaseConfigurator instance", e);
				}

				System.out.println(SqlTag.SQL_PRODUCT_START + " " + database + " Database Configurator:");
				System.out.println(SqlTag.SQL_PRODUCT_START + "  -> databaseConfiguratorClassName: ");
				System.out.println(SqlTag.SQL_PRODUCT_START + "     " + databaseConfiguratorClassName);

			}

			// Load Configurators for Blobs/Clobs
			String blobDownloadConfiguratorClassName = ServletParametersStore.getBlobDownloadConfiguratorClassName();
			String blobUploadConfiguratorClassName = ServletParametersStore.getBlobUploadConfiguratorClassName();

			classNameToLoad = blobDownloadConfiguratorClassName;
			if (blobDownloadConfiguratorClassName != null && !blobDownloadConfiguratorClassName.isEmpty()) {
				Class<?> c = Class.forName(blobDownloadConfiguratorClassName);

				// blobDownloadConfigurator = (BlobDownloadConfigurator)
				// c.newInstance();
				Constructor<?> constructor = c.getConstructor();
				blobDownloadConfigurator = (BlobDownloadConfigurator) constructor.newInstance();

			} else {
				blobDownloadConfigurator = new DefaultBlobDownloadConfigurator();
				blobDownloadConfiguratorClassName = blobDownloadConfigurator.getClass().getName();
				classNameToLoad = blobDownloadConfiguratorClassName;
			}

			classNameToLoad = blobUploadConfiguratorClassName;
			if (blobUploadConfiguratorClassName != null && !blobUploadConfiguratorClassName.isEmpty()) {
				Class<?> c = Class.forName(blobUploadConfiguratorClassName);

				// blobUploadConfigurator = (BlobUploadConfigurator)
				// c.newInstance();
				Constructor<?> constructor = c.getConstructor();
				blobUploadConfigurator = (BlobUploadConfigurator) constructor.newInstance();

			} else {
				blobUploadConfigurator = new DefaultBlobUploadConfigurator();
				blobUploadConfiguratorClassName = blobUploadConfigurator.getClass().getName();
				classNameToLoad = blobUploadConfiguratorClassName;
			}

			if (!blobDownloadConfiguratorClassName
					.equals(org.kawanfw.sql.api.server.blob.DefaultBlobDownloadConfigurator.class.getName())) {
				System.out.println(SqlTag.SQL_PRODUCT_START + " blobDownloadConfiguratorClassName: ");
				System.out.println(SqlTag.SQL_PRODUCT_START + " " + blobDownloadConfiguratorClassName);
			}

			if (!blobUploadConfiguratorClassName
					.equals(org.kawanfw.sql.api.server.blob.DefaultBlobUploadConfigurator.class.getName())) {
				System.out.println(SqlTag.SQL_PRODUCT_START + " blobUploadConfiguratorClassName: ");
				System.out.println(SqlTag.SQL_PRODUCT_START + " " + blobUploadConfiguratorClassName);
			}

			// Load Configurators for SessionManager
			String sessionManagerConfiguratorClassName = ServletParametersStore.getSessionConfiguratorClassName();

			classNameToLoad = sessionManagerConfiguratorClassName;
			if (sessionManagerConfiguratorClassName != null && !sessionManagerConfiguratorClassName.isEmpty()) {
				Class<?> c = Class.forName(sessionManagerConfiguratorClassName);

				// sessionConfigurator = (SessionConfigurator) c.newInstance();
				Constructor<?> constructor = c.getConstructor();
				sessionConfigurator = (SessionConfigurator) constructor.newInstance();

			} else {
				sessionConfigurator = new DefaultSessionConfigurator();
				sessionManagerConfiguratorClassName = sessionConfigurator.getClass().getName();
				classNameToLoad = sessionManagerConfiguratorClassName;
			}

			if (!sessionManagerConfiguratorClassName
					.equals(org.kawanfw.sql.api.server.session.DefaultSessionConfigurator.class.getName())) {
				System.out.println(SqlTag.SQL_PRODUCT_START + " sessionManagerConfiguratorClassName: ");
				System.out.println(SqlTag.SQL_PRODUCT_START + "  -> " + sessionManagerConfiguratorClassName);
			}

		} catch (ClassNotFoundException e) {
			initErrrorMesage = Tag.PRODUCT_USER_CONFIG_FAIL
					+ " Impossible to load (ClassNotFoundException) Configurator class: " + classNameToLoad;
			exception = e;
		} catch (InstantiationException e) {
			initErrrorMesage = Tag.PRODUCT_USER_CONFIG_FAIL
					+ " Impossible to load (InstantiationException) Configurator class: " + classNameToLoad;
			exception = e;
		} catch (IllegalAccessException e) {
			initErrrorMesage = Tag.PRODUCT_USER_CONFIG_FAIL
					+ " Impossible to load (IllegalAccessException) Configurator class: " + classNameToLoad;
			exception = e;
		} catch (DatabaseConfigurationException e) {
			initErrrorMesage = e.getMessage();
			exception = e;
		} catch (Exception e) {
			initErrrorMesage = Tag.PRODUCT_PRODUCT_FAIL + " Please contact support at: support@kawansoft.com";
			exception = e;
		}

		if (exception == null) {
			System.out.println(SqlTag.SQL_PRODUCT_START + " Configurators Status: OK.");

			if (!TomcatSqlModeStore.isTomcatEmbedded()) {
				String runningMessage = SqlTag.SQL_PRODUCT_START + " " + Version.PRODUCT.NAME + " Start OK.";
				System.out.println(runningMessage);
			}

		} else {

			exception.printStackTrace();

			if (!TomcatSqlModeStore.isTomcatEmbedded()) {
				String errorMessage1 = SqlTag.SQL_PRODUCT_START + "  -> Configurators Status: KO.";
				String errorMessage2 = initErrrorMesage;
				String errorMessage3 = ExceptionUtils.getStackTrace(exception);

				System.out.println(errorMessage1);
				System.out.println(errorMessage2);
				System.out.println(errorMessage3);

				System.out.println();
			}

		}
	}

	@Override
	public void destroy() {
		super.destroy();
		
		if (threadPoolExecutor != null) {
			try {
				threadPoolExecutor.shutdown();
			} catch (Exception e) {
				e.printStackTrace(); // Should never happen
			}
		}
		
	}

	/**
	 * Entry point. All is Async in AceQL.
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/* Call in async mode */
		final AsyncContext asyncContext = request.startAsync();
		asyncContext.setTimeout(0);
		asyncContext.addListener(new ServerAsyncListener());

		// Just in case
		if (threadPoolExecutor == null) {
			throw new NullPointerException("threadPoolExecutor is null");
		}

		threadPoolExecutor.execute(new Runnable() {
			public void run() {

				HttpServletRequest request = (HttpServletRequest) asyncContext.getRequest();
				HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();

				try {
					handleRequestWrapper(request, response);
				} finally {
					asyncContext.complete();
				}
			}
		});
	}

	/**
	 * POST & GET. Handles all servlet calls. Allows to log Exceptions including
	 * runtime Exceptions
	 * 
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private void handleRequestWrapper(HttpServletRequest request, HttpServletResponse response) {

		OutputStream out = null;

		try {

			handleRequest(request, response, out);

		} catch (Throwable e) {

			try {

				// Always use our own tmp file for logging or exception
				PrivateTmpLogger privateTmpLogger = new PrivateTmpLogger(e);
				privateTmpLogger.log();

				if (out == null) {
					out = response.getOutputStream();
				}

				ExceptionReturner.logAndReturnException(request, response, out, e);
			} catch (IOException ioe) {
				ioe.printStackTrace(System.out);
			}

		}

	}

	/**
	 * Don't catch Exception in this method. All Throwable are catch in caller
	 * handleRequestWrapper
	 * 
	 * @param request
	 * @param response
	 * @param out
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws FileUploadException
	 * @throws SQLException
	 */
	private void handleRequest(HttpServletRequest request, HttpServletResponse response, OutputStream out)
			throws UnsupportedEncodingException, IOException, SQLException, FileUploadException {
		request.setCharacterEncoding("UTF-8");

		// Web Display if no Servlet path
		/*
		 * Enumeration<String> enumeration = request.getParameterNames();
		 * 
		 * if (! enumeration.hasMoreElements() &&
		 * StringUtils.countMatches(request.getRequestURI(), "/") < 2) {
		 * ServerSqlManagerDoGetTester serverSqlManagerDoGetTester = new
		 * ServerSqlManagerDoGetTester();
		 * serverSqlManagerDoGetTester.doGetTest(response, this.getServletName(),
		 * exception); return; }
		 */

		// If Init fail, say it cleanly to client, instead of bad 500 Servlet
		// Error
		if (exception != null) {
			out = response.getOutputStream();
			JsonErrorReturn jsonErrorReturn = new JsonErrorReturn(response,
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR, JsonErrorReturn.ERROR_ACEQL_ERROR,
					initErrrorMesage + " Reason: " + exception.getMessage(), ExceptionUtils.getStackTrace(exception));

			writeLine(out, jsonErrorReturn.build());
			return;
		}

		debug("after RequestInfoStore.init(request);");
		debug(request.getRemoteAddr());

		// Wrap the HttpServletRequest in roder to allow to set new parameters
		HttpServletRequestHolder requestHolder = new HttpServletRequestHolder(request);

		ServerSqlDispatch dispatch = new ServerSqlDispatch();
		debug("before dispatch.executeRequest()");

		// Allows to emulate a request parameter from the Servlet Path:
		// domain/aceql/database/[database]/username/[username]/login
		// domain/aceql/session/[session]/[action_name]/[action_value]

		String database = null;
		String username = null;
		String sessionId = null;
		String connectionId = null;
		String action = null;
		String actionValue = null;

		// Minimalist URL analyzer
		debug("servlet Path : " + request.getServletPath());
		debug("getRequestURI: " + request.getRequestURI());

		String servletPath = request.getServletPath();
		String requestUri = request.getRequestURI();

		String servletName = ServletParametersStore.getServletName();

		if (servletName != null) {
			servletName = servletName.trim();
		}

		if (!requestUri.startsWith("/" + servletName) && !servletPath.startsWith("/" + servletName)) {
			out = response.getOutputStream();

			// System.out.println("servletPath:" + servletPath);
			// System.out.println("urlContent :" + urlContent);

			if (requestUri.equals("/")) {
				JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
						JsonErrorReturn.ERROR_ACEQL_ERROR,
						JsonErrorReturn.ACEQL_SERVLET_NOT_FOUND_IN_PATH + servletName);
				// out.println(errorReturn.build());
				writeLine(out, errorReturn.build());
				return;
			} else {
				String servlet = requestUri.substring(1);
				JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
						JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.UNKNOWN_SERVLET + servlet);
				// out.println(errorReturn.build());
				writeLine(out, errorReturn.build());
				return;
			}

		}

		// Display version if we just call the servlet
		if (requestUri.endsWith("/" + servletName) || requestUri.endsWith("/" + servletName + "/")) {
			out = response.getOutputStream();
			String version = org.kawanfw.sql.version.Version.getVersion();
			writeLine(out, JsonOkReturn.build("version", version));
			return;
		}

		ServletPathAnalyzer servletPathAnalyzer = new ServletPathAnalyzer();

		try {
			if (isLoginAction(requestUri)) {
				action = "login";

				if (!requestUri.contains("/" + servletName + "/database/")) {
					throw new IllegalArgumentException("Request does not contain /database/ subpath in path");
				}

				if (!requestUri.contains("/username/")) {
					throw new IllegalArgumentException("Request does not contain /username/ subpath in path");
				}

				database = StringUtils.substringBetween(requestUri, "/database/", "/username");

				// Accept /connect pattern
				if (requestUri.endsWith("/connect")) {
					requestUri = StringUtils.substringBeforeLast(requestUri, "/connect") + "/login";
				} else if (requestUri.contains("/connect?")) {
					requestUri = StringUtils.substringBeforeLast(requestUri, "/connect?") + "/login?";
				}

				username = StringUtils.substringBetween(requestUri, "/username/", "/login");

			} else if (servletPathAnalyzer.isVersionAction(requestUri)) {
				action = "get_version";
				servletPathAnalyzer.buildElements(servletName, requestUri);
				sessionId = servletPathAnalyzer.getSession();
			} else if (servletPathAnalyzer.isConnectionModifierOrReader(requestUri)) {
				action = servletPathAnalyzer.getConnectionModifierOrReader();
				actionValue = servletPathAnalyzer.getActionValue();
				servletPathAnalyzer.buildElements(servletName, requestUri);
				sessionId = servletPathAnalyzer.getSession();
				connectionId = servletPathAnalyzer.getConnection();
			} else if (servletPathAnalyzer.isBlobAction(requestUri)) {
				action = servletPathAnalyzer.getBlobAction();
				actionValue = servletPathAnalyzer.getActionValue();
				servletPathAnalyzer.buildElements(servletName, requestUri);
				sessionId = servletPathAnalyzer.getSession();
				connectionId = servletPathAnalyzer.getConnection();
			} else if (servletPathAnalyzer.isExecuteUpdateOrQueryStatement(requestUri)) {
				action = servletPathAnalyzer.getSqlStatement();
				servletPathAnalyzer.buildElements(servletName, requestUri);
				sessionId = servletPathAnalyzer.getSession();
				connectionId = servletPathAnalyzer.getConnection();
			} else {
				throw new IllegalArgumentException(
						"Unknown action: " + StringUtils.substringAfterLast(requestUri, "/"));
			}

		} catch (Exception e) {
			// Happens if bad request ==> 400
			String errorMessage = e.getMessage();
			out = response.getOutputStream();
			JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
					JsonErrorReturn.ERROR_ACEQL_ERROR, errorMessage);
			// out.println(errorReturn.build());
			writeLine(out, errorReturn.build());
			return;
		}

		// In other cases than connect, username & database are null
		if (username == null && database == null) {

			boolean isVerified = sessionConfigurator.verifySessionId(sessionId);

			if (!isVerified) {
				out = response.getOutputStream();
				JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_UNAUTHORIZED,
						JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.INVALID_SESSION_ID);
				// out.println(errorReturn.build());
				writeLine(out, errorReturn.build());
				return;
			}

			username = sessionConfigurator.getUsername(sessionId);
			database = sessionConfigurator.getDatabase(sessionId);

			if (username == null || database == null) {
				out = response.getOutputStream();
				JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_UNAUTHORIZED,
						JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.INVALID_SESSION_ID);
				// out.println(errorReturn.build());
				writeLine(out, errorReturn.build());
				return;
			}
		}

		debug("");
		debug("action      : " + action);
		debug("actionValue : " + actionValue);
		debug("username    : " + username);
		debug("sessionId   : " + sessionId);
		debug("connectionId: " + connectionId);
		debug("database    : " + database);

		requestHolder.setParameter(HttpParameter.ACTION, action);
		requestHolder.setParameter(HttpParameter.ACTION_VALUE, actionValue);

		requestHolder.setParameter(HttpParameter.SESSION_ID, sessionId);
		requestHolder.setParameter(HttpParameter.CONNECTION_ID, connectionId);

		requestHolder.setParameter(HttpParameter.USERNAME, username);
		requestHolder.setParameter(HttpParameter.DATABASE, database);

		dispatch.executeRequestInTryCatch(requestHolder, response, out);
	}

	/**
	 * Throws an Exception for tests purposes if
	 * user.home/.kawansoft/throw_exception.txt exists
	 */
	public static void testThrowException() {
		File file = new File(
				SystemUtils.USER_HOME + File.separator + ".kawansoft" + File.separator + "throw_exception.txt");
		if (file.exists()) {
			throw new IllegalArgumentException(
					"Exception thrown because user.home/.kawansoft/throw_exception.txt exists!");
		}
	}

	private boolean isLoginAction(String requestUri) {
		return requestUri.endsWith("/login") || requestUri.endsWith("/connect");
	}

	/**
	 * Creates the data sources - this is called only if AceQL is used in Servlet
	 * Container
	 * 
	 * @param config
	 * @throws IOException
	 */
	private void createDataSources(ServletConfig config) throws IOException {
		String propertiesFileStr = config.getInitParameter("properties");

		if (propertiesFileStr == null || propertiesFileStr.isEmpty()) {
			throw new DatabaseConfigurationException(Tag.PRODUCT_USER_CONFIG_FAIL
					+ " AceQL servlet param-name \"properties\" not set. Impossible to load the AceQL Server properties file.");
		}

		File propertiesFile = new File(propertiesFileStr);

		if (!propertiesFile.exists()) {
			throw new DatabaseConfigurationException(
					Tag.PRODUCT_USER_CONFIG_FAIL + " properties file not found: " + propertiesFile);
		}

		System.out.println(SqlTag.SQL_PRODUCT_START + " " + "Using properties file: ");
		System.out.println(SqlTag.SQL_PRODUCT_START + "  -> " + propertiesFile);

		Properties properties = TomcatStarterUtil.getProperties(propertiesFile);

		TomcatStarterUtil.setInitParametersInStore(properties);

		// Create the default DataSource if necessary
		TomcatStarterUtil.createAndStoreDataSources(properties);

	}

	/**
	 * Write a line of string on the servlet output stream. Will add the necessary
	 * CR_LF
	 * 
	 * @param out
	 *            the servlet output stream
	 * @param s
	 *            the string to write
	 * @throws IOException
	 */
	public static void write(OutputStream out, String s) throws IOException {
		out.write((s + CR_LF).getBytes("UTF-8"));
	}

	/**
	 * Write a CR/LF on the servlet output stream. Designed to add a end line to
	 * ResultSetWriter action
	 * 
	 * @param out
	 *            the servlet output stream
	 * @throws IOException
	 */
	public static void writeLine(OutputStream out) throws IOException {
		out.write((CR_LF).getBytes("UTF-8"));
	}

	/**
	 * Write a line of string on the servlet output stream. Will add the necessary
	 * CR_LF
	 * 
	 * @param out
	 *            the servlet output stream
	 * @param s
	 *            the string to write
	 * @throws IOException
	 */
	public static void writeLine(OutputStream out, String s) throws IOException {
		out.write((s + CR_LF).getBytes("UTF-8"));
	}

	/**
	 * Method called by children Servlet for debug purpose Println is done only if
	 * class name name is in kawansoft-debug.ini
	 */
	public static void debug(String s) {
		if (DEBUG) {
			System.out.println(new Date() + " " + s);
		}
	}

}
