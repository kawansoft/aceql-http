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

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.auth.UserAuthenticator;
import org.kawanfw.sql.api.server.session.SessionConfigurator;
import org.kawanfw.sql.servlet.connection.ConnectionIdUtil;
import org.kawanfw.sql.servlet.connection.ConnectionStore;
import org.kawanfw.sql.servlet.injection.classes.InjectedClassesStore;
import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesUtil;
import org.kawanfw.sql.servlet.sql.json_return.ExceptionReturner;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonOkReturn;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.IpUtil;

/**
 * Login.
 *
 * @author Nicolas de Pomereu
 *
 */
public class ServerLoginActionSql extends HttpServlet {

    public static boolean DEBUG = FrameworkDebug.isSet(ServerLoginActionSql.class);

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    // A space
    public static final String SPACE = " ";

    /**
     *
     * Execute the login request
     *
     * @param request  the http request
     * @param response the http response
     * @param out      the servlet output stream
     * @param action   the login action: BEFORE_LOGIN_ACTION or LOGIN_ACTION
     * @throws IOException if any Servlet Exception occurs
     */
    public void executeAction(HttpServletRequest request, HttpServletResponse response, OutputStream out, String action)
	    throws IOException {

	try {
	    response.setContentType("text/html");
	    debug("before request.getParameter(HttpParameter.LOGIN);");
	    String username = request.getParameter(HttpParameter.USERNAME);
	    String password = request.getParameter(HttpParameter.PASSWORD);

	    // User must provide a user
	    if (! checkCredentialsAreSet(username, password)) {
		JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_UNAUTHORIZED,
			JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.INVALID_USERNAME_OR_PASSWORD);
		ServerSqlManager.writeLine(out, errorReturn.build());
		return;
	    }

	    username = username.trim();
	    password = password.trim();

	    debug("calling login");
	    UserAuthenticator userAuthenticator = InjectedClassesStore.get().getUserAuthenticator();
	    String database = request.getParameter(HttpParameter.DATABASE);

	    DatabaseConfigurator databaseConfigurator = InjectedClassesStore.get().getDatabaseConfigurators().get(database);

	    if (databaseConfigurator == null) {
		JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
			JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.DATABASE_DOES_NOT_EXIST + database);
		ServerSqlManager.writeLine(out, errorReturn.build());
		return;
	    }

	    String ipAddress = IpUtil.getRemoteAddr(request);
	    boolean isOk = false;
	    
	    // If userAuthenticator is null, none has been defined in .properties file, so we allow login
	    if (userAuthenticator != null) {
		isOk = userAuthenticator.login(username, password.toCharArray(), database, ipAddress);		
	    }
	    else {
		isOk = true;
	    }

	    debug("login isOk: " + isOk + " (login: " + username + ")");

	    if (!isOk) {
		debug("login: invalid login or password");

		JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_UNAUTHORIZED,
			JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.INVALID_USERNAME_OR_PASSWORD);
		ServerSqlManager.writeLine(out, errorReturn.build());
		return;
	    }

	    debug("Login done!");

	    // Generate the session id
	    SessionConfigurator sessionConfigurator = InjectedClassesStore.get().getSessionConfigurator();
	    String sessionId = sessionConfigurator.generateSessionId(username, database);

	    String connectionId = null;
	    	    
	    if (ConfPropertiesUtil.isStatelessMode()) {
		// Stateless: we just return the "stateless" Connection Id
		connectionId = ConnectionIdUtil.getStatelessConnectionId();
	    }
	    else {
		// Stateful: We create the Connection and store it: 
		Connection connection = databaseConfigurator.getConnection(database);
		// Each Connection is identified by hashcode of connection 
		connectionId = ConnectionIdUtil.getConnectionId(connection);
		// We store the Connection in Memory
		ConnectionStore connectionStore = new ConnectionStore(username, sessionId, connectionId);
		
		connectionStore.put(connection);
	    }
	    
	    Trace.sessionId("sessionId: " + sessionId);
	    debug("sessionId: "+ sessionId);
	    
	    Map<String, String> map = new HashMap<>();
	    map.put("session_id", sessionId);
	    map.put("connection_id", connectionId);

	    ServerSqlManager.writeLine(out, JsonOkReturn.build(map));

	} catch (Exception e) {
	    ExceptionReturner.logAndReturnException(request, response, out, e);
	}
    }

    /**
     * Check that credentials are not null and not empty
     * @param username
     * @param password
     * @return true if credentials are not null and are not empty
     */
    public boolean checkCredentialsAreSet(String username, String password) {
	return username != null && ! username.isEmpty() && password != null && ! password.isEmpty();
    }

    private void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }
}
