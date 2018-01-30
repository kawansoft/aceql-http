/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP                                     
 * Copyright (C) 2017,  KawanSoft SAS
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;

import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.servlet.ConnectionCloser;
import org.kawanfw.sql.servlet.DatabaseConfiguratorCall;
import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.servlet.connection.ConnectionStore;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonSecurityMessage;
import org.kawanfw.sql.servlet.sql.json_return.JsonUtil;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.FrameworkFileUtil;

/**
 * @author KawanSoft S.A.S
 * 
 *         Allows to execute the Statement or Prepared Statement on the Server
 *         as executeQuery() or executeUpdate()
 */
public class ServerStatement {
    private static boolean DEBUG = FrameworkDebug.isSet(ServerStatement.class);

    public static String CR_LF = System.getProperty("line.separator");

    private Connection connection = null;
    // private String username = null;

    /** The http request */
    private HttpServletRequest request;

    private DatabaseConfigurator databaseConfigurator;

    private HttpServletResponse response;

    private Boolean doPrettyPrinting;

    /**
     * Default Constructor
     * 
     * @param request
     *            the http request
     * @param response
     *            the http servlet response
     * @param databaseConfigurator
     * @param connection
     * @param sqlOrderAndParmsStore
     *            the Sql order and parms
     */

    public ServerStatement(HttpServletRequest request,
	    HttpServletResponse response,
	    DatabaseConfigurator databaseConfigurator, Connection connection)
	    throws SQLException {
	this.request = request;
	this.response = response;
	this.databaseConfigurator = databaseConfigurator;
	this.connection = connection;

	String prettyPrinting = request
		.getParameter(HttpParameter.PRETTY_PRINTING);
	// doPrettyPrinting = new Boolean(prettyPrinting);
	doPrettyPrinting = Boolean.valueOf(prettyPrinting);

    }

    /**
     * Execute the SQL query or update. <br>
     * 
     * @param out
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SQLException
     */
    public void executeQueryOrUpdate(OutputStream out)
	    throws FileNotFoundException, IOException, SQLException {

	// Get the GZIP Stream if necessary
	OutputStream outFinal = null;

	try {

	    outFinal = getFinalOutputStream(out);

	    // Execute it
	    if (isPreparedStatement()) {
		executePrepStatement(outFinal);
	    } else {
		executeStatement(outFinal);
	    }
	} catch (SecurityException e) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response,
		    HttpServletResponse.SC_UNAUTHORIZED,
		    JsonErrorReturn.ERROR_ACEQL_UNAUTHORIZED, e.getMessage());
	    ServerSqlManager.writeLine(outFinal, errorReturn.build());
	} catch (SQLException e) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response,
		    HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_JDBC_ERROR, e.getMessage());
	    ServerSqlManager.writeLine(outFinal, errorReturn.build());
	} catch (Exception e) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response,
		    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
		    JsonErrorReturn.ERROR_ACEQL_FAILURE, e.getMessage(),
		    ExceptionUtils.getStackTrace(e));
	    ServerSqlManager.writeLine(outFinal, errorReturn.build());
	} finally {

	    // IOUtils.closeQuietly(outFinal);

	    if (outFinal != null) {
		try {
		    outFinal.close();
		} catch (Exception e) {
		    // e.printStackTrace();
		}
	    }

	    String username = request.getParameter(HttpParameter.USERNAME);
	    String sessionId = request.getParameter(HttpParameter.SESSION_ID);

	    if (ConnectionStore.isStateless(username, sessionId)) {
		// Release the connection
		ConnectionCloser.freeConnection(connection,
			databaseConfigurator);
	    }
	}
    }

    /**
     * Get the OutputStream to use. A regular one or a GZIP_RESULT one
     * 
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private OutputStream getFinalOutputStream(OutputStream out)
	    throws FileNotFoundException, IOException {

	String gzipResult = request.getParameter(HttpParameter.GZIP_RESULT);
	boolean doGzip = Boolean.parseBoolean(gzipResult);

	// No GZIP if execute update
	if (isExecuteUpdate()) {
	    doGzip = false;
	}

	if (doGzip) {
	    GZIPOutputStream gZipOut = new GZIPOutputStream(out);
	    return gZipOut;
	} else {
	    OutputStream outFinal = out;
	    return outFinal;

	}
    }

    private boolean isPreparedStatement() {
	String preparedStatement = request
		.getParameter(HttpParameter.PREPARED_STATEMENT);
	return Boolean.parseBoolean(preparedStatement);

    }

    /**
     * Execute the passed SQL Statement and return: <br>
     * - The result set as a List of Maps for SELECT statements. <br>
     * - The return code for other statements
     * 
     * @param sqlOrder
     *            the qsql order
     * @param sqlParms
     *            the sql parameters
     * @param out
     *            the writer where to write to result set output
     * 
     * 
     * @throws SQLException
     */
    private void executePrepStatement(OutputStream out)
	    throws SQLException, IOException {

	String username = request.getParameter(HttpParameter.USERNAME);
	String sqlOrder = request.getParameter(HttpParameter.SQL);

	debug("sqlOrder        : " + sqlOrder);

	PreparedStatement preparedStatement = null;

	// Class to set all the statement parameters
	ServerPreparedStatementParameters serverPreparedStatementParameters = null;

	try {

	    if (sqlOrder == null || sqlOrder.isEmpty()) {
		throw new SQLException("A 'sql' statement is required.");
	    }
	    preparedStatement = connection.prepareStatement(sqlOrder);

	    debug("before ServerPreparedStatementParameters");

	    serverPreparedStatementParameters = new ServerPreparedStatementParameters(
		    preparedStatement, request);

	    try {
		serverPreparedStatementParameters.setParameters();
	    } catch (IllegalArgumentException e) {
		JsonErrorReturn errorReturn = new JsonErrorReturn(response,
			HttpServletResponse.SC_BAD_REQUEST,
			JsonErrorReturn.ERROR_ACEQL_ERROR, e.getMessage());
		ServerSqlManager.writeLine(out, errorReturn.build());
		return;
	    }

	    // Throws a SQL exception if the order is not authorized:
	    debug("before new SqlSecurityChecker()");

	    String database = request.getParameter(HttpParameter.DATABASE);
	    DatabaseConfigurator databaseConfigurator = ServerSqlManager
		    .getDatabaseConfigurator(database);

	    boolean isAllowed = true;

	    String ipAddress = request.getRemoteAddr();

	    boolean isAllowedAfterAnalysis = databaseConfigurator
		    .allowStatementAfterAnalysis(username, connection,
			    ipAddress, sqlOrder, isPreparedStatement(),
			    serverPreparedStatementParameters
				    .getParameterValues());

	    if (!isAllowedAfterAnalysis) {
		isAllowed = false;
	    }

	    if (!isAllowed) {

		debug("Before DatabaseConfiguratorCall.runIfStatementRefused");
		DatabaseConfiguratorCall.runIfStatementRefused(
			databaseConfigurator, ipAddress, connection, ipAddress,
			sqlOrder,
			serverPreparedStatementParameters.getParameterValues());
		debug("After  DatabaseConfiguratorCall.runIfStatementRefused");

		String message = JsonSecurityMessage
			.prepStatementNotAllowedBuild(sqlOrder,
				"Prepared Statement not allowed",
				serverPreparedStatementParameters
					.getParameterTypes(),
				serverPreparedStatementParameters
					.getParameterValues(),
				doPrettyPrinting);
		throw new SecurityException(message);
	    }

	    debug("before executeQuery() / executeUpdate()");

	    if (isExecuteUpdate()) {

		if (!DatabaseConfiguratorCall.allowExecuteUpdate(
			databaseConfigurator, username, connection)) {

		    DatabaseConfiguratorCall.runIfStatementRefused(
			    databaseConfigurator, username, connection,
			    ipAddress, sqlOrder,
			    serverPreparedStatementParameters
				    .getParameterValues());

		    String message = JsonSecurityMessage
			    .prepStatementNotAllowedBuild(sqlOrder,
				    "Prepared Statement not allowed for executeUpdate",
				    serverPreparedStatementParameters
					    .getParameterTypes(),
				    serverPreparedStatementParameters
					    .getParameterValues(),
				    doPrettyPrinting);

		    throw new SecurityException(message);
		}

		int rc = preparedStatement.executeUpdate();

		StringWriter sw = new StringWriter();
		JsonGeneratorFactory jf = JsonUtil.getJsonGeneratorFactory(
			JsonUtil.DEFAULT_PRETTY_PRINTING);
		JsonGenerator gen = jf.createGenerator(sw);

		gen.writeStartObject().write("status", "OK")
			.write("row_count", rc).writeEnd();
		gen.close();

		ServerSqlManager.write(out, sw.toString());

	    } else {

		ResultSet rs = null;

		try {

		    rs = preparedStatement.executeQuery();

		    ResultSetWriter resultSetWriter = new ResultSetWriter(
			    request, out, username, sqlOrder);
		    resultSetWriter.write(rs);

		} finally {

		    if (rs != null) {
			rs.close();
		    }
		}
	    }
	} catch (SQLException e) {

	    String message = StatementFailure.prepStatementFailureBuild(
		    sqlOrder, e.toString(),
		    serverPreparedStatementParameters.getParameterTypes(),
		    serverPreparedStatementParameters.getParameterValues(),
		    doPrettyPrinting);

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
     * @param sqlOrder
     *            the qsql order
     * @param sqlParms
     *            the sql parameters
     * @param out
     *            the writer where to write to result set output
     * 
     * 
     * @throws SQLException
     */
    private void executeStatement(OutputStream out)
	    throws SQLException, IOException {

	String username = request.getParameter(HttpParameter.USERNAME);
	String sqlOrder = request.getParameter(HttpParameter.SQL);
	debug("sqlOrder   : " + sqlOrder);

	Statement statement = null;

	String database = request.getParameter(HttpParameter.DATABASE);
	DatabaseConfigurator databaseConfigurator = ServerSqlManager
		.getDatabaseConfigurator(database);

	try {

	    if (sqlOrder == null || sqlOrder.isEmpty()) {
		throw new SQLException("A 'sql' statement is required.");
	    }

	    statement = connection.prepareStatement(sqlOrder);
	    // Throws a SQL exception if the order is not authorized:
	    debug("before new SqlSecurityChecker()");

	    boolean isAllowed = true;

	    boolean statementClassAllowed = DatabaseConfiguratorCall
		    .allowStatementClass(databaseConfigurator, username,
			    connection);

	    if (!statementClassAllowed) {
		isAllowed = false;
	    }

	    String ipAddress = request.getRemoteAddr();

	    boolean isAllowedAfterAnalysis = databaseConfigurator
		    .allowStatementAfterAnalysis(username, connection,
			    ipAddress, sqlOrder, isPreparedStatement(),
			    new Vector<Object>());

	    if (!isAllowedAfterAnalysis) {
		isAllowed = false;
	    }

	    if (!isAllowed) {

		DatabaseConfiguratorCall.runIfStatementRefused(
			databaseConfigurator, username, connection, ipAddress,
			sqlOrder, new Vector<Object>());

		String message = JsonSecurityMessage.statementNotAllowedBuild(
			sqlOrder, "Statement not allowed", doPrettyPrinting);
		throw new SecurityException(message);
	    }

	    statement = connection.createStatement();

	    debug("before executeQuery() / executeUpdate(sqlOrder)");

	    if (isExecuteUpdate()) {

		if (!DatabaseConfiguratorCall.allowExecuteUpdate(
			databaseConfigurator, username, connection)) {

		    DatabaseConfiguratorCall.runIfStatementRefused(
			    databaseConfigurator, username, connection,
			    ipAddress, sqlOrder, new Vector<Object>());

		    String message = JsonSecurityMessage
			    .statementNotAllowedBuild(sqlOrder,
				    "Statement not allowed for for executeUpdate",
				    doPrettyPrinting);
		    throw new SecurityException(message);
		}

		int rc = -1;

		rc = statement.executeUpdate(sqlOrder);

		StringWriter sw = new StringWriter();

		JsonGeneratorFactory jf = JsonUtil.getJsonGeneratorFactory(
			JsonUtil.DEFAULT_PRETTY_PRINTING);
		JsonGenerator gen = jf.createGenerator(sw);

		gen.writeStartObject().write("status", "OK")
			.write("row_count", rc).writeEnd();
		gen.close();

		ServerSqlManager.write(out, sw.toString());

	    } else {
		ResultSet rs = null;

		try {

		    ServerSqlUtil.setMaxRowsToReturn(statement,
			    databaseConfigurator);
		    debug("sqlorder: " + sqlOrder);

		    rs = statement.executeQuery(sqlOrder);

		    ResultSetWriter resultSetWriter = new ResultSetWriter(
			    request, out, username, sqlOrder);
		    resultSetWriter.write(rs);

		} finally {
		    if (rs != null) {
			rs.close();
		    }
		}
	    }
	} catch (SQLException e) {

	    String message = StatementFailure.statementFailureBuild(sqlOrder,
		    e.toString(), doPrettyPrinting);

	    LoggerUtil.log(request, e, message);
	    throw e;

	} finally {
	    // NO! IOUtils.closeQuietly(out);

	    if (statement != null) {
		statement.close();
	    }
	}
    }

    private boolean isExecuteUpdate() {

	return request.getParameter(HttpParameter.ACTION)
		.equals(HttpParameter.EXECUTE_UPDATE);
    }

    /**
     * Create our own temp file in user.home/kawansoft/tmp
     * 
     * @return the tempfile to create
     * 
     */
    public static synchronized File createTempFileForResultSet() {
	String unique = FrameworkFileUtil.getUniqueId();
	String tempDir = FrameworkFileUtil.getKawansoftTempDir();
	String tempFile = tempDir + File.separator + "server-result-set-"
		+ unique + ".tmp";

	return new File(tempFile);
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
