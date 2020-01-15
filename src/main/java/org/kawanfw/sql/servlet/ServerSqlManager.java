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
package org.kawanfw.sql.servlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

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
import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.blob.BlobDownloadConfigurator;
import org.kawanfw.sql.api.server.blob.BlobUploadConfigurator;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.api.server.session.SessionConfigurator;
import org.kawanfw.sql.servlet.sql.json_return.ExceptionReturner;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonOkReturn;
import org.kawanfw.sql.tomcat.ServletParametersStore;
import org.kawanfw.sql.util.FrameworkDebug;

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
    public static final String SQL_FIREWALL_MANAGER_CLASS_NAMES = "sqlFirewallManagerClassNames";
    public static final String BLOB_DOWNLOAD_CONFIGURATOR_CLASS_NAME = "blobDownloadConfiguratorClassName";
    public static final String BLOB_UPLOAD_CONFIGURATOR_CLASS_NAME = "blobUploadConfiguratorClassName";
    public static final String SESSION_CONFIGURATOR_CLASS_NAME = "sessionConfiguratorClassName";
    public static final String JWT_SESSION_CONFIGURATOR_SECRET = "jwtSessionConfiguratorSecret";

    /** The map of (database, DatabaseConfigurator) */
    private static Map<String, DatabaseConfigurator> databaseConfigurators = new ConcurrentHashMap<>();

    /** The map of (database, List<SqlFirewallManager>) */
    private static Map<String, List<SqlFirewallManager>> sqlFirewallMap = new ConcurrentHashMap<>();

    /** The BlobUploadConfigurator instance */
    private static BlobUploadConfigurator blobUploadConfigurator = null;

    /** The BlobUploadConfigurator instance */
    private static BlobDownloadConfigurator blobDownloadConfigurator = null;

    /** The SessionConfigurator instance */
    private static SessionConfigurator sessionConfigurator = null;

    /** The Exception thrown at init */
    private Exception exception = null;

    /** The init error message trapped */
    private String initErrrorMesage = null;

    /** The executor to use */
    private ThreadPoolExecutor threadPoolExecutor = null;

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

    /**
     * Getter to used in all classes to get the DatabaseConfigurator for the
     * database name
     *
     * @param database the database to load the DatabaseConfigurator for the
     *                 database name
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

    /**
     * Returns the list of SqlFirewallManager
     * @return the list of SqlFirewallManager
     */
    public static Map<String, List<SqlFirewallManager>> getSqlFirewallMap() {
        return sqlFirewallMap;
    }

    /**
     * Init
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
	super.init(config);
	ServerSqlManagerInit serverSqlManagerInit = new ServerSqlManagerInit(config);
	databaseConfigurators = serverSqlManagerInit.getDatabaseConfigurators();
	sqlFirewallMap = serverSqlManagerInit.getSqlFirewallMap();
	blobUploadConfigurator = serverSqlManagerInit.getBlobUploadConfigurator();
	blobDownloadConfigurator = serverSqlManagerInit.getBlobDownloadConfigurator();
	sessionConfigurator = serverSqlManagerInit.getSessionConfigurator();
	exception = serverSqlManagerInit.getException();
	initErrrorMesage = serverSqlManagerInit.getInitErrrorMesage();
	threadPoolExecutor = serverSqlManagerInit.getThreadPoolExecutor();
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
	    @Override
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
	    } else if (servletPathAnalyzer.isMetadataQuery(requestUri)) {
		ServletMetadataQuery servletMetadataQuery = new ServletMetadataQuery(requestUri);
		action = servletMetadataQuery.getAction();
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
     * Write a line of string on the servlet output stream. Will add the necessary
     * CR_LF
     *
     * @param out the servlet output stream
     * @param s   the string to write
     * @throws IOException
     */
    public static void write(OutputStream out, String s) throws IOException {
	out.write((s + CR_LF).getBytes("UTF-8"));
    }

    /**
     * Write a CR/LF on the servlet output stream. Designed to add a end line to
     * ResultSetWriter action
     *
     * @param out the servlet output stream
     * @throws IOException
     */
    public static void writeLine(OutputStream out) throws IOException {
	out.write((CR_LF).getBytes("UTF-8"));
    }

    /**
     * Write a line of string on the servlet output stream. Will add the necessary
     * CR_LF
     *
     * @param out the servlet output stream
     * @param s   the string to write
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
