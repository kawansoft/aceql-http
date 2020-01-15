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
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.servlet.connection.ConnectionStore;
import org.kawanfw.sql.servlet.connection.ConnectionStoreCleaner;
import org.kawanfw.sql.servlet.connection.SavepointUtil;
import org.kawanfw.sql.servlet.connection.TransactionUtil;
import org.kawanfw.sql.servlet.sql.LoggerUtil;
import org.kawanfw.sql.servlet.sql.ServerStatement;
import org.kawanfw.sql.servlet.sql.callable.ServerCallableStatement;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonOkReturn;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * @author Nicolas de Pomereu
 *
 *         The method executeRequest() is to to be called from the SqlHttpServer
 *         Servlet and Class. <br>
 *         It will execute a client side request with a RemoteConnection
 *         connection.
 *
 */
public class ServerSqlDispatch {

    private static boolean DEBUG = FrameworkDebug.isSet(ServerSqlDispatch.class);

    /**
     * Constructor
     */
    public ServerSqlDispatch() {
	// Does nothing
    }

    /**
     * Execute the client sent sql request that is already wrapped in the calling
     * try/catch that handles Throwable
     *
     * @param request  the http request
     * @param response the http response
     * @param out      the output stream to write result to client
     * @throws IOException         if any IOException occurs
     * @throws SQLException
     * @throws FileUploadException
     */
    public void executeRequestInTryCatch(HttpServletRequest request, HttpServletResponse response, OutputStream out)
	    throws IOException, SQLException, FileUploadException {

	// Immediate catch if we are asking a file upload, because
	// parameters are in unknown sequence.
	// We know it's a upload action if it's mime Multipart
	if (ServletFileUpload.isMultipartContent(request)) {
	    BlobUploader blobUploader = new BlobUploader(request, response);
	    blobUploader.blobUpload();
	    return;
	}

	debug("executeRequest Start");

	// Prepare the response
	response.setContentType("text/html; charset=UTF-8");

	// Get the send string
	debug("ACTION retrieval");

	String action = request.getParameter(HttpParameter.ACTION);
	String username = request.getParameter(HttpParameter.USERNAME);
	String database = request.getParameter(HttpParameter.DATABASE);
	String sessionId = request.getParameter(HttpParameter.SESSION_ID);
	String connectionId = request.getParameter(HttpParameter.CONNECTION_ID);


	if (action == null || action.isEmpty()) {
	    out = response.getOutputStream();
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.NO_ACTION_FOUND_IN_REQUEST);
	    ServerSqlManager.writeLine(out, errorReturn.build());
	    return;
	}

	debug("ACTION: " + action);
	debug("test action.equals(HttpParameter.LOGIN)");
	if (action.equals(HttpParameter.LOGIN) || action.equals(HttpParameter.CONNECT)) {
	    ServerLoginActionSql serverLoginActionSql = new ServerLoginActionSql();
	    serverLoginActionSql.executeAction(request, response, action);
	    return;
	}

	debug("ACTION : " + action);

	DatabaseConfigurator databaseConfigurator = ServerSqlManager.getDatabaseConfigurator(database);

	if (databaseConfigurator == null) {
	    out = response.getOutputStream();
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.DATABASE_DOES_NOT_EXIST + database);
	    ServerSqlManager.writeLine(out, errorReturn.build());
	    return;
	}

	//
	if (action.equals(HttpParameter.GET_CONNECTION)) {
	    out = response.getOutputStream();
	    connectionId = ServerLoginActionSql.getConnectionId(sessionId, request, username, database,
		    databaseConfigurator);
	    ServerSqlManager.writeLine(out, JsonOkReturn.build("connection_id", connectionId));
	    return;
	}

	// Tests exceptions
	ServerSqlManager.testThrowException();

	// Redirect if it's a File download request (Blobs/Clobs)
	if (action.equals(HttpParameter.BLOB_DOWNLOAD)) {
	    BlobDownloader blobDownloader = new BlobDownloader(request, response, username, databaseConfigurator);
	    blobDownloader.blobDownload();
	    return;
	}

	// No need to get a SQL connection for getting Blob size
	if (action.equals(HttpParameter.GET_BLOB_LENGTH)) {
	    BlobLengthGetter blobLengthGetter = new BlobLengthGetter(request, response, username, databaseConfigurator);
	    blobLengthGetter.getLength();
	    return;
	}

	debug("Before if (action.equals(HttpParameter.LOGOUT))");

	if (action.equals(HttpParameter.LOGOUT) || action.equals(HttpParameter.DISCONNECT)) {
	    ServerLogout.logout(request, response, databaseConfigurator);
	    return;
	}

	out = response.getOutputStream();
	if (action.equals(HttpParameter.GET_VERSION)) {
	    String version = new org.kawanfw.sql.version.Version.PRODUCT().server();
	    ServerSqlManager.writeLine(out, JsonOkReturn.build("result", version));
	    return;
	}

	// Start clean Connections thread
	connectionStoreClean();

	Connection connection = null;

	try {
	    ConnectionStore connectionStore = new ConnectionStore(username, sessionId, connectionId);

	    // Hack to allow version 1.0 to continue to get connection
	    if (connectionId == null || connectionId.isEmpty()) {
		connection = connectionStore.getFirst();
	    } else {
		connection = connectionStore.get();
	    }

	    if (connection == null || connection.isClosed()) {
		JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_NOT_FOUND,
			JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.INVALID_CONNECTION);
		ServerSqlManager.writeLine(out, errorReturn.build());
		return;
	    }

	} catch (SQLException e) {
	    JsonErrorReturn jsonErrorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.UNABLE_TO_GET_A_CONNECTION,
		    ExceptionUtils.getStackTrace(e));
	    ServerSqlManager.writeLine(out, jsonErrorReturn.build());
	    LoggerUtil.log(request, e);

	    return;
	}

	// Release connection in pool & remove all references
	if (action.equals(HttpParameter.CLOSE)) {
	    try {
		// ConnectionCloser.freeConnection(connection,
		// databaseConfigurator);
		databaseConfigurator.close(connection);
		if (connectionId == null) {
		    connectionId = ServerLoginActionSql.getConnectionId(connection);
		}
		ConnectionStore connectionStore = new ConnectionStore(username, sessionId, connectionId);
		connectionStore.remove();
		ServerSqlManager.writeLine(out, JsonOkReturn.build());
	    } catch (SQLException e) {
		JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
			JsonErrorReturn.ERROR_JDBC_ERROR, e.getMessage());
		ServerSqlManager.writeLine(out, errorReturn.build());
	    }
	    return;
	}

	List<SqlFirewallManager> sqlFirewallManagers = ServerSqlManager.getSqlFirewallMap().get(database);

	// Redirect if it's a metadaquery
	if (ServletMetadataQuery.isMetadataQueryAction(action)) {
	    MetadataQueryActionManager metadataQueryActionManager = new MetadataQueryActionManager(request, response,
		    out, sqlFirewallManagers, connection);
	    metadataQueryActionManager.execute();
	    return;
	}

	if (ServerSqlDispatchUtil.isStatement(action) && !ServerSqlDispatchUtil.isStoredProcedure(request)) {
	    ServerStatement serverStatement = new ServerStatement(request, response, sqlFirewallManagers, connection);
	    serverStatement.executeQueryOrUpdate(out);
	} else if (ServerSqlDispatchUtil.isStoredProcedure(request)) {
	    ServerCallableStatement serverCallableStatement = new ServerCallableStatement(request, response,
		    sqlFirewallManagers, connection);
	    serverCallableStatement.executeOrExecuteQuery(out);
	} else if (ServerSqlDispatchUtil.isConnectionModifier(action)) {
	    TransactionUtil.setConnectionModifierAction(request, response, out, action, connection);
	} else if (ServerSqlDispatchUtil.isSavepointModifier(action)) {
	    SavepointUtil.setSavepointExecute(request, response, out, action, connection);
	} else if (ServerSqlDispatchUtil.isConnectionReader(action)) {
	    TransactionUtil.getConnectionionInfosExecute(request, response, out, action, connection);
	} else {
	    throw new IllegalArgumentException("Invalid Sql Action: " + action);
	}

    }



    /**
     * Clean connection store.
     */
    private void connectionStoreClean() {

	if (ConnectionStoreCleaner.timeToCleanConnectionStore()) {
	    ConnectionStoreCleaner cleaner = new ConnectionStoreCleaner();
	    cleaner.start();
	}

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
