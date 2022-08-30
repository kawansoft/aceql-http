/*
 * Copyright (c)2022 KawanSoft S.A.S.
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-30
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.SqlEventWrapper;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.api.util.firewall.SqlFirewallTriggerWrapper;
import org.kawanfw.sql.metadata.AceQLMetaData;
import org.kawanfw.sql.metadata.JdbcDatabaseMetaData;
import org.kawanfw.sql.metadata.Table;
import org.kawanfw.sql.metadata.dto.JdbcDatabaseMetaDataDto;
import org.kawanfw.sql.metadata.dto.TableDto;
import org.kawanfw.sql.metadata.dto.TableNamesDto;
import org.kawanfw.sql.metadata.util.GsonWsUtil;
import org.kawanfw.sql.servlet.connection.RollbackUtil;
import org.kawanfw.sql.servlet.sql.ServerStatementUtil;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonSecurityMessage;
import org.kawanfw.sql.util.IpUtil;

/**
 * Execute the metadata query asked by user.
 *
 * @author Nicolas de Pomereu
 */
public class MetadataQueryActionManager {

    private HttpServletRequest request = null;
    private HttpServletResponse response = null;
    private Connection connection = null;
    List<SqlFirewallManager> sqlFirewallManagers = new ArrayList<>();

    private OutputStream out = null;

    public MetadataQueryActionManager(HttpServletRequest request, HttpServletResponse response, OutputStream out,
	    List<SqlFirewallManager> sqlFirewallManagers, Connection connection) {
	super();
	this.request = request;
	this.response = response;
	this.out = out;
	this.sqlFirewallManagers = sqlFirewallManagers;
	this.connection = connection;

    }

    public void execute() throws SQLException, IOException {

	try {
	    executeInTryCatch(out);
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
	}

    }

    /**
     * @param out
     * @throws SQLException
     * @throws IOException
     * @throws SecurityException
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     */
    private void executeInTryCatch(OutputStream out)
	    throws SQLException, IOException, SecurityException, FileNotFoundException, IllegalArgumentException {
	AceQLMetaData aceQLMetaData = new AceQLMetaData(connection);
	String action = request.getParameter(HttpParameter.ACTION);
	String username = request.getParameter(HttpParameter.USERNAME);
	String database = request.getParameter(HttpParameter.DATABASE);

	boolean allow = true;
	String sql = "<void>";
	for (SqlFirewallManager sqlFirewallManager : sqlFirewallManagers) {
	    allow = sqlFirewallManager.allowMetadataQuery(username, database, connection);
	    if (!allow) {
		String ipAddress = IpUtil.getRemoteAddr(request);
		List<Object> parameterValues = new ArrayList<>();

		SqlEvent sqlEvent = SqlEventWrapper.sqlEventBuild(username, database, ipAddress, sql,
			ServerStatementUtil.isPreparedStatement(request), parameterValues, true);

		SqlFirewallTriggerWrapper.runIfStatementRefused(sqlEvent, sqlFirewallManager, connection);
		break;
	    }
	}

	if (!allow) {
	    Map<Integer, String> parameters = new HashMap<>();

	    List<Object> values = new ArrayList<>();
	    String message = JsonSecurityMessage.prepStatementNotAllowedBuild(sql,
		    "Metadata Query API calls are not allowed!", parameters, values, true);
	    throw new SecurityException(message);
	}

	if (action.equals(HttpParameter.METADATA_QUERY_DB_SCHEMA_DOWNLOAD)) {
	    MetadataQuerySchemaDownloader metadataQuerySchemaDownloader = new MetadataQuerySchemaDownloader(request,
		    response, out, connection, aceQLMetaData);
	    metadataQuerySchemaDownloader.schemaDowload();
	} else if (action.equals(HttpParameter.METADATA_QUERY_GET_DB_METADATA)) {
	    JdbcDatabaseMetaData jdbcDatabaseMetaData = aceQLMetaData.getJdbcDatabaseMetaData();
	    JdbcDatabaseMetaDataDto jdbcDatabaseMetaDataDto = new JdbcDatabaseMetaDataDto(jdbcDatabaseMetaData);
	    String jsonString = GsonWsUtil.getJSonString(jdbcDatabaseMetaDataDto);
	    response.setContentType("text/plain");
	    ServerSqlManager.writeLine(out, jsonString);
	} else if (action.equals(HttpParameter.METADATA_QUERY_GET_TABLE_DETAILS)) {
	    String tableName = request.getParameter(HttpParameter.TABLE_NAME);
	    response.setContentType("text/plain");

	    Set<String> tables = new HashSet<>();
	    List<String> theTableNames = aceQLMetaData.getTableNames();
	    for (String theTableName : theTableNames) {
		tables.add(theTableName.toLowerCase());
	    }

	    if (tableName != null && tables.contains(tableName.toLowerCase())) {
		Table table = aceQLMetaData.getTable(tableName);
		TableDto tableDto = new TableDto(table);
		String jsonString = GsonWsUtil.getJSonString(tableDto);
		ServerSqlManager.writeLine(out, jsonString);
	    } else {
		JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
			JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.INVALID_TABLE_NAME);
		ServerSqlManager.writeLine(out, errorReturn.build());
		return;
	    }
	} else if (action.equals(HttpParameter.METADATA_QUERY_GET_TABLE_NAMES)) {

	    String tableType = request.getParameter(HttpParameter.TABLE_TYPE);
	    List<String> tableNames = new ArrayList<>();
	    if (tableType == null || tableType.isEmpty()) {
		tableNames = aceQLMetaData.getTableNames();
	    } else {
		tableNames = aceQLMetaData.getTableNames(tableType);
	    }

	    TableNamesDto tableNamesDto = new TableNamesDto(tableNames);
	    String jsonString = GsonWsUtil.getJSonString(tableNamesDto);
	    response.setContentType("text/plain");
	    ServerSqlManager.writeLine(out, jsonString);
	} else {
	    throw new IllegalArgumentException("Unknown metadata_query action: " + action);
	}
    }

}
