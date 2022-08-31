/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.jdbc.metadata;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.jdbc.metadata.DatabaseMetaDataMethodCallDTO;
import org.kawanfw.sql.metadata.util.GsonWsUtil;
import org.kawanfw.sql.servlet.ActionUtil;
import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.ServerSqlDispatchUtil;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.servlet.connection.RollbackUtil;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;

/**
 * @author Nicolas de Pomereu
 *
 */
public class DefaultJdbcDatabaseMetadataActionManagerWrap {

    /**
     * @param request
     * @param response
     * @param out
     * @param sqlFirewallManagers
     * @param connection
     * @throws SQLException
     * @throws IOException
     */
    public static void executeWrap(HttpServletRequest request, HttpServletResponse response, OutputStream out,
            List<SqlFirewallManager> sqlFirewallManagers, Connection connection) throws SQLException, IOException {
        
        try {
            // Throws SecurityException if not authorized
            ServerSqlDispatchUtil.checkMetadataAuthorized(request, connection, sqlFirewallManagers);
    
            DefaultJdbcDatabaseMetadataActionManagerWrap.executeInTryCatch(request, out, connection);
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
     * Executes within a try/catch.
     *
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
    private static void executeInTryCatch(HttpServletRequest request, OutputStream out, Connection connection)
            throws SQLException, IOException, SecurityException, FileNotFoundException, IllegalArgumentException,
            ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        String action = request.getParameter(HttpParameter.ACTION);
    
        // Double check
        if (!ActionUtil.isJdbcDatabaseMetaDataQuery(action)) {
            throw new IllegalArgumentException("Unknown JDBC DatabaseMetaData query action: " + action);
        }
    
        // Get the DTO
        // jsonDatabaseMetaDataMethodCallDTO
        // String database_meta_data_method_call_dto
        String jsonString = request.getParameter(HttpParameter.JSON_DATABASE_META_DATA_METHOD_CALLL_DTO);
        DatabaseMetaDataMethodCallDTO databaseMetaDataMethodCallDTO = GsonWsUtil.fromJson(jsonString,
        	DatabaseMetaDataMethodCallDTO.class);
    
        JdbcDatabaseMetaDataExecutor jdbcDatabaseMetaDataExecutor = new JdbcDatabaseMetaDataExecutor(request,
        	databaseMetaDataMethodCallDTO, out, connection);
        jdbcDatabaseMetaDataExecutor.callDatabaseMetaDataMethod();
    
    }


}
