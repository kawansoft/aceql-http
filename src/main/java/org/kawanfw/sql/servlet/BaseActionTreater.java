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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.servlet.connection.ConnectionIdUtil;
import org.kawanfw.sql.servlet.connection.ConnectionStore;
import org.kawanfw.sql.servlet.connection.RollbackUtil;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonOkReturn;
import org.kawanfw.sql.tomcat.ServletParametersStore;

public class BaseActionTreater {
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
     * Create a new Connection if /get_connection" has been asked
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

	    if (ServletParametersStore.isStatelessMode()) {
		// Stateless we return the present connection Id
		ServerSqlManager.writeLine(out,
			JsonOkReturn.build("connection_id", ConnectionIdUtil.getStatelessConnectionId()));
		return true;
	    } else {

		Connection connection = null;

		try {
		    // Statefull: We create the Connection and store it before returning id
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
	databaseConfigurator = ServerSqlManager.getDatabaseConfigurator(database);

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

}
