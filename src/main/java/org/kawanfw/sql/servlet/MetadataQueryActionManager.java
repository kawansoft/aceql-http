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
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.metadata.AceQLMetaData;
import org.kawanfw.sql.metadata.JdbcDatabaseMetaData;
import org.kawanfw.sql.metadata.Table;
import org.kawanfw.sql.metadata.dto.JdbcDatabaseMetaDataDto;
import org.kawanfw.sql.metadata.dto.TableDto;
import org.kawanfw.sql.metadata.dto.TableNamesDto;
import org.kawanfw.sql.metadata.util.GsonWsUtil;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonSecurityMessage;

/**
 * Execute the metadata query asked by user.
 *
 * @author Nicolas de Pomereu
 */
public class MetadataQueryActionManager {

    private HttpServletRequest request = null;
    private HttpServletResponse response = null;
    private Connection connection = null;
    private AceQLMetaData aceQLMetaData = null;
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
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_FORBIDDEN,
		    JsonErrorReturn.ERROR_ACEQL_UNAUTHORIZED, e.getMessage());
	    ServerSqlManager.writeLine(out, errorReturn.build());
	} catch (SQLException e) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_JDBC_ERROR, e.getMessage());
	    ServerSqlManager.writeLine(out, errorReturn.build());
	} catch (Exception e) {
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
	aceQLMetaData = new AceQLMetaData(connection);
	String action = request.getParameter(HttpParameter.ACTION);
	String username = request.getParameter(HttpParameter.USERNAME);
	String database = request.getParameter(HttpParameter.DATABASE);

	boolean allow = false;
	String sql = "<void>";
	for (SqlFirewallManager sqlFirewallManager : sqlFirewallManagers) {
	    allow = sqlFirewallManager.allowMetadataQuery(username, database, connection);
	    if (!allow) {
		String ipAddress = request.getRemoteAddr();
		List<Object> parameterValues = new ArrayList<>();

		sqlFirewallManager.runIfStatementRefused(username, database, connection, ipAddress, true, sql, parameterValues);
		break;
	    }
	}

	if (!allow) {
	    Map<Integer, String> parameters = new HashMap<>();
	    String prettyPrinting = request.getParameter(HttpParameter.PRETTY_PRINTING);

	    System.err.println("prettyPrinting: " + prettyPrinting);

	    List<Object> values = new ArrayList<>();
	    String message = JsonSecurityMessage.prepStatementNotAllowedBuild(sql,
		    "Metadata Query API calls are not allowed!", parameters, values,
		    Boolean.parseBoolean(prettyPrinting));
	    throw new SecurityException(message);
	}

	if (action.equals(HttpParameter.METADATA_QUERY_DB_SCHEMA_DOWNLOAD)) {
	    MetadataQuerySchemaDownloader metadataQuerySchemaDownloader = new MetadataQuerySchemaDownloader(request,
		    response, connection, aceQLMetaData);
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
