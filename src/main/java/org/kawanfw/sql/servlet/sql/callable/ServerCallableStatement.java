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
package org.kawanfw.sql.servlet.sql.callable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.servlet.sql.AceQLParameter;
import org.kawanfw.sql.servlet.sql.LoggerUtil;
import org.kawanfw.sql.servlet.sql.ResultSetWriter;
import org.kawanfw.sql.servlet.sql.ServerPreparedStatementParameters;
import org.kawanfw.sql.servlet.sql.StatementFailure;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonSecurityMessage;
import org.kawanfw.sql.servlet.sql.json_return.JsonUtil;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * @author KawanSoft S.A.S
 * @version 1.0
 *
 *          Allows to execute the Statement or Prepared Statement on the Server
 *          as executeQuery() or executeUpdate()
 */
public class ServerCallableStatement {
    private static boolean DEBUG = FrameworkDebug.isSet(ServerCallableStatement.class);

    public static String CR_LF = System.getProperty("line.separator");

    private Connection connection = null;
    // private String username = null;

    /** The http request */
    private HttpServletRequest request = null;
    private List<SqlFirewallManager> sqlFirewallManagers = new ArrayList<>();
    private HttpServletResponse response = null;
    private Boolean doPrettyPrinting = false;

    /**
     * Default Constructor
     *
     * @param request               the http request
     * @param response              the http servlet response
     * @param sqlFirewallManagers
     * @param connection
     * @param sqlOrderAndParmsStore the Sql order and parms
     */

    public ServerCallableStatement(HttpServletRequest request, HttpServletResponse response,
	    List<SqlFirewallManager> sqlFirewallManagers, Connection connection)
	    throws SQLException {
	this.request = request;
	this.response = response;
	this.connection = connection;

	String prettyPrinting = request.getParameter(HttpParameter.PRETTY_PRINTING);
	doPrettyPrinting = Boolean.valueOf(prettyPrinting);
    }

    /**
     * Execute the SQL query or execute procedures. <br>
     *
     * @param out
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SQLException
     */
    public void executeOrExecuteQuery(OutputStream out) throws FileNotFoundException, IOException, SQLException {

	// Get the GZIP Stream if necessary
	OutputStream outFinal = null;

	try {

	    outFinal = getFinalOutputStream(out);
	    executePrepStatement(outFinal);
	} catch (SecurityException e) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_FORBIDDEN,
		    JsonErrorReturn.ERROR_ACEQL_UNAUTHORIZED, e.getMessage());
	    ServerSqlManager.writeLine(outFinal, errorReturn.build());
	} catch (SQLException e) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_JDBC_ERROR, e.getMessage());
	    ServerSqlManager.writeLine(outFinal, errorReturn.build());
	} catch (Exception e) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
		    JsonErrorReturn.ERROR_ACEQL_FAILURE, e.getMessage(), ExceptionUtils.getStackTrace(e));
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
    private OutputStream getFinalOutputStream(OutputStream out) throws FileNotFoundException, IOException {

	String gzipResult = request.getParameter(HttpParameter.GZIP_RESULT);
	boolean doGzip = Boolean.parseBoolean(gzipResult);

	// No GZIP if execute update
	if (!isExecuteQuery()) {
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

	debug("sqlOrder        : " + sqlOrder);

	CallableStatement callableStatement = null;

	// Class to set all the statement parameters
	ServerPreparedStatementParameters serverPreparedStatementParameters = null;

	try {

	    if (sqlOrder == null || sqlOrder.isEmpty()) {
		throw new SQLException("A 'sql' statement is required.");
	    }
	    callableStatement = connection.prepareCall(sqlOrder);

	    // Set the IN Parameters
	    debug("before ServerPreparedStatementParameters");
	    serverPreparedStatementParameters = new ServerPreparedStatementParameters(callableStatement, request);

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

	    boolean isAllowed = true;
	    String ipAddress = request.getRemoteAddr();

	    SqlFirewallManager sqlFirewallOnDeny = null;
	    for (SqlFirewallManager sqlFirewallManager : sqlFirewallManagers) {
		sqlFirewallOnDeny = sqlFirewallManager;
		isAllowed = sqlFirewallManager.allowSqlRunAfterAnalysis(username, database, connection, ipAddress, sqlOrder,
			true, serverPreparedStatementParameters.getParameterValues());
		if (!isAllowed) {
		    break;
		}
	    }

	    if (!isAllowed) {

		sqlFirewallOnDeny.runIfStatementRefused(username, database, connection, ipAddress, false,
			sqlOrder, serverPreparedStatementParameters.getParameterValues());

		String message = JsonSecurityMessage.prepStatementNotAllowedBuild(sqlOrder,
			"Callable Statement not allowed", serverPreparedStatementParameters.getParameterTypes(),
			serverPreparedStatementParameters.getParameterValues(), doPrettyPrinting);
		throw new SecurityException(message);
	    }

	    debug("before executeQuery() / execute()");

	    if (!isExecuteQuery()) {

		for (SqlFirewallManager sqlFirewallManager : sqlFirewallManagers) {
		    isAllowed = sqlFirewallManager.allowExecuteUpdate(username, database, connection);
		    if (!isAllowed) {
			sqlFirewallManager.runIfStatementRefused(username, database, connection, ipAddress, false,
				sqlOrder, serverPreparedStatementParameters.getParameterValues());

			String message = JsonSecurityMessage.statementNotAllowedBuild(sqlOrder,
				"Statement not allowed for for executeUpdate", doPrettyPrinting);
			throw new SecurityException(message);
		    }
		}

		callableStatement.execute();

		StringWriter sw = new StringWriter();
		JsonGeneratorFactory jf = JsonUtil.getJsonGeneratorFactory(JsonUtil.DEFAULT_PRETTY_PRINTING);
		JsonGenerator gen = jf.createGenerator(sw);

		gen.writeStartObject().write("status", "OK");

		addToJsonOutParameters(callableStatement, serverPreparedStatementParameters, gen);

		gen.write("row_count", 0);
		gen.writeEnd(); // .write("status", "OK")

		gen.flush();
		gen.close();

		ServerSqlManager.write(out, sw.toString());

	    } else {

		ResultSet rs = null;

		try {

		    rs = callableStatement.executeQuery();

		    JsonGeneratorFactory jf = JsonUtil.getJsonGeneratorFactory(doPrettyPrinting);

		    JsonGenerator gen = jf.createGenerator(out);
		    gen.writeStartObject().write("status", "OK");

		    ResultSetWriter resultSetWriter = new ResultSetWriter(request, out, username, sqlOrder, gen);
		    resultSetWriter.write(rs);

		    /*
		     * KEEP THAT AS MODEL gen.writeStartArray("stored_procedure_out");
		     * gen.writeStartObject(); gen.write("key_1", "value_1"); gen.write("key_2",
		     * "value_2"); gen.writeEnd(); gen.writeEnd();
		     *
		     * gen.writeEnd(); // .write("status", "OK")
		     *
		     * gen.flush(); gen.close();
		     */

		    addToJsonOutParameters(callableStatement, serverPreparedStatementParameters, gen);

		    gen.writeEnd(); // .write("status", "OK")

		    gen.flush();
		    gen.close();

		} finally {

		    if (rs != null) {
			rs.close();
		    }
		}
	    }
	} catch (SQLException e) {

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

	    if (callableStatement != null) {
		callableStatement.close();
	    }

	    // Clean all
	    serverPreparedStatementParameters = null;

	}
    }

    /**
     * Add to the Json flow all the OUT parameter values set after the execution.
     *
     * @param callableStatement
     * @param serverPreparedStatementParameters
     * @param gen
     */
    private void addToJsonOutParameters(CallableStatement callableStatement,
	    ServerPreparedStatementParameters serverPreparedStatementParameters, JsonGenerator gen)
	    throws SQLException {

	Map<Integer, AceQLParameter> inOutStatementParameters = serverPreparedStatementParameters
		.getInOutStatementParameters();

	gen.writeStartObject("parameters_out_per_index");

	for (Map.Entry<Integer, AceQLParameter> entry : inOutStatementParameters.entrySet()) {
	    int outParamIndex = entry.getKey();
	    AceQLParameter aceQLParameter = entry.getValue();
	    String paramType = aceQLParameter.getParameterType();

	    if (!aceQLParameter.isOutParameter()) {
		continue;
	    }

	    String outParamValue = ServerCallableUtil.callableStatementGetStringValue(callableStatement, outParamIndex,
		    paramType);

	    // HACK Version 3.2.2: Never write null on Json
	    if (outParamValue == null) {
		outParamValue = "NULL";
	    }

	    gen.write("" + outParamIndex, outParamValue);
	}

	gen.writeEnd();

	gen.writeStartObject("parameters_out_per_name");

	for (Map.Entry<Integer, AceQLParameter> entry : inOutStatementParameters.entrySet()) {
	    AceQLParameter aceQLParameter = entry.getValue();
	    int paramIndex = aceQLParameter.getParameterIndex();
	    String paramType = aceQLParameter.getParameterType();

	    if (!aceQLParameter.isOutParameter()) {
		continue;
	    }

	    String outParameterName = aceQLParameter.getOutParameterName();

	    if (outParameterName == null) {
		continue;
	    }

	    String outParamValue = null;

	    outParamValue = ServerCallableUtil.callableStatementGetStringValue(callableStatement, paramIndex,
		    paramType);
	    gen.write(outParameterName, outParamValue);
	}

	gen.writeEnd();

    }

    private boolean isExecuteQuery() {

	return request.getParameter(HttpParameter.ACTION).equals(HttpParameter.EXECUTE_QUERY);
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
