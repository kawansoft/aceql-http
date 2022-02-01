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
package org.kawanfw.sql.servlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.kawanfw.sql.api.server.auth.headers.RequestHeadersAuthenticator;
import org.kawanfw.sql.api.server.session.SessionConfigurator;
import org.kawanfw.sql.servlet.injection.classes.InjectedClassesManagerNew;
import org.kawanfw.sql.servlet.injection.classes.InjectedClassesStore;
import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesStore;
import org.kawanfw.sql.servlet.sql.json_return.ExceptionReturner;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonOkReturn;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.version.DefaultVersion;

/**
 * Http JDBC Server
 *
 * @author Nicolas de Pomereu
 */

@SuppressWarnings("serial")
public class ServerSqlManager extends HttpServlet {

    private static boolean DEBUG = FrameworkDebug.isSet(ServerSqlManager.class);

    public static String CR_LF = System.getProperty("line.separator");

    public static final String STATELESS_MODE = "statelessMode";
    public static final String DATABASE_CONFIGURATOR_CLASS_NAME = "databaseConfiguratorClassName";
    public static final String USER_AUTHENTICATOR_CLASS_NAME = "userAuthenticatorClassName";
    public static final String REQUEST_HEADERS_AUTHENTICATOR_CLASS_NAME = "requestHeadersAuthenticatorClassName";
    public static final String SQL_FIREWALL_MANAGER_CLASS_NAMES = "sqlFirewallManagerClassNames";
    public static final String BLOB_DOWNLOAD_CONFIGURATOR_CLASS_NAME = "blobDownloadConfiguratorClassName";
    public static final String BLOB_UPLOAD_CONFIGURATOR_CLASS_NAME = "blobUploadConfiguratorClassName";
    public static final String SESSION_CONFIGURATOR_CLASS_NAME = "sessionConfiguratorClassName";
    public static final String JWT_SESSION_CONFIGURATOR_SECRET = "jwtSessionConfiguratorSecret";

    public static final String UPDATE_LISTENER_MANAGER_CLASS_NAMES = "updateListenerClassNames";
    
    /** The Exception thrown at init */
    private Exception exception = null;

    /** The init error message trapped */
    private String initErrrorMesage = null;


    /**
     * Init
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
	super.init(config);
	InjectedClassesManagerNew injectedClassesManager = new InjectedClassesManagerNew(config);
	injectedClassesManager.createClasses();

	exception = injectedClassesManager.getException();
	initErrrorMesage = injectedClassesManager.getInitErrrorMesage();
    }

    @Override
    public void destroy() {
	super.destroy();
	ThreadPoolExecutor threadPoolExecutor = InjectedClassesStore.get().getThreadPoolExecutor();
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

	ThreadPoolExecutor threadPoolExecutor = InjectedClassesStore.get().getThreadPoolExecutor();
	
	// Just in case
	Objects.requireNonNull(threadPoolExecutor, "threadPoolExecutor cannot be null!");

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
     */
    private void handleRequestWrapper(HttpServletRequest request, HttpServletResponse response) {
	OutputStream out = null;
	try {
	    out = response.getOutputStream();
	    handleRequest(request, response, out);
	} catch (Throwable e) {
	    try {
		// Always use our own tmp file for logging or exception
		PrivateTmpLogger privateTmpLogger = new PrivateTmpLogger(e);
		privateTmpLogger.log();

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

	if (isExceptionSet(response, out)) {
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

	String servletName = ConfPropertiesStore.get().getServletName();
	if (!checkRequestStartsWithAceqlServlet(response, out, servletPath, requestUri, servletName)) {
	    return;
	}

	if (getVersion(out, requestUri, servletName)) {
	    return;
	}

	try {
	    ServletPathAnalyzer servletPathAnalyzer = new ServletPathAnalyzer(requestUri, servletName);
	    action = servletPathAnalyzer.getAction();
	    actionValue = servletPathAnalyzer.getActionValue();
	    database = servletPathAnalyzer.getDatabase();
	    username = servletPathAnalyzer.getUsername();
	    sessionId = servletPathAnalyzer.getSession();
	    connectionId = servletPathAnalyzer.getConnection();

	} catch (Exception e) {
	    // Happens if bad request ==> 400
	    String errorMessage = e.getMessage();
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, errorMessage);
	    // out.println(errorReturn.build());
	    writeLine(out, errorReturn.build());
	    return;
	}

	// Check that the request headers are accepted
	if (!validateHeaders(request, response, out)) {
	    return;
	}

	// In other cases than connect, username & database are null
	if (username == null && database == null) {

	    if (!checkSessionIsVerified(response, out, sessionId)) {
		return;
	    }

	    SessionConfigurator sessionConfigurator = InjectedClassesStore.get().getSessionConfigurator();
		
	    username = sessionConfigurator.getUsername(sessionId);
	    database = sessionConfigurator.getDatabase(sessionId);

	    if (!checkUsernameAndDatabase(response, out, database, username)) {
		return;
	    }
	}

	debugValues(database, username, sessionId, connectionId, action, actionValue);

	requestHolder.setParameter(HttpParameter.ACTION, action);
	requestHolder.setParameter(HttpParameter.ACTION_VALUE, actionValue);
	requestHolder.setParameter(HttpParameter.SESSION_ID, sessionId);
	requestHolder.setParameter(HttpParameter.CONNECTION_ID, connectionId);
	requestHolder.setParameter(HttpParameter.USERNAME, username);
	requestHolder.setParameter(HttpParameter.DATABASE, database);

	// Tests exceptions
	ServerSqlManager.testThrowException();
	dispatch.executeRequestInTryCatch(requestHolder, response, out);
    }

    /**
     * Checks that headers are authenticated/validated.
     * 
     * @param request
     * @param response
     * @param out
     * @return
     * @throws IOException
     */
    private boolean validateHeaders(HttpServletRequest request, HttpServletResponse response, OutputStream out)
	    throws IOException {

	// Request Headers;
	Map<String, String> headers = new HashMap<>();
	Enumeration<?> e = request.getHeaderNames();
	while (e.hasMoreElements()) {
	    String key = (String) e.nextElement();
	    String value = request.getHeader(key);
	    headers.put(key, value);
	}

	RequestHeadersAuthenticator requestHeadersAuthenticator = InjectedClassesStore.get().getRequestHeadersAuthenticator();
	boolean checked = requestHeadersAuthenticator.validate(headers);

	if (!checked) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_UNAUTHORIZED,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.INVALID_SESSION_ID);
	    // out.println(errorReturn.build());
	    writeLine(out, errorReturn.build());
	    return false;
	}

	return checked;
    }

    /**
     * @param database
     * @param username
     * @param sessionId
     * @param connectionId
     * @param action
     * @param actionValue
     */
    private void debugValues(String database, String username, String sessionId, String connectionId, String action,
	    String actionValue) {
	debug("");
	debug("action      : " + action);
	debug("actionValue : " + actionValue);
	debug("username    : " + username);
	debug("sessionId   : " + sessionId);
	debug("connectionId: " + connectionId);
	debug("database    : " + database);
    }

    /**
     * @param response
     * @param out
     * @param database
     * @param username
     * @throws IOException
     */
    private boolean checkUsernameAndDatabase(HttpServletResponse response, OutputStream out, String database,
	    String username) throws IOException {
	if (username == null || database == null) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_UNAUTHORIZED,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.INVALID_SESSION_ID);
	    // out.println(errorReturn.build());
	    writeLine(out, errorReturn.build());
	    return false;
	}
	return true;
    }

    /**
     * @param response
     * @param out
     * @param sessionId
     * @throws IOException
     */
    private boolean checkSessionIsVerified(HttpServletResponse response, OutputStream out, String sessionId)
	    throws IOException {
	SessionConfigurator sessionConfigurator = InjectedClassesStore.get().getSessionConfigurator();
	boolean isVerified = sessionConfigurator.verifySessionId(sessionId);

	if (!isVerified) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_UNAUTHORIZED,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.INVALID_SESSION_ID);
	    // out.println(errorReturn.build());
	    writeLine(out, errorReturn.build());
	}
	return isVerified;
    }

    /**
     * @param response
     * @param out
     * @throws IOException
     */
    private boolean isExceptionSet(HttpServletResponse response, OutputStream out) throws IOException {
	// If Init fail, say it cleanly to client, instead of bad 500 Servlet
	if (exception != null) {
	    JsonErrorReturn jsonErrorReturn = new JsonErrorReturn(response,
		    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, JsonErrorReturn.ERROR_ACEQL_ERROR,
		    initErrrorMesage + " Reason: " + exception.getMessage(), ExceptionUtils.getStackTrace(exception));

	    writeLine(out, jsonErrorReturn.build());
	    return true;
	}
	return false;
    }

    /**
     * @param out
     * @param requestUri
     * @param servletName
     * @throws IOException
     */
    private boolean getVersion(OutputStream out, String requestUri, String servletName) throws IOException {
	// Display version if we just call the servlet
	if (requestUri.endsWith("/" + servletName) || requestUri.endsWith("/" + servletName + "/")) {
	    String version = new DefaultVersion().getServerVersion();
	    writeLine(out, JsonOkReturn.build("version", version));
	    return true;
	}
	return false;
    }

    /**
     * @param response
     * @param out
     * @param servletPath
     * @param requestUri
     * @param servletName
     * @throws IOException
     */
    private boolean checkRequestStartsWithAceqlServlet(HttpServletResponse response, OutputStream out,
	    String servletPath, String requestUri, String servletName) throws IOException {
	if (!requestUri.startsWith("/" + servletName) && !servletPath.startsWith("/" + servletName)) {

	    // System.out.println("servletPath:" + servletPath);
	    // System.out.println("urlContent :" + urlContent);

	    if (requestUri.equals("/")) {
		JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
			JsonErrorReturn.ERROR_ACEQL_ERROR,
			JsonErrorReturn.ACEQL_SERVLET_NOT_FOUND_IN_PATH + servletName);
		// out.println(errorReturn.build());
		writeLine(out, errorReturn.build());
		return false;
	    } else {
		String servlet = requestUri.substring(1);
		JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
			JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.UNKNOWN_SERVLET + servlet);
		// out.println(errorReturn.build());
		writeLine(out, errorReturn.build());
		return false;
	    }
	} else {
	    return true;
	}
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
