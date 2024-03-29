/*
 * Copyright (c)2023 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-02-21
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
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
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.SqlEventWrapper;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.api.util.SqlUtil;
import org.kawanfw.sql.api.util.firewall.LearningModeExecutor;
import org.kawanfw.sql.api.util.firewall.SqlFirewallTriggerWrapper;
import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.servlet.connection.RollbackUtil;
import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesStore;
import org.kawanfw.sql.servlet.injection.properties.OperationalMode;
import org.kawanfw.sql.servlet.sql.AceQLParameter;
import org.kawanfw.sql.servlet.sql.LoggerUtil;
import org.kawanfw.sql.servlet.sql.ResultSetWriter;
import org.kawanfw.sql.servlet.sql.ServerStatementUtil;
import org.kawanfw.sql.servlet.sql.StatementFailure;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonSecurityMessage;
import org.kawanfw.sql.servlet.sql.json_return.JsonUtil;
import org.kawanfw.sql.servlet.sql.parameters.ServerPreparedStatementParameters;
import org.kawanfw.sql.servlet.sql.parameters.ServerPreparedStatementParametersUtil;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.IpUtil;

import oracle.jdbc.OracleTypes;

/**
 * @author KawanSoft S.A.S
 * @version 1.0
 *
 *          Allows to execute the Statement or Prepared Statement on the Server
 *          as executeQuery() or executeUpdate()
 */
public class AdvancedServerCallableStatement {
    private static boolean DEBUG = FrameworkDebug.isSet(AdvancedServerCallableStatement.class);

    public static String CR_LF = System.getProperty("line.separator");

    private Connection connection = null;
    // private String username = null;

    /** The http request */
    private HttpServletRequest request = null;
    private Set<SqlFirewallManager> sqlFirewallManagers;
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

    public AdvancedServerCallableStatement(HttpServletRequest request, HttpServletResponse response,
	    Set<SqlFirewallManager> sqlFirewallManagers, Connection connection) throws SQLException {
	this.request = request;
	this.response = response;
	this.sqlFirewallManagers = sqlFirewallManagers;
	this.connection = connection;

	doPrettyPrinting = true; // Always pretty printing
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
	    RollbackUtil.rollback(connection);

	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_FORBIDDEN,
		    JsonErrorReturn.ERROR_ACEQL_UNAUTHORIZED, e.getMessage());
	    ServerSqlManager.writeLine(outFinal, errorReturn.build());
	} catch (SQLException e) {
	    RollbackUtil.rollback(connection);

	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_JDBC_ERROR, e.getMessage());
	    ServerSqlManager.writeLine(outFinal, errorReturn.build());
	} catch (Exception e) {
	    RollbackUtil.rollback(connection);

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
	String htlmEncoding = request.getParameter(HttpParameter.HTML_ENCODING);

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
	    Map<Integer, AceQLParameter> inOutStatementParameters = ServerPreparedStatementParametersUtil
		    .buildParametersFromRequest(request);
	    serverPreparedStatementParameters = new ServerPreparedStatementParameters(username, database, sqlOrder,
		    callableStatement, inOutStatementParameters, htlmEncoding);

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
	    checkFirewallGeneral(username, database, sqlOrder, serverPreparedStatementParameters);
	    debug("before executeQuery() / execute()");

	    if (!isExecuteQuery()) {
		doExecute(out, callableStatement, serverPreparedStatementParameters);
	    } else {
		doSelect(out, sqlOrder, callableStatement, serverPreparedStatementParameters);
	    }
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

	    if (callableStatement != null) {
		callableStatement.close();
	    }

	    // Clean all
	    serverPreparedStatementParameters = null;
	}
    }

    /**
     * @param out
     * @param sqlOrder
     * @param callableStatement
     * @param serverPreparedStatementParameters
     * @throws SQLException
     * @throws IOException
     */
    private void doSelect(OutputStream out, String sqlOrder, CallableStatement callableStatement,
	    ServerPreparedStatementParameters serverPreparedStatementParameters) throws SQLException, IOException {
	ResultSet rs = null;

	try {

	    // Special treatment for Oracle queries
	    // Oracle requires to use CallableStatement.registerOutParameter(size + 1,
	    // OracleTypes.CURSOR) call
	    // Where size is the number of IN parameters + 1
	    // And Oracle requires to retrieve the ResultSet with a cast:
	    // rs= (ResultSet) callableStatement.getObject(size + 1);
	    SqlUtil sqlUtil = new SqlUtil(connection);
	    if (sqlUtil.isOracle()) {
		
		debug("DB is Oracle!");
		int size = serverPreparedStatementParameters.getParametersNumber();
		debug("Oracle Stored Procedure parameters size: " + size);
				
		callableStatement.registerOutParameter(size + 1, OracleTypes.CURSOR);
		callableStatement.executeQuery();

		rs = (ResultSet) callableStatement.getObject(size + 1);
		
	    } else {
		rs = callableStatement.executeQuery();
	    }

	    JsonGeneratorFactory jf = JsonUtil.getJsonGeneratorFactory(doPrettyPrinting);

	    JsonGenerator gen = jf.createGenerator(out);
	    gen.writeStartObject().write("status", "OK");

	    String fillResultSetMetaDataStr = request.getParameter(HttpParameter.FILL_RESULT_SET_META_DATA);
	    boolean fillResultSetMetaData = Boolean.parseBoolean(fillResultSetMetaDataStr);

	    ResultSetWriter resultSetWriter = new ResultSetWriter(request, sqlOrder, gen, fillResultSetMetaData);
	    resultSetWriter.write(rs);

	    ServerSqlManager.writeLine(out);

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

    /**
     * @param out
     * @param callableStatement
     * @param serverPreparedStatementParameters
     * @throws IOException
     * @throws SQLException
     * @throws SecurityException
     */
    private void doExecute(OutputStream out, CallableStatement callableStatement,
	    ServerPreparedStatementParameters serverPreparedStatementParameters)
	    throws IOException, SQLException, SecurityException {

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
    }

    /**
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

	OperationalMode operationalMode = ConfPropertiesStore.get().getOperationalModeMap(database);

	if (operationalMode.equals(OperationalMode.off)) {
	    return;
	}

	if (operationalMode.equals(OperationalMode.learning)) {
	    LearningModeExecutor.learn(sqlOrder, database);
	    return;
	}

	boolean isAllowed = true;
	String ipAddress = IpUtil.getRemoteAddr(request);

	SqlFirewallManager sqlFirewallOnDeny = null;
	for (SqlFirewallManager sqlFirewallManager : sqlFirewallManagers) {
	    sqlFirewallOnDeny = sqlFirewallManager;

	    SqlEvent sqlEvent = SqlEventWrapper.sqlEventBuild(username, database, ipAddress, sqlOrder,
		    ServerStatementUtil.isPreparedStatement(request),
		    serverPreparedStatementParameters.getParameterValues(), false);

	    isAllowed = sqlFirewallManager.allowSqlRunAfterAnalysis(sqlEvent, connection);
	    if (!isAllowed) {
		break;
	    }
	}

	if (!isAllowed) {

	    SqlEvent sqlEvent = SqlEventWrapper.sqlEventBuild(username, database, ipAddress, sqlOrder,
		    ServerStatementUtil.isPreparedStatement(request),
		    serverPreparedStatementParameters.getParameterValues(), false);
	    SqlFirewallTriggerWrapper.runIfStatementRefused(sqlEvent, sqlFirewallOnDeny, connection);

	    if (!operationalMode.equals(OperationalMode.detecting)) {
		String message = JsonSecurityMessage.prepStatementNotAllowedBuild(sqlOrder,
			"Callable Statement not allowed", serverPreparedStatementParameters.getParameterTypes(),
			serverPreparedStatementParameters.getParameterValues(), doPrettyPrinting);
		throw new SecurityException(message);
	    }

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

	    // HACK ProVersion 3.2.2: Never write null on Json
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
