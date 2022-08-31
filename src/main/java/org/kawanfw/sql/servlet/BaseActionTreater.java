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
package org.kawanfw.sql.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.servlet.connection.ConnectionIdUtil;
import org.kawanfw.sql.servlet.connection.ConnectionStore;
import org.kawanfw.sql.servlet.connection.RollbackUtil;
import org.kawanfw.sql.servlet.injection.classes.InjectedClassesStore;
import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesUtil;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonOkReturn;
import org.kawanfw.sql.util.FrameworkDebug;

public class BaseActionTreater {
    
    private static boolean DEBUG = FrameworkDebug.isSet(BaseActionTreater.class);
    
    private HttpServletRequest request;
    private HttpServletResponse response;
    private DatabaseConfigurator databaseConfigurator;
    private OutputStream out;

    public BaseActionTreater(HttpServletRequest request, HttpServletResponse response, OutputStream out) {
	super();
	this.request = request;
	this.response = response;
	this.out = out;

    }

    public boolean treatAndContinue() throws IOException, SQLException {
	String action = request.getParameter(HttpParameter.ACTION);
	String username = request.getParameter(HttpParameter.USERNAME);
	String database = request.getParameter(HttpParameter.DATABASE);
	String sessionId = request.getParameter(HttpParameter.SESSION_ID);

	if (isActionNullOrEmpty(action)) {
	    return false;
	}

	if (isActionLogin(action)) {
	    return false;
	}

	if (isDatabaseConfiguratorNull(database)) {
	    return false;
	}

	if (isActionGetConnection(action, username, database, sessionId)) {
	    return false;
	}

	if (isActiobBlobUpload(action, username)) {
	    return false;
	}

	if (isActionDownload(action, username)) {
	    return false;
	}

	if (action.equals(HttpParameter.LOGOUT) || action.equals(HttpParameter.DISCONNECT)) {
	    ServerLogout.logout(request, response, out, databaseConfigurator);
	    return false;
	}

	return true;

    }

    /**
     * @param action
     * @param username
     * @throws IOException
     * @throws SQLException
     */
    private boolean isActionDownload(String action, String username) throws IOException, SQLException {
	// No need to get a SQL connection for getting Blob size
	if (action.equals(HttpParameter.GET_BLOB_LENGTH)) {
	    BlobLengthGetter blobLengthGetter = new BlobLengthGetter(request, response, out, username,
		    databaseConfigurator);
	    blobLengthGetter.getLength();
	    return true;
	}

	return false;
    }

    /**
     * @param action
     * @param username
     * @throws IOException
     * @throws SQLException
     */
    private boolean isActiobBlobUpload(String action, String username) throws IOException, SQLException {
	// Redirect if it's a File download request (Blobs/Clobs)
	if (action.equals(HttpParameter.BLOB_DOWNLOAD)) {
	    BlobDownloader blobDownloader = new BlobDownloader(request, response, out, username, databaseConfigurator);
	    blobDownloader.blobDownload();
	    return true;
	}
	return false;
    }

    /**
     * Create a new Connection if "/get_connection" has been asked
     * 
     * @param action
     * @param username
     * @param database
     * @param sessionId
     * @throws SQLException
     * @throws IOException
     */

    private boolean isActionGetConnection(String action, String username, String database, String sessionId)
	    throws SQLException, IOException {

	if (action.equals(HttpParameter.GET_CONNECTION)) {

	    debug(new Date() + " /get_connection called!");
	    
	    if (ConfPropertiesUtil.isStatelessMode()) {
		// Stateless we return the present connection Id
		ServerSqlManager.writeLine(out,
			JsonOkReturn.build("connection_id", ConnectionIdUtil.getStatelessConnectionId()));
		return true;
	    } else {

		Connection connection = null;

		try {
		    // Stateful: We create the Connection and store it before returning id
		    connection = databaseConfigurator.getConnection(database);
		    // Each Connection is identified by hashcode of connection
		    String connectionIdNew = ConnectionIdUtil.getConnectionId(connection);
		    // We store the Connection in Memory
		    ConnectionStore connectionStore = new ConnectionStore(username, sessionId, connectionIdNew);
		    connectionStore.put(connection);

		    ServerSqlManager.writeLine(out, JsonOkReturn.build("connection_id", connectionIdNew));
		    return true;
		} catch (Exception e) {
		    RollbackUtil.rollback(connection);
		    throw e;
		}
	    }

	}
	return false;
    }

    /**
     * @param database
     * @throws IOException
     */
    private boolean isDatabaseConfiguratorNull(String database) throws IOException {
	databaseConfigurator = InjectedClassesStore.get().getDatabaseConfigurators().get(database);

	if (databaseConfigurator == null) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.DATABASE_DOES_NOT_EXIST + database);
	    ServerSqlManager.writeLine(out, errorReturn.build());
	    return true;
	}
	return false;
    }

    /**
     * @param action
     * @throws IOException
     */
    private boolean isActionLogin(String action) throws IOException {
	if (action.equals(HttpParameter.LOGIN) || action.equals(HttpParameter.CONNECT)) {
	    ServerLoginActionSql serverLoginActionSql = new ServerLoginActionSql();
	    serverLoginActionSql.executeAction(request, response, out, action);
	    return true;
	}
	return false;
    }

    /**
     * @param action
     * @throws IOException
     */
    private boolean isActionNullOrEmpty(String action) throws IOException {
	if (action == null || action.isEmpty()) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.NO_ACTION_FOUND_IN_REQUEST);
	    ServerSqlManager.writeLine(out, errorReturn.build());
	    return true;
	}
	return false;
    }

    public DatabaseConfigurator getDatabaseConfigurator() {
	return databaseConfigurator;
    }

    public static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }
    
}
