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

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.session.SessionConfigurator;
import org.kawanfw.sql.servlet.connection.ConnectionStore;
import org.kawanfw.sql.servlet.connection.ConnectionUtil;
import org.kawanfw.sql.servlet.sql.json_return.ExceptionReturner;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonOkReturn;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * Login.
 *
 * @author Nicolas de Pomereu
 *
 */
public class ServerLoginActionSql extends HttpServlet {

    public static boolean DEBUG = FrameworkDebug
	    .isSet(ServerLoginActionSql.class);

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    // A space
    public static final String SPACE = " ";

    /**
     * Constructor
     */
    public ServerLoginActionSql() {

    }

    /**
     *
     * Execute the login request
     *
     * @param request
     *            the http request
     * @param response
     *            the http response
     * @param action
     *            the login action: BEFORE_LOGIN_ACTION or LOGIN_ACTION
     * @throws IOException
     *             if any Servlet Exception occurs
     */
    public void executeAction(HttpServletRequest request,
	    HttpServletResponse response, String action) throws IOException {
	PrintWriter out = response.getWriter();

	try {
	    response.setContentType("text/html");

	    debug("before request.getParameter(HttpParameter.LOGIN);");

	    String username = request.getParameter(HttpParameter.USERNAME);
	    String password = request.getParameter(HttpParameter.PASSWORD);

	    // User must provide a user
	    if ((username == null || username.isEmpty())
		    || (password == null || password.isEmpty())) {
		debug("username.length() < 1!");
		// No login transmitted
		// Redirect to ClientLogin with error message.
		// String logMessage = "username.length() < 1!";
		// HttpStatus.set(response, HttpServletResponse.SC_UNAUTHORIZED,
		// logMessage);
		JsonErrorReturn errorReturn = new JsonErrorReturn(response,
			HttpServletResponse.SC_UNAUTHORIZED,
			JsonErrorReturn.ERROR_ACEQL_ERROR,
			JsonErrorReturn.INVALID_USERNAME_OR_PASSWORD);
		out.println(errorReturn.build());
		return;
	    }

	    username = username.trim();
	    password = password.trim();

	    debug("calling login");

	    String database = request.getParameter(HttpParameter.DATABASE);

	    DatabaseConfigurator databaseConfigurator = ServerSqlManager
		    .getDatabaseConfigurator(database);

	    if (databaseConfigurator == null) {
		JsonErrorReturn errorReturn = new JsonErrorReturn(response,
			HttpServletResponse.SC_BAD_REQUEST,
			JsonErrorReturn.ERROR_ACEQL_ERROR,
			JsonErrorReturn.DATABASE_DOES_NOT_EXIST + database);
		out.println(errorReturn.build());
		return;
	    }

	    String ipAddress = request.getRemoteAddr();
	    boolean isOk = databaseConfigurator.login(username,
		    password.toCharArray(), database, ipAddress);

	    debug("login isOk: " + isOk + " (login: " + username + ")");

	    if (!isOk) {
		debug("login: invalid login or password");

		JsonErrorReturn errorReturn = new JsonErrorReturn(response,
			HttpServletResponse.SC_UNAUTHORIZED,
			JsonErrorReturn.ERROR_ACEQL_ERROR,
			JsonErrorReturn.INVALID_USERNAME_OR_PASSWORD);
		out.println(errorReturn.build());
		return;
	    }

	    debug("Login done!");

	    // Generate the session id
	    SessionConfigurator sessionConfigurator = ServerSqlManager
		    .getSessionManagerConfigurator();
	    String sessionId = sessionConfigurator.generateSessionId(username,
		    database);

	    String connectionId = null;

	    connectionId = getConnectionId(sessionId, request, username,
		    database, databaseConfigurator);

	    Trace.sessionId("sessionId: " + sessionId);

	    Map<String, String> map = new HashMap<>();
	    map.put("session_id", sessionId);
	    map.put("connection_id", connectionId);

	    out.println(JsonOkReturn.build(map));

	} catch (Exception e) {

	    ExceptionReturner.logAndReturnException(request, response, out, e);

	}
    }

    /**
     * Extract a Connection from the pool and return the connection id (hashcode) to client.
     * Connections is stored in memory until client side calls close action
     * @param sessionId
     * @param request
     * @param username
     * @param database
     * @param databaseConfigurator
     * @return
     * @throws SQLException
     */
    public static  String getConnectionId(String sessionId, HttpServletRequest request,
	    String username, String database, DatabaseConfigurator databaseConfigurator)
	    throws SQLException {

	// Exract connection from pool
	Connection connection = databaseConfigurator
	    .getConnection(database);

	// Each Connection is identified by hashcode
	String connectionId  = getConnectionId(connection);

//	// Force connectionId if client version is not 2.0
//	String clientVersion = request.getParameter(HttpParameter.CLIENT_VERSION);
//	if (clientVersion == null || clientVersion.compareTo("v2.0") < 0) {
//	    connectionId = "unique";
//	}

	ConnectionStore connectionStore = new ConnectionStore(username,
	    sessionId, connectionId);

	// Make sure we are in auto-commit mode when user starts
	// session
	ConnectionUtil.connectionInit(connection);
	connectionStore.put(connection);
	return connectionId;
    }

    public static String getConnectionId(Connection connection) {
	return "" + connection.hashCode();
    }

    private void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }
}
