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
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.servlet.connection.RollbackUtil;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonSecurityMessage;
import org.kawanfw.sql.servlet.sql.json_return.JsonUtil;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * @author KawanSoft S.A.S
 *
 *         Allows to execute the Statement or Prepared Statement on the Server
 *         as a raw execute()
 */

public class ServerStatementRawExecute {
    private static boolean DEBUG = FrameworkDebug.isSet(ServerStatementRawExecute.class);

    public static String CR_LF = System.getProperty("line.separator");

    private Connection connection = null;
    // private String username = null;

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

    public ServerStatementRawExecute(HttpServletRequest request, HttpServletResponse response,
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
    public void execute(OutputStream out) throws FileNotFoundException, IOException, SQLException {

	try {

	    //System.err.println("ServerStatementUtil.isPreparedStatement(request): " + ServerStatementUtil.isPreparedStatement(request));

	    // Execute it
	    if (ServerStatementUtil.isPreparedStatement(request)) {
		executePrepStatement(out);
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
	debug("sqlOrder   : " + sqlOrder);

	Statement statement = null;

	DatabaseConfigurator databaseConfigurator = ServerSqlManager.getDatabaseConfigurator(database);

	try {

	    if (sqlOrder == null || sqlOrder.isEmpty()) {
		throw new SQLException("A 'sql' statement is required.");
	    }

	    statement = connection.createStatement();
	    // Throws a SQL exception if the order is not authorized:
	    debug("before new SqlSecurityChecker()");

	    String ipAddress = request.getRemoteAddr();

	    checkFirewallGeneral(username, database, sqlOrder, ipAddress);
	    statement = connection.createStatement();

	    debug("before executeQuery() / executeUpdate(sqlOrder)");
	    doExecute(out, databaseConfigurator, username, database, sqlOrder, statement, ipAddress);

	} catch (SQLException e) {

	    RollbackUtil.rollback(connection);

	    e.printStackTrace();
	    String message = StatementFailure.statementFailureBuild(sqlOrder, e.toString(), doPrettyPrinting);

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
    private void executePrepStatement(OutputStream out) throws SQLException, IOException {
	String username = request.getParameter(HttpParameter.USERNAME);
	String database = request.getParameter(HttpParameter.DATABASE);
	String sqlOrder = request.getParameter(HttpParameter.SQL);

	PreparedStatement preparedStatement = null;
	DatabaseConfigurator databaseConfigurator = ServerSqlManager.getDatabaseConfigurator(database);

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

	    debug("before execute()");
	    doExecute(out, databaseConfigurator, username, database, sqlOrder, preparedStatement,
		    serverPreparedStatementParameters, ipAddress);

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
     * Calls the Statement.execute() method.
     * @param out
     * @param atabaseConfigurator TODO
     * @param username
     * @param database
     * @param sqlOrder
     * @param statement
     * @param ipAddress
     * @throws IOException
     * @throws SQLException
     * @throws SecurityException
     */
    private void doExecute(OutputStream out, DatabaseConfigurator databaseConfigurator, String username, String database,
	    String sqlOrder, Statement statement, String ipAddress) throws IOException, SQLException, SecurityException {
	checkFirewallForExecute(username, database, sqlOrder, ipAddress);

	checkFirewallForExecute(username, database, sqlOrder, ipAddress);
	ServerSqlUtil.setMaxRowsToReturn(request, username, database, statement, databaseConfigurator);

	@SuppressWarnings("unused")
	boolean executeResult = statement.execute(sqlOrder);

	if (! executeResult) {
	//if (statement.getUpdateCount() != -1) {
	    // It is an update statement or prepared statement
	    StringWriter sw = new StringWriter();
	    JsonGeneratorFactory jf = JsonUtil.getJsonGeneratorFactory(JsonUtil.DEFAULT_PRETTY_PRINTING);
	    JsonGenerator gen = jf.createGenerator(sw);

	    gen.writeStartObject().write("status", "OK").write("row_count", statement.getUpdateCount()).writeEnd();
	    gen.close();

	    ServerSqlManager.write(out, sw.toString());
	}
	else {
	    // It is a query
	    ResultSet rs = statement.getResultSet();
	    dumpResultSet(rs, out, sqlOrder);
	}

    }

    /**
     * Calls the PreparedStatement.execute() method.
     *
     * @param out
     * @param databaseConfigurator TODO
     * @param username
     * @param database
     * @param sqlOrder
     * @param preparedStatement
     * @param serverPreparedStatementParameters
     * @param ipAddress
     * @throws IOException
     * @throws SQLException
     * @throws SecurityException
     */
    private void doExecute(OutputStream out, DatabaseConfigurator databaseConfigurator, String username, String database,
	    String sqlOrder, PreparedStatement preparedStatement,
	    ServerPreparedStatementParameters serverPreparedStatementParameters, String ipAddress) throws IOException, SQLException, SecurityException {

	checkFirewallForExecute(username, database, sqlOrder, serverPreparedStatementParameters, ipAddress);
	ServerSqlUtil.setMaxRowsToReturn(request, username, database, preparedStatement, databaseConfigurator);

	@SuppressWarnings("unused")
	boolean executeResult = preparedStatement.execute();

	if (preparedStatement.getUpdateCount() != -1) {
	    // It is an update statement or prepared statement
	    StringWriter sw = new StringWriter();
	    JsonGeneratorFactory jf = JsonUtil.getJsonGeneratorFactory(JsonUtil.DEFAULT_PRETTY_PRINTING);
	    JsonGenerator gen = jf.createGenerator(sw);

	    gen.writeStartObject().write("status", "OK").write("row_count", preparedStatement.getUpdateCount()).writeEnd();
	    gen.close();

	    ServerSqlManager.write(out, sw.toString());
	}
	else {
	    // It is a query
	    ResultSet rs = preparedStatement.getResultSet();
	    dumpResultSet(rs, out, sqlOrder);
	}


    }

    /**
     * @param username
     * @param database
     * @param sqlOrder
     * @param serverPreparedStatementParameters
     * @param ipAddress
     * @throws IOException
     * @throws SQLException
     * @throws SecurityException
     */
    private void checkFirewallForExecute(String username, String database, String sqlOrder,
	    ServerPreparedStatementParameters serverPreparedStatementParameters, String ipAddress)
	    throws IOException, SQLException, SecurityException {
	boolean isAllowedAfterAnalysis;
	for (SqlFirewallManager sqlFirewallManager : sqlFirewallManagers) {
	    isAllowedAfterAnalysis = sqlFirewallManager.allowExecute(username, database, connection);
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
		    ipAddress, sqlOrder, ServerStatementUtil.isPreparedStatement(request),
		    serverPreparedStatementParameters.getParameterValues());
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
     * Dumps the Result Set on the servlet stream
     * @param out
     * @param sqlOrder
     * @throws SQLException
     * @throws IOException
     */
    private void dumpResultSet(ResultSet rs, OutputStream out, String sqlOrder) throws SQLException, IOException {

	try {

	    debug("sqlorder: " + sqlOrder);

	    JsonGeneratorFactory jf = JsonUtil.getJsonGeneratorFactory(doPrettyPrinting);

	    JsonGenerator gen = jf.createGenerator(out);
	    gen.writeStartObject().write("status", "OK");

	 // Always force to Get ResultSetMetaData, because client side is probably a DB Visualizer tool
	    boolean fillResultSetMetaData = true;

	    ResultSetWriter resultSetWriter = new ResultSetWriter(request, sqlOrder, gen, fillResultSetMetaData);
	    resultSetWriter.write(rs);

	    ServerSqlManager.writeLine(out);

	    gen.writeEnd(); // .write("status", "OK")
	    gen.flush();
	    gen.close();

	} finally {
	    if (rs != null) {
		rs.close();
	    }
	}
    }




    /**
     * @param username
     * @param database
     * @param sqlOrder
     * @param ipAddress
     * @throws IOException
     * @throws SQLException
     * @throws SecurityException
     */
    private void checkFirewallForExecute(String username, String database, String sqlOrder, String ipAddress)
	    throws IOException, SQLException, SecurityException {
	boolean isAllowed;
	for (SqlFirewallManager sqlFirewallManager : sqlFirewallManagers) {
	    isAllowed = sqlFirewallManager.allowExecute(username, database, connection);
	    if (!isAllowed) {
		List<Object> parameterValues = new ArrayList<>();
		sqlFirewallManager.runIfStatementRefused(username, database, connection, ipAddress, false, sqlOrder,
			parameterValues);

		String message = JsonSecurityMessage.statementNotAllowedBuild(sqlOrder,
			"Statement not allowed for for executeUpdate", doPrettyPrinting);
		throw new SecurityException(message);

	    }
	}
    }

    /**
     * @param username
     * @param database
     * @param sqlOrder
     * @param ipAddress
     * @throws IOException
     * @throws SQLException
     * @throws SecurityException
     */
    private void checkFirewallGeneral(String username, String database, String sqlOrder, String ipAddress)
	    throws IOException, SQLException, SecurityException {
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
