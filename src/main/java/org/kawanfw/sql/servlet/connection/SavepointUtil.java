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
package org.kawanfw.sql.servlet.connection;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonOkReturn;
import org.kawanfw.sql.transport.SavepointHttp;
import org.kawanfw.sql.util.ConnectionParms;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.HtmlConverter;

/**
 * @author Nicolas de Pomereu
 * 
 *         Wrapper for savepoint commands to decrease code in ServerSqlDispatch
 */
public class SavepointUtil {

    private static boolean DEBUG = FrameworkDebug.isSet(SavepointUtil.class);

    /**
     * Calls a setSavepoint(), setSavepoint(name), rollback(Savepoint
     * savepoint), releaseSavepoint(Savepoint savepoint)
     * 
     * @param request
     * @param response
     *            TODO
     * @param out
     * @param action
     * @param connection
     * @throws IOException
     * @throws SQLException
     * @throws IllegalArgumentException
     */
    public static void setSavepointExecute(HttpServletRequest request,
	    HttpServletResponse response, OutputStream out, String action,
	    Connection connection)
	    throws IOException, SQLException, IllegalArgumentException {

	try {
	    saveSavePointExecuteThrowException(request, out, action,
		    connection);
	} catch (IllegalArgumentException e) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response,
		    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, e.getMessage());
	    ServerSqlManager.writeLine(out, errorReturn.build());
	} catch (SQLException e) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response,
		    HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_JDBC_ERROR, e.getMessage());
	    ServerSqlManager.writeLine(out, errorReturn.build());
	}

    }

    private static void saveSavePointExecuteThrowException(
	    HttpServletRequest request, OutputStream out, String action,
	    Connection connection) throws SQLException, IOException {
	String username = request.getParameter(HttpParameter.USERNAME);
	String sessionId = request.getParameter(HttpParameter.SESSION_ID);
	String connectionId = request.getParameter(HttpParameter.CONNECTION_ID);
	
	ConnectionStore connectionStore = new ConnectionStore(username,
		sessionId, connectionId);

	if (action.equals(HttpParameter.SET_SAVEPOINT)) {

	    Savepoint savepoint = connection.setSavepoint();
	    connectionStore.put(savepoint);

	    SavepointHttp savepointHttp = new SavepointHttp(
		    savepoint.getSavepointId(), "noname");
	    String savepointStr = savepointHttp.toString();
	    savepointStr = HtmlConverter.toHtml(savepointStr);

	    ServerSqlManager.writeLine(out,
		    JsonOkReturn.build("result", savepointStr));

	    return;

	} else if (action.equals(HttpParameter.SET_SAVEPOINT_NAME)) {
	    String name = request.getParameter(ConnectionParms.NAME);

	    name = HtmlConverter.fromHtml(name);

	    Savepoint savepoint = connection.setSavepoint(name);
	    connectionStore.put(savepoint);

	    SavepointHttp savepointHttp = new SavepointHttp(-1,
		    savepoint.getSavepointName());
	    String savepointStr = savepointHttp.toString();
	    savepointStr = HtmlConverter.toHtml(savepointStr);

	    ServerSqlManager.writeLine(out,
		    JsonOkReturn.build("savepoint", savepointStr));

	    return;

	} else if (action.equals(HttpParameter.ROLLBACK_SAVEPOINT)) {
	    String savepointStr = request.getParameter(HttpParameter.SAVEPOINT);
	    savepointStr = HtmlConverter.fromHtml(savepointStr);

	    Savepoint savepointInfo = SavepointHttp
		    .buildFromString(savepointStr);

	    Savepoint savepoint = connectionStore.getSavepoint(savepointInfo);

	    if (savepoint == null) {
		throw new SQLException("Savepoint does not exists anymore");
	    }

	    connection.rollback(savepoint);

	    // ServerSqlManager.writeLine(out, TransferStatus.SEND_OK);
	    ServerSqlManager.writeLine(out, JsonOkReturn.build());
	    return;

	} else if (action.equals(HttpParameter.RELEASE_SAVEPOINT)) {
	    String savepointStr = request.getParameter(HttpParameter.SAVEPOINT);
	    savepointStr = HtmlConverter.fromHtml(savepointStr);

	    Savepoint savepointInfo = SavepointHttp
		    .buildFromString(savepointStr);

	    Savepoint savepoint = connectionStore.getSavepoint(savepointInfo);

	    if (savepoint == null) {
		throw new SQLException("Savepoint does not esxists anymore");
	    }

	    connection.releaseSavepoint(savepoint);
	    connectionStore.remove(savepointInfo);

	    // ServerSqlManager.writeLine(out, TransferStatus.SEND_OK);
	    ServerSqlManager.writeLine(out, JsonOkReturn.build());
	    return;
	} else {
	    throw new IllegalArgumentException(
		    "Invalid Sql Action for setSavepointExecute(): " + action);
	}
    }

    /**
     * Method called by children Servlet for debug purpose println is done only
     * if class name name is in kawansoft-debug.ini
     */
    public static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
