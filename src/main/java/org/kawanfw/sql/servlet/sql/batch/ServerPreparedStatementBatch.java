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
package org.kawanfw.sql.servlet.sql.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.SqlEventWrapper;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.api.server.listener.UpdateListener;
import org.kawanfw.sql.api.util.firewall.SqlFirewallTriggerWrapper;
import org.kawanfw.sql.metadata.util.GsonWsUtil;
import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.servlet.connection.RollbackUtil;
import org.kawanfw.sql.servlet.injection.classes.InjectedClassesStore;
import org.kawanfw.sql.servlet.sql.AceQLParameter;
import org.kawanfw.sql.servlet.sql.LoggerUtil;
import org.kawanfw.sql.servlet.sql.ServerStatementUtil;
import org.kawanfw.sql.servlet.sql.StatementFailure;
import org.kawanfw.sql.servlet.sql.UpdateListenersCaller;
import org.kawanfw.sql.servlet.sql.dto.PrepStatementParamsHolder;
import org.kawanfw.sql.servlet.sql.dto.UpdateCountsArrayDto;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonSecurityMessage;
import org.kawanfw.sql.servlet.sql.parameters.ServerPreparedStatementParameters;
import org.kawanfw.sql.servlet.sql.parameters.ServerPreparedStatementParametersUtil;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.IpUtil;

/**
 * @author KawanSoft S.A.S
 *
 *         Allows to execute the Statement or Prepared Statement on the Server
 *         as executeQuery() or executeUpdate()
 */

public class ServerPreparedStatementBatch {
    private static boolean DEBUG = FrameworkDebug.isSet(ServerPreparedStatementBatch.class);

    public static String CR_LF = System.getProperty("line.separator");

    private Connection connection = null;

    /** The http request */
    private HttpServletRequest request;

    private HttpServletResponse response;

    private Boolean doPrettyPrinting;

    private List<SqlFirewallManager> sqlFirewallManagers;

    private DatabaseConfigurator databaseConfigurator;

    private List<UpdateListener> updateListeners;

    /**
     * Default Constructor
     *
     * @param request               the http request
     * @param response              the http servlet response
     * @param sqlFirewallManagers
     * @param connection
     * @param databaseConfigurator
     * @param sqlOrderAndParmsStore the Sql order and parms
     */

    public ServerPreparedStatementBatch(HttpServletRequest request, HttpServletResponse response,
	    List<SqlFirewallManager> sqlFirewallManagers, Connection connection,
	    DatabaseConfigurator databaseConfigurator) throws SQLException {
	this.request = request;
	this.response = response;
	this.sqlFirewallManagers = sqlFirewallManagers;
	this.connection = connection;
	doPrettyPrinting = true; // Always pretty printing
	this.databaseConfigurator = databaseConfigurator;

	String database = request.getParameter(HttpParameter.DATABASE);
	updateListeners = InjectedClassesStore.get().getUpdateListenerMap().get(database);

    }

    /**
     * Execute the SQL query or update. <br>
     *
     * @param out
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SQLException
     */
    public void executeBatch(OutputStream out) throws FileNotFoundException, IOException, SQLException {

	try {
	    // Execute it
	    executeStatement(out);
	} catch (SecurityException e) {
	    RollbackUtil.rollback(connection);

	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_FORBIDDEN,
		    JsonErrorReturn.ERROR_ACEQL_UNAUTHORIZED, e.getMessage());
	    ServerSqlManager.writeLine(out, errorReturn.build());
	} catch (SQLException e) {
	    RollbackUtil.rollback(connection);

	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_JDBC_ERROR, e.getMessage());
	    ServerSqlManager.writeLine(out, errorReturn.build());
	} catch (Exception e) {
	    RollbackUtil.rollback(connection);

	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
		    JsonErrorReturn.ERROR_ACEQL_FAILURE, e.getMessage(), ExceptionUtils.getStackTrace(e));
	    ServerSqlManager.writeLine(out, errorReturn.build());
	} finally {

	    // IOUtils.closeQuietly(outFinal);

	    if (out != null) {
		try {
		    out.close();
		} catch (Exception e) {
		    // e.printStackTrace();
		}
	    }

	}
    }

    /**
     * Execute the passed SQL Statement and return: <br>
     * - The result set as a List of Maps for SELECT statements. <br>
     * - The return code for other statements
     *
     * @param out the writer where to write to result set output
     *
     *
     * @throws SQLException
     */
    private void executeStatement(OutputStream out) throws SQLException, IOException {

	String username = request.getParameter(HttpParameter.USERNAME);
	String database = request.getParameter(HttpParameter.DATABASE);
	String sqlOrder = request.getParameter(HttpParameter.SQL);
	String blobId = request.getParameter(HttpParameter.BLOB_ID);
	String htlmEncoding = request.getParameter(HttpParameter.HTML_ENCODING);

	debug("sqlOrder             : " + sqlOrder);
	debug("blobId: " + blobId);

	PreparedStatement preparedStatement = null;
	File blobFile = null;

	try {

	    if (blobId == null || blobId.isEmpty()) {
		throw new SQLException("blob_id cannnot be null!.");
	    }

	    File blobsDir = databaseConfigurator.getBlobsDirectory(username);
	    blobFile = new File(blobsDir.toString() + File.separator + blobId);

	    if (!blobFile.exists()) {
		throw new FileNotFoundException(
			"Cannot find file of batch SQL prepared statement parameters for Id: " + blobId);
	    }

	    preparedStatement = connection.prepareStatement(sqlOrder);
	    debug("before PreparedStatement.addBatch() loop & executeBatch() ");

	    // Store in List the SqlOrder & parameterValues
	    List<List<Object>> parametersList = new ArrayList<>();

	    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(blobFile));) {
		String line = null;

		while ((line = bufferedReader.readLine()) != null) {

		    if (DEBUG) {
			ServerPreparedStatementParametersUtil.dump("line: " + line);
		    }

		    PrepStatementParamsHolder paramsHolder = GsonWsUtil.fromJson(line.trim(),
			    PrepStatementParamsHolder.class);

		    Map<Integer, AceQLParameter> inOutStatementParameters = ServerPreparedStatementParametersUtil
			    .buildParametersFromHolder(paramsHolder);

		    ServerPreparedStatementParameters serverPreparedStatementParameters = new ServerPreparedStatementParameters(
			    username, database, sqlOrder, preparedStatement, inOutStatementParameters, htlmEncoding);

		    try {
			serverPreparedStatementParameters.setParameters();
		    } catch (IllegalArgumentException e) {
			JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
				JsonErrorReturn.ERROR_ACEQL_ERROR, e.getMessage());
			ServerSqlManager.writeLine(out, errorReturn.build());
			return;
		    }

		    debug("before new SqlSecurityChecker()");
		    checkFirewallGeneral(username, database, sqlOrder, serverPreparedStatementParameters);
		    preparedStatement.addBatch();

		    parametersList.add(serverPreparedStatementParameters.getParameterValues());
		}
	    }

	    int[] rc = preparedStatement.executeBatch();

	    String ipAddress = IpUtil.getRemoteAddr(request);
	    callUpdateListenersInThread(sqlOrder, parametersList, username, database, ipAddress);

	    UpdateCountsArrayDto updateCountsArrayDto = new UpdateCountsArrayDto(rc);
	    String jsonString = GsonWsUtil.getJSonString(updateCountsArrayDto);
	    ServerSqlManager.writeLine(out, jsonString);

	} catch (SQLException e) {
	    RollbackUtil.rollback(connection);
	    String message = StatementFailure.statementFailureBuild(sqlOrder, e.toString(), doPrettyPrinting);

	    LoggerUtil.log(request, e, message);
	    throw e;

	} finally {
	    // NO! IOUtils.closeQuietly(out);

	    if (preparedStatement != null) {
		preparedStatement.close();
	    }
	}
    }

    /**
     * Checks the general firewall rules
     * 
     * @param username
     * @param database
     * @param sqlOrder
     * @param serverPreparedStatementParameters
     * @throws IOException
     * @throws SQLException
     * @throws SecurityException
     */
    private void checkFirewallGeneral(String username, String database, String sqlOrder,
	    ServerPreparedStatementParameters serverPreparedStatementParameters)
	    throws IOException, SQLException, SecurityException {
	String ipAddress = IpUtil.getRemoteAddr(request);

	boolean isAllowedAfterAnalysis = true;
	for (SqlFirewallManager sqlFirewallManager : sqlFirewallManagers) {

	    SqlEvent sqlEvent = SqlEventWrapper.sqlEventBuild(username, database, ipAddress, sqlOrder,
		    ServerStatementUtil.isPreparedStatement(request),
		    serverPreparedStatementParameters.getParameterValues(), false);

	    isAllowedAfterAnalysis = sqlFirewallManager.allowSqlRunAfterAnalysis(sqlEvent, connection);
	    if (!isAllowedAfterAnalysis) {
		SqlFirewallTriggerWrapper.runIfStatementRefused(sqlEvent, sqlFirewallManager, connection);
		break;
	    }
	}

	if (!isAllowedAfterAnalysis) {
	    String message = JsonSecurityMessage.prepStatementNotAllowedBuild(sqlOrder,
		    "Prepared Statement not allowed", serverPreparedStatementParameters.getParameterTypes(),
		    serverPreparedStatementParameters.getParameterValues(), doPrettyPrinting);
	    throw new SecurityException(message);
	}
    }

    private void callUpdateListenersInThread(String sqlOrder, List<List<Object>> parametersList, String username,
	    String database, String ipAddress) {

	Thread t = new Thread() {
	    @Override
	    public void run() {
		try {

		    for (List<Object> parameterValues : parametersList) {

			UpdateListenersCaller updateListenersCaller = new UpdateListenersCaller(updateListeners,
				connection);

			updateListenersCaller.callUpdateListeners(username, database, sqlOrder, parameterValues,
				ipAddress, true);

		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	};
	t.start();
    }

    /**
     * Debug function
     * 
     * @param s
     */

    protected void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
