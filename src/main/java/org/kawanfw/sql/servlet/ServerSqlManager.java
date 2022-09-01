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
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.kawanfw.sql.api.server.auth.headers.RequestHeadersAuthenticator;
import org.kawanfw.sql.api.server.session.SessionConfigurator;
import org.kawanfw.sql.servlet.injection.classes.InjectedClassesManagerNew;
import org.kawanfw.sql.servlet.injection.classes.InjectedClassesStore;
import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileStore;
import org.kawanfw.sql.servlet.sql.json_return.ExceptionReturner;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonOkReturn;
import org.kawanfw.sql.tomcat.TomcatSqlModeStore;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.IpUtil;
import org.kawanfw.sql.util.SqlTag;
import org.kawanfw.sql.util.TimestampUtil;
import org.kawanfw.sql.version.VersionWrapper;

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
    public static final String SQL_FIREWALL_TRIGGER_CLASS_NAMES = "sqlFirewallTriggerClassNames";

    public static final String BLOB_DOWNLOAD_CONFIGURATOR_CLASS_NAME = "blobDownloadConfiguratorClassName";
    public static final String BLOB_UPLOAD_CONFIGURATOR_CLASS_NAME = "blobUploadConfiguratorClassName";

    public static final String SESSION_CONFIGURATOR_CLASS_NAME = "sessionConfiguratorClassName";
    public static final String JWT_SESSION_CONFIGURATOR_SECRET = "jwtSessionConfiguratorSecret";

    public static final String UPDATE_LISTENER_MANAGER_CLASS_NAMES = "updateListenerClassNames";

    private static boolean INIT_DONE = false;

    private String propertiesFileStr;
    private static String licenseFileStr = null;

    /**
     * Returns the name of the license file, null if not exists
     * @return the name of the license file, null if not exists
     */
    public static String getLicenseFileStr() {
        return licenseFileStr;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
	super.init(config);
	INIT_DONE = false;
	propertiesFileStr = config.getInitParameter("properties");
	licenseFileStr = config.getInitParameter("licenseFile");

	if (!TomcatSqlModeStore.isTomcatEmbedded()) {
	    System.out.println(SqlTag.SQL_PRODUCT_INIT + " " + TimestampUtil.getHumanTimestampNoMillisNow()
		    + " Call the AceQL Servlet from a browser to display full start in Tomcat logs...");
	    System.out.println();
	}

	// To be done if we are not in Tomcat
	if (propertiesFileStr == null) {
	    propertiesFileStr = PropertiesFileStore.get().toString();
	}

	debug("propertiesFileStr: " + propertiesFileStr);
	debug("licenseFileStr   : " + licenseFileStr);

    }

    @Override
    public void destroy() {
	super.destroy();
	INIT_DONE = false;

	if (InjectedClassesStore.get() != null && InjectedClassesStore.get().getThreadPoolExecutor() != null) {
	    ThreadPoolExecutor threadPoolExecutor = InjectedClassesStore.get().getThreadPoolExecutor();
	    if (threadPoolExecutor != null) {
		try {
		    threadPoolExecutor.shutdown();
		} catch (Exception e) {
		    e.printStackTrace(); // Should never happen
		}
	    }
	}

    }

    /**
     * Entry point. All is Async in AceQL.
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {

	createClassesSynchronized(propertiesFileStr, licenseFileStr);

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
     * Create all classes.
     * 
     * @param propertiesFileStr
     * @param licenseFileStr
     * @throws ServletException
     * @throws IOException
     */
    public static synchronized void createClassesSynchronized(String propertiesFileStr, String licenseFileStr)
	    throws ServletException, IOException {
	if (!INIT_DONE) {
	    INIT_DONE = true;
	    InjectedClassesManagerNew injectedClassesManager = new InjectedClassesManagerNew();
	    injectedClassesManager.createClasses(propertiesFileStr, licenseFileStr);
	}
    }

    /**
     * POST & GET. Handles all servlet calls. Allows to log Exceptions including
     * runtime Exceptions
     *
     * @param request
     * @param response
     * @throws ServletException
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

	debug("after RequestInfoStore.init(request);");
	debug(IpUtil.getRemoteAddr(request));

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

	String servletCallName = ConfPropertiesStore.get().getServletCallName();
	if (!checkRequestStartsWithAceqlServlet(response, out, servletPath, requestUri, servletCallName)) {
	    return;
	}

	if (getVersion(out, requestUri, servletCallName)) {
	    return;
	}

	try {
	    ServletPathAnalyzer servletPathAnalyzer = new ServletPathAnalyzer(requestUri, servletCallName);
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

	RequestHeadersAuthenticator requestHeadersAuthenticator = InjectedClassesStore.get()
		.getRequestHeadersAuthenticator();
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

//    /**
//     * @param response
//     * @param out
//     * @throws IOException
//     */
//    private boolean isExceptionSet(HttpServletResponse response, OutputStream out) throws IOException {
//	// If Init fail, say it cleanly to client, instead of bad 500 Servlet
//	if (exception != null) {
//	    JsonErrorReturn jsonErrorReturn = new JsonErrorReturn(response,
//		    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, JsonErrorReturn.ERROR_ACEQL_ERROR,
//		    initErrrorMesage + " Reason: " + exception.getMessage(), ExceptionUtils.getStackTrace(exception));
//
//	    writeLine(out, jsonErrorReturn.build());
//	    return true;
//	}
//	return false;
//    }

    /**
     * @param out
     * @param requestUri
     * @param servletCallName
     * @throws IOException
     */
    private boolean getVersion(OutputStream out, String requestUri, String servletCallName) throws IOException {
	// Display version if we just call the servlet
	if (requestUri.endsWith("/" + servletCallName) || requestUri.endsWith("/" + servletCallName + "/")) {
	    String version = VersionWrapper.getServerVersion();
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
     * @param servletCallName
     * @throws IOException
     */
    private boolean checkRequestStartsWithAceqlServlet(HttpServletResponse response, OutputStream out,
	    String servletPath, String requestUri, String servletCallName) throws IOException {
	if (!requestUri.startsWith("/" + servletCallName) && !servletPath.startsWith("/" + servletCallName)) {

	    // System.out.println("servletPath:" + servletPath);
	    // System.out.println("urlContent :" + urlContent);

	    if (requestUri.equals("/")) {
		JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
			JsonErrorReturn.ERROR_ACEQL_ERROR,
			JsonErrorReturn.ACEQL_SERVLET_NOT_FOUND_IN_PATH + servletCallName);
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
	    System.out.println(new Date() + " " + ServerSqlManager.class.getSimpleName() + " " + s);
	}
    }

}
