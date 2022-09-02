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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Vector;

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
import org.kawanfw.sql.servlet.sql.LoggerUtil;
import org.kawanfw.sql.servlet.sql.ServerStatementUtil;
import org.kawanfw.sql.servlet.sql.StatementFailure;
import org.kawanfw.sql.servlet.sql.UpdateListenersCaller;
import org.kawanfw.sql.servlet.sql.dto.UpdateCountsArrayDto;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonSecurityMessage;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.IpUtil;

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

    private Set<SqlFirewallManager> sqlFirewallManagers;

    private DatabaseConfigurator databaseConfigurator;

    private Set<UpdateListener> updateListeners;

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

    public ServerStatementBatch(HttpServletRequest request, HttpServletResponse response,
	    Set<SqlFirewallManager> sqlFirewallManagers, Connection connection,
	    DatabaseConfigurator databaseConfigurator) throws SQLException {
	this.request = request;
	this.response = response;
	this.sqlFirewallManagers = sqlFirewallManagers;
	this.connection = connection;
	this.databaseConfigurator = databaseConfigurator;
	doPrettyPrinting = true; // Always pretty printing

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
	String blobId = request.getParameter(HttpParameter.BLOB_ID);
	debug("blobId: " + blobId);

	Statement statement = null;
	File blobFile = null;

	try {

	    if (blobId == null || blobId.isEmpty()) {
		throw new SQLException("blobId cannnot be null!.");
	    }

	    File blobsDir = databaseConfigurator.getBlobsDirectory(username);
	    blobFile = new File(blobsDir.toString() + File.separator + blobId);

	    if (!blobFile.exists()) {
		throw new FileNotFoundException("Cannot find file of batch SQL statement for Id: " + blobId);
	    }

	    // Throws a SQL exception if the order is not authorized:
	    String ipAddress = IpUtil.getRemoteAddr(request);

	    statement = connection.createStatement();
	    debug("before statement.addBatch() loop");
	    
	    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(blobFile));) {
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
		    String sql = line.trim();
		    debug("before new SqlSecurityChecker()");
		    checkFirewallGeneral(username, database, sql, ipAddress);
		    //checkFirewallForAllowExecute(username, database, sql, ipAddress);
		    statement.addBatch(sql);
		}
	    }

	    debug("before statement.executeBatch()");
	    int[] rc = statement.executeBatch();

	    callUpdateListenersInThread(blobFile, username, database, ipAddress);

	    UpdateCountsArrayDto updateCountsArrayDto = new UpdateCountsArrayDto(rc);
	    String jsonString = GsonWsUtil.getJSonString(updateCountsArrayDto);
	    ServerSqlManager.writeLine(out, jsonString);

	} catch (SQLException e) {
	    RollbackUtil.rollback(connection);

	    String message = StatementFailure.statementFailureBuild(blobId, e.toString(), doPrettyPrinting);

	    LoggerUtil.log(request, e, message);
	    throw e;

	} finally {
	    // NO! IOUtils.closeQuietly(out);

	    if (statement != null) {
		statement.close();
	    }

	    if (blobFile != null) {
		blobFile.delete();
	    }
	}
    }

    private void callUpdateListenersInThread(File blobFile, String username, String database, String ipAddress) {

	Thread t = new Thread() {
	    @Override
	    public void run() {
		try {

		    List<Object> parameterValues = new ArrayList<>();

		    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(blobFile));) {
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
			    String sqlOrder = line.trim();

			    UpdateListenersCaller updateListenersCaller = new UpdateListenersCaller(updateListeners,
				    connection);
			    updateListenersCaller.callUpdateListeners(username, database, sqlOrder, parameterValues,
				    ipAddress, false);
			}
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	};
	t.start();
    }

//    /**
//     * Checks the firewall rules for an ExecuteUpdate for a statement.
//     * 
//     * @param username
//     * @param database
//     * @param sqlOrder
//     * @param ipAddress
//     * @throws IOException
//     * @throws SQLException
//     * @throws SecurityException
//     */
//    private void checkFirewallForAllowExecute(String username, String database, String sqlOrder, String ipAddress)
//	    throws IOException, SQLException, SecurityException {
//	boolean isAllowed;
//	for (SqlFirewallManager sqlFirewallManager : sqlFirewallManagers) {
//
//	    isAllowed = sqlFirewallManager.allowExecute(username, database, connection);
//	    if (!isAllowed) {
//		List<Object> parameterValues = new ArrayList<>();
//
//		SqlEvent sqlEvent = SqlEventWrapper.sqlEventBuild(username, database, ipAddress, sqlOrder,
//			ServerStatementUtil.isPreparedStatement(request), parameterValues, false);
//
//		SqlFirewallTriggerWrapper.runIfStatementRefused(sqlEvent, sqlFirewallManager, connection);
//
//		String message = JsonSecurityMessage.statementNotAllowedBuild(sqlOrder,
//			"Statement not allowed for for execute", doPrettyPrinting);
//		throw new SecurityException(message);
//
//	    }
//	}
//    }

    /**
     * Check general firewall rules
     * 
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

	    SqlEvent sqlEvent = SqlEventWrapper.sqlEventBuild(username, database, ipAddress, sqlOrder,
		    ServerStatementUtil.isPreparedStatement(request), new Vector<Object>(), false);

	    isAllowed = sqlFirewallManager.allowSqlRunAfterAnalysis(sqlEvent, connection);

	    if (!isAllowed) {
		break;
	    }
	}

	if (!isAllowed) {
	    List<Object> parameterValues = new ArrayList<>();

	    SqlEvent sqlEvent = SqlEventWrapper.sqlEventBuild(username, database, ipAddress, sqlOrder,
		    ServerStatementUtil.isPreparedStatement(request), parameterValues, false);

	    SqlFirewallTriggerWrapper.runIfStatementRefused(sqlEvent, sqlFirewallOnDeny, connection);

	    String message = JsonSecurityMessage.statementNotAllowedBuild(sqlOrder, "Statement not allowed",
		    doPrettyPrinting);
	    throw new SecurityException(message);
	}
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
