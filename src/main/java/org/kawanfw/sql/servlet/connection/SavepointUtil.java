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
package org.kawanfw.sql.servlet.connection;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.sql.metadata.util.GsonWsUtil;
import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonOkReturn;
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
	    RollbackUtil.rollback(connection);
	    
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response,
		    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, e.getMessage());
	    ServerSqlManager.writeLine(out, errorReturn.build());
	} catch (SQLException e) {
	    RollbackUtil.rollback(connection);

	    JsonErrorReturn errorReturn = new JsonErrorReturn(response,
		    HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_JDBC_ERROR, e.getMessage());
	    ServerSqlManager.writeLine(out, errorReturn.build());
	} catch (Exception e) {
	    RollbackUtil.rollback(connection);
	    
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
		    JsonErrorReturn.ERROR_ACEQL_FAILURE, e.getMessage(), ExceptionUtils.getStackTrace(e));
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

	debug("action: " + action);
	
	if (action.equals(HttpParameter.SET_SAVEPOINT)) {

	    Savepoint savepoint = connection.setSavepoint();
	    connectionStore.put(savepoint);

	    SavepointDto savepointDto = new SavepointDto(savepoint.getSavepointId(), "");
	    String jsonString = GsonWsUtil.getJSonString(savepointDto);
	    ServerSqlManager.writeLine(out, jsonString);
	    return;

	} else if (action.equals(HttpParameter.SET_NAMED_SAVEPOINT)) {
	    String name = request.getParameter(HttpParameter.NAME);
	    name = HtmlConverter.fromHtml(name);

	    debug("savepoint name: " + name);
	    
	    Savepoint savepoint = connection.setSavepoint(name);
	    connectionStore.put(savepoint);

	    //SavepointHttp savepointHttp = new SavepointHttp(-1, savepoint.getSavepointName());
	    //String savepointStr = savepointHttp.toString();
	    //savepointStr = HtmlConverter.toHtml(savepointStr);
	    SavepointDto savepointDto = new SavepointDto(-1, savepoint.getSavepointName());
	    String jsonString = GsonWsUtil.getJSonString(savepointDto);
	    ServerSqlManager.writeLine(out, jsonString);

	    return;

	} else if (action.equals(HttpParameter.ROLLBACK_SAVEPOINT)) {
	    String idStr = request.getParameter(HttpParameter.ID);
	    String name = request.getParameter(HttpParameter.NAME);

	    if (idStr == null || idStr.isEmpty()) {
		idStr = "-1";
	    }
	    
	    int id = Integer.parseInt(idStr);
	    name = HtmlConverter.fromHtml(name);

	    debug("Uploaded savepoint id  : " + id);
	    debug("Uploaded savepoint name: " + name);
	    
	    Savepoint savepoint = id >= 0 ? connectionStore.getSavepoint(id) : connectionStore.getSavepoint(name);

	    if (savepoint == null) {
		throw new SQLException("Savepoint does not exists anymore.");
	    }

	    connection.rollback(savepoint);
	    
	    ServerSqlManager.writeLine(out, JsonOkReturn.build());
	    return;

	} else if (action.equals(HttpParameter.RELEASE_SAVEPOINT)) {
	    String idStr = request.getParameter(HttpParameter.ID);
	    String name = request.getParameter(HttpParameter.NAME);

	    if (idStr == null || idStr.isEmpty()) {
		idStr = "-1";
	    }
	    
	    int id = Integer.parseInt(idStr);
	    name = HtmlConverter.fromHtml(name);
	    
	    Savepoint savepoint = id >= 0 ? connectionStore.getSavepoint(id) : connectionStore.getSavepoint(name);

	    if (savepoint == null) {
		throw new SQLException("Savepoint does not exists anymore.");
	    }
	    connection.releaseSavepoint(savepoint);
	    connectionStore.remove(savepoint);

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
