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
package org.kawanfw.sql.servlet.jdbc.metadata;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.jdbc.metadata.DatabaseMetaDataMethodCallDTO;
import org.kawanfw.sql.metadata.util.GsonWsUtil;
import org.kawanfw.sql.servlet.ActionUtil;
import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonSecurityMessage;

/**
 * Execute the DatabaseMetaData method call asked by client side.
 *
 * @author Nicolas de Pomereu
 */
public class JdbcDatabaseMetadataActionManager {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private OutputStream out;
    List<SqlFirewallManager> sqlFirewallManagers = new ArrayList<>();
    private Connection connection;

    public JdbcDatabaseMetadataActionManager(HttpServletRequest request, HttpServletResponse response, OutputStream out,
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
     * Executes within a try/catch.
     *
     * @param out
     * @throws SQLException
     * @throws IOException
     * @throws SecurityException
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     */
    private void executeInTryCatch(OutputStream out) throws SQLException, IOException, SecurityException,
	    FileNotFoundException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException,
	    InstantiationException, IllegalAccessException, InvocationTargetException {
	String action = request.getParameter(HttpParameter.ACTION);
	String username = request.getParameter(HttpParameter.USERNAME);
	String database = request.getParameter(HttpParameter.DATABASE);

	// Double check
	if (!ActionUtil.isJdbcDatabaseMetaDataQuery(action)) {
	    throw new IllegalArgumentException("Unknown JDBC DatabaseMetaData query action: " + action);
	}

	boolean allow = false;
	String sql = "<void>";
	for (SqlFirewallManager sqlFirewallManager : sqlFirewallManagers) {
	    allow = sqlFirewallManager.allowMetadataQuery(username, database, connection);
	    if (!allow) {
		String ipAddress = request.getRemoteAddr();
		List<Object> parameterValues = new ArrayList<>();

		sqlFirewallManager.runIfStatementRefused(username, database, connection, ipAddress, true, sql,
			parameterValues);
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

	// Get the DTO
	// jsonDatabaseMetaDataMethodCallDTO
	// String database_meta_data_method_call_dto
	String jsonString = request.getParameter(HttpParameter.JSON_DATABASE_META_DATA_METHOD_CALLL_DTO);
	DatabaseMetaDataMethodCallDTO databaseMetaDataMethodCallDTO = GsonWsUtil.fromJson(jsonString,
		DatabaseMetaDataMethodCallDTO.class);

	JdbcDatabaseMetaDataExecutor jdbcDatabaseMetaDataExecutor = new JdbcDatabaseMetaDataExecutor(request, databaseMetaDataMethodCallDTO, out, connection);
	jdbcDatabaseMetaDataExecutor.callDatabaseMetaDataMethod();

    }


}
