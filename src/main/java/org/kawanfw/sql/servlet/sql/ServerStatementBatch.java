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
package org.kawanfw.sql.servlet.sql;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.metadata.util.GsonWsUtil;
import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.servlet.connection.RollbackUtil;
import org.kawanfw.sql.servlet.sql.dto.StatementsBatchDto;
import org.kawanfw.sql.servlet.sql.dto.UpdateCountsArrayDto;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonSecurityMessage;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * @author KawanSoft S.A.S
 *
 *         Allows to execute the Statement or Prepared Statement on the Server
 *         as executeQuery() or executeUpdate()
 */
public class ServerStatementBatch {
    private static boolean DEBUG = FrameworkDebug.isSet(ServerStatementBatch.class);

    public static String CR_LF = System.getProperty("line.separator");

    private Connection connection = null;

    /** The http request */
    private HttpServletRequest request;

    private HttpServletResponse response;

    private Boolean doPrettyPrinting;

    private List<SqlFirewallManager> sqlFirewallManagers;

    /**
     * Default Constructor
     *
     * @param request               the http request
     * @param response              the http servlet response
     * @param sqlFirewallManagers
     * @param connection
     * @param sqlOrderAndParmsStore the Sql order and parms
     */

    public ServerStatementBatch(HttpServletRequest request, HttpServletResponse response,
	    List<SqlFirewallManager> sqlFirewallManagers, Connection connection) throws SQLException {
	this.request = request;
	this.response = response;
	this.sqlFirewallManagers = sqlFirewallManagers;
	this.connection = connection;
	doPrettyPrinting = true; // Always pretty printing

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
	    if (ServerStatementUtil.isPreparedStatement(request)) {
		//executePrepStatement(out);
	    } else {
		executeStatement(out);
	    }
	} catch (SecurityException e) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_FORBIDDEN,
		    JsonErrorReturn.ERROR_ACEQL_UNAUTHORIZED, e.getMessage());
	    ServerSqlManager.writeLine(out, errorReturn.build());
	} catch (SQLException e) {

	    RollbackUtil.rollback(connection);

	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_JDBC_ERROR, e.getMessage());
	    ServerSqlManager.writeLine(out, errorReturn.build());
	} catch (Exception e) {
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
     * @param sqlOrder the qsql order
     * @param sqlParms the sql parameters
     * @param out      the writer where to write to result set output
     *
     *
     * @throws SQLException
     */
    
    @SuppressWarnings("unused")
    private void executePrepStatement(OutputStream out) throws SQLException, IOException {
	String username = request.getParameter(HttpParameter.USERNAME);
	String database = request.getParameter(HttpParameter.DATABASE);
	String sqlOrder = request.getParameter(HttpParameter.SQL);

	DatabaseConfigurator databaseConfigurator = ServerSqlManager.getDatabaseConfigurator(database);

	PreparedStatement preparedStatement = null;

	// Class to set all the statement parameters
	ServerPreparedStatementParameters serverPreparedStatementParameters = null;

	try {
	    if (sqlOrder == null || sqlOrder.isEmpty()) {
		throw new SQLException("A 'sql' statement is required.");
	    }
	    preparedStatement = connection.prepareStatement(sqlOrder);

	    debug("before ServerPreparedStatementParameters");
	    serverPreparedStatementParameters = new ServerPreparedStatementParameters(preparedStatement, request);

	    try {
		serverPreparedStatementParameters.setParameters();
	    } catch (IllegalArgumentException e) {
		JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
			JsonErrorReturn.ERROR_ACEQL_ERROR, e.getMessage());
		ServerSqlManager.writeLine(out, errorReturn.build());
		return;
	    }

	    // Throws a SQL exception if the order is not authorized:
	    debug("before new SqlSecurityChecker()");
	    String ipAddress = checkFirewallGeneral(username, database, sqlOrder, serverPreparedStatementParameters);

	    debug("before executeQuery() / executeUpdate()");
//	    doExecuteUpdate(out, username, database, sqlOrder, preparedStatement, serverPreparedStatementParameters,
//		    ipAddress);
		
	} catch (SQLException e) {

	    RollbackUtil.rollback(connection);

	    String message = StatementFailure.prepStatementFailureBuild(sqlOrder, e.toString(),
		    serverPreparedStatementParameters.getParameterTypes(),
		    serverPreparedStatementParameters.getParameterValues(), doPrettyPrinting);

	    LoggerUtil.log(request, e, message);
	    throw e;
	} finally {
	    // Close the ServerPreparedStatementParameters
	    if (serverPreparedStatementParameters != null) {
		serverPreparedStatementParameters.close();
	    }

	    if (preparedStatement != null) {
		preparedStatement.close();
	    }

	    // Clean all
	    serverPreparedStatementParameters = null;

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
	String jsonStringBatchList = request.getParameter(HttpParameter.BATCH_LIST);
	debug("jsonString BATCH_LIST: " + jsonStringBatchList);

	Statement statement = null;

	try {

	    if (jsonStringBatchList == null || jsonStringBatchList.isEmpty()) {
		throw new SQLException("At least one 'sql' statement is required in batch mode.");
	    }

	    statement = connection.createStatement();
	    // Throws a SQL exception if the order is not authorized:
	    debug("before new SqlSecurityChecker()");

	    String ipAddress = request.getRemoteAddr();

	    checkFirewallGeneral(username, database, jsonStringBatchList, ipAddress);
	    statement = connection.createStatement();

	    debug("before addBatch() loop & executeBatch() ");
	    
	    StatementsBatchDto statementsBatchDto = GsonWsUtil.fromJson(jsonStringBatchList, StatementsBatchDto.class);
	    List<String> batchList = statementsBatchDto.getBatchList();
	    for (String sql : batchList) {
		checkFirewallExecuteUpdate(username, database, sql, ipAddress);
		statement.addBatch(sql);
	    }
	    
	    int [] rc = statement.executeBatch();
	    UpdateCountsArrayDto updateCountsArrayDto = new UpdateCountsArrayDto(rc);
	    String jsonString = GsonWsUtil.getJSonString(updateCountsArrayDto);
	    ServerSqlManager.writeLine(out, jsonString);
	    
	} catch (SQLException e) {

	    RollbackUtil.rollback(connection);
	    String message = StatementFailure.statementFailureBuild(jsonStringBatchList, e.toString(), doPrettyPrinting);

	    LoggerUtil.log(request, e, message);
	    throw e;

	} finally {
	    // NO! IOUtils.closeQuietly(out);

	    if (statement != null) {
		statement.close();
	    }
	}
    }

    /**
     * Checks the firewall rules for an ExecuteUpdate for a prepared statement.
     * @param username
     * @param database
     * @param sqlOrder
     * @param serverPreparedStatementParameters
     * @param ipAddress
     * @throws IOException
     * @throws SQLException
     * @throws SecurityException
     */
    @SuppressWarnings("unused")
    private void checkFirewallForExecuteUpdate(String username, String database, String sqlOrder,
	    ServerPreparedStatementParameters serverPreparedStatementParameters, String ipAddress)
	    throws IOException, SQLException, SecurityException {
	boolean isAllowedAfterAnalysis;
	for (SqlFirewallManager sqlFirewallManager : sqlFirewallManagers) {
	    isAllowedAfterAnalysis = sqlFirewallManager.allowExecuteUpdate(username, database, connection);
	    if (!isAllowedAfterAnalysis) {
		List<Object> parameterValues = new ArrayList<>();
		sqlFirewallManager.runIfStatementRefused(username, database, connection, ipAddress, false, sqlOrder,
			parameterValues);

		String message = JsonSecurityMessage.prepStatementNotAllowedBuild(sqlOrder,
			"Prepared Statement not allowed for executeUpdate",
			serverPreparedStatementParameters.getParameterTypes(),
			serverPreparedStatementParameters.getParameterValues(), doPrettyPrinting);

		throw new SecurityException(message);
	    }
	}
    }

    /**
     * Checks the general firewall rules
     * @param username
     * @param database
     * @param sqlOrder
     * @param serverPreparedStatementParameters
     * @return
     * @throws IOException
     * @throws SQLException
     * @throws SecurityException
     */
    private String checkFirewallGeneral(String username, String database, String sqlOrder,
	    ServerPreparedStatementParameters serverPreparedStatementParameters)
	    throws IOException, SQLException, SecurityException {
	String ipAddress = request.getRemoteAddr();

	boolean isAllowedAfterAnalysis = false;
	for (SqlFirewallManager sqlFirewallManager : sqlFirewallManagers) {
	    isAllowedAfterAnalysis = sqlFirewallManager.allowSqlRunAfterAnalysis(username, database, connection,
		    ipAddress, sqlOrder, ServerStatementUtil.isPreparedStatement(request), serverPreparedStatementParameters.getParameterValues());
	    if (!isAllowedAfterAnalysis) {
		List<Object> parameterValues = new ArrayList<>();
		sqlFirewallManager.runIfStatementRefused(username, database, connection, ipAddress, false, sqlOrder,
			parameterValues);
		break;
	    }
	}

	if (!isAllowedAfterAnalysis) {
	    String message = JsonSecurityMessage.prepStatementNotAllowedBuild(sqlOrder,
		    "Prepared Statement not allowed", serverPreparedStatementParameters.getParameterTypes(),
		    serverPreparedStatementParameters.getParameterValues(), doPrettyPrinting);
	    throw new SecurityException(message);
	}
	return ipAddress;
    }




    /**
     * Checks the firewall rules for an ExecuteUpdate for a statement.
     * @param username
     * @param database
     * @param sqlOrder
     * @param ipAddress
     * @throws IOException
     * @throws SQLException
     * @throws SecurityException
     */
    private void checkFirewallExecuteUpdate(String username, String database, String sqlOrder, String ipAddress)
	    throws IOException, SQLException, SecurityException {
	boolean isAllowed;
	for (SqlFirewallManager sqlFirewallManager : sqlFirewallManagers) {
	    isAllowed = sqlFirewallManager.allowExecuteUpdate(username, database, connection);
	    if (!isAllowed) {
		List<Object> parameterValues = new ArrayList<>();
		sqlFirewallManager.runIfStatementRefused(username, database, connection, ipAddress, false,
			sqlOrder, parameterValues);

		String message = JsonSecurityMessage.statementNotAllowedBuild(sqlOrder,
			"Statement not allowed for for executeUpdate", doPrettyPrinting);
		throw new SecurityException(message);

	    }
	}
    }


    /**
     * Check general firewall rules
     * @param username
     * @param database
     * @param sqlOrder
     * @param ipAddress
     * @throws IOException
     * @throws SQLException
     * @throws SecurityException
     */
    private void checkFirewallGeneral(String username, String database, String sqlOrder, String ipAddress) throws IOException, SQLException, SecurityException {
	SqlFirewallManager sqlFirewallOnDeny = null;
	boolean isAllowed = true;
	for (SqlFirewallManager sqlFirewallManager : sqlFirewallManagers) {
	    sqlFirewallOnDeny = sqlFirewallManager;
	    isAllowed = sqlFirewallManager.allowStatementClass(username, database, connection);
	    if (!isAllowed) {
		break;
	    }

	    isAllowed = sqlFirewallManager.allowSqlRunAfterAnalysis(username, database, connection, ipAddress, sqlOrder,
		    ServerStatementUtil.isPreparedStatement(request), new Vector<Object>());
	    if (!isAllowed) {
		break;
	    }
	}

	if (!isAllowed) {
	    List<Object> parameterValues = new ArrayList<>();
	    sqlFirewallOnDeny.runIfStatementRefused(username, database, connection, ipAddress, false, sqlOrder,
		    parameterValues);

	    String message = JsonSecurityMessage.statementNotAllowedBuild(sqlOrder, "Statement not allowed",
		    doPrettyPrinting);
	    throw new SecurityException(message);
	}
    }



    /**
     * @param s
     */

    protected void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
