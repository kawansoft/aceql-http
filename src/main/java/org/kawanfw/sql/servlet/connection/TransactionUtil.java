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
package org.kawanfw.sql.servlet.connection;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonOkReturn;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * @author Nicolas de Pomereu
 *
 *         Wrapper for transactions commands to decrease code in
 *         ServerSqlDispatch
 */
public class TransactionUtil {

    private static boolean DEBUG = FrameworkDebug.isSet(TransactionUtil.class);

    /**
     * Calls a setAutocommit, setReadOnly(), setHoldbility,
     * getTransactionIsolation
     * 
     * @param request
     * @param response
     *            TODO
     * @param out
     * @param action
     * @param connection
     * @throws IOException
     * @throws SQLException
     * @throws IllegalArgumentException
     */
    public static void setConnectionModifierAction(HttpServletRequest request,
	    HttpServletResponse response, OutputStream out, String action,
	    Connection connection)
	    throws IOException, SQLException, IllegalArgumentException {

	try {

	    if (action.equals(HttpParameter.COMMIT)) {
		connection.commit();

	    } else if (action.equals(HttpParameter.ROLLBACK)) {
		connection.rollback();
	    } else if (action.equals(HttpParameter.SET_AUTO_COMMIT)) {
		boolean autoCommit = Boolean.parseBoolean(
			request.getParameter(HttpParameter.ACTION_VALUE));
		connection.setAutoCommit(autoCommit);
	    } else if (action.equals(HttpParameter.SET_READ_ONLY)) {
		boolean readOnly = Boolean.parseBoolean(
			request.getParameter(HttpParameter.ACTION_VALUE));
		connection.setReadOnly(readOnly);
	    } else if (action.equals(HttpParameter.SET_HOLDABILITY)) {
		int holdability = getHoldability(
			request.getParameter(HttpParameter.ACTION_VALUE));
		connection.setHoldability(holdability);
	    } else if (action
		    .equals(HttpParameter.SET_TRANSACTION_ISOLATION_LEVEL)) {
		int level = getTransactionIsolation(
			request.getParameter(HttpParameter.ACTION_VALUE));
		connection.setTransactionIsolation(level);
	    } else {
		throw new IllegalArgumentException(
			"Invalid Sql Action: " + action);
	    }

	    ServerSqlManager.writeLine(out, JsonOkReturn.build());

	} catch (IllegalArgumentException e) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response,
		    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, e.getMessage());
	    ServerSqlManager.writeLine(out, errorReturn.build());
	} catch (SQLException e) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response,
		    HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_JDBC_ERROR, e.getMessage());
	    ServerSqlManager.writeLine(out, errorReturn.build());
	}

    }

    /**
     * Calls a getAutocommit, isReadOnly(), getHoldbility,
     * SetTransactionIsolation
     * 
     * @param request
     * @param response
     * @param out
     * @param action
     * @param connection
     * @throws IOException
     * @throws SQLException
     * @throws IllegalArgumentException
     */

    public static void getConnectionionInfosExecute(HttpServletRequest request,
	    HttpServletResponse response, OutputStream out, String action,
	    Connection connection)
	    throws IOException, SQLException, IllegalArgumentException {

	try {
	    if (action.equals(HttpParameter.GET_AUTO_COMMIT)) {
		boolean autoCommit = connection.getAutoCommit();
		ServerSqlManager.writeLine(out,
			JsonOkReturn.build("result", "" + autoCommit));
	    } else if (action.equals(HttpParameter.IS_READ_ONLY)) {
		boolean readOnly = connection.isReadOnly();
		ServerSqlManager.writeLine(out,
			JsonOkReturn.build("result", "" + readOnly));
	    } else if (action.equals(HttpParameter.GET_CATALOG)) {
		String catalog = connection.getCatalog();
		ServerSqlManager.writeLine(out,
			JsonOkReturn.build("result", catalog));

	    } else if (action.equals(HttpParameter.GET_HOLDABILITY)) {
		int holdability = connection.getHoldability();
		String strHoldability = getHoldabilityAsString(holdability);
		ServerSqlManager.writeLine(out,
			JsonOkReturn.build("result", "" + strHoldability));
	    } else if (action
		    .equals(HttpParameter.GET_TRANSACTION_ISOLATION_LEVEL)) {
		int transactionIsolation = connection.getTransactionIsolation();
		String strTransactionIsolation = getTransactionIsolationAsString(
			transactionIsolation);
		ServerSqlManager.writeLine(out, JsonOkReturn.build("result",
			"" + strTransactionIsolation));
	    } else {
		throw new IllegalArgumentException(
			"Invalid Sql Action: " + action);
	    }
	} catch (IllegalArgumentException e) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response,
		    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, e.getMessage());
	    ServerSqlManager.writeLine(out, errorReturn.build());
	} catch (SQLException e) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response,
		    HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_JDBC_ERROR, e.getMessage());
	    ServerSqlManager.writeLine(out, errorReturn.build());
	}
    }

    private static String getTransactionIsolationAsString(
	    int transactionIsolationLevel) {

	if (transactionIsolationLevel == Connection.TRANSACTION_NONE) {
	    return HttpParameter.NONE;
	} else if (transactionIsolationLevel == Connection.TRANSACTION_READ_UNCOMMITTED) {
	    return HttpParameter.READ_UNCOMMITTED;
	} else if (transactionIsolationLevel == Connection.TRANSACTION_READ_COMMITTED) {
	    return HttpParameter.READ_COMMITTED;
	} else if (transactionIsolationLevel == Connection.TRANSACTION_REPEATABLE_READ) {
	    return HttpParameter.REPEATABLE_READ;
	} else if (transactionIsolationLevel == Connection.TRANSACTION_SERIALIZABLE) {
	    return HttpParameter.SERIALIZABLE;
	} else {
	    return "UNKNOWN";
	}
    }

    private static int getTransactionIsolation(String actionValue) {

	if (actionValue.equals(HttpParameter.READ_UNCOMMITTED)) {
	    return Connection.TRANSACTION_READ_UNCOMMITTED;
	} else if (actionValue.equals(HttpParameter.READ_COMMITTED)) {
	    return Connection.TRANSACTION_READ_COMMITTED;
	} else if (actionValue.equals(HttpParameter.REPEATABLE_READ)) {
	    return Connection.TRANSACTION_REPEATABLE_READ;
	} else if (actionValue.equals(HttpParameter.SERIALIZABLE)) {
	    return Connection.TRANSACTION_SERIALIZABLE;
	} else {
	    throw new IllegalArgumentException(
		    "Unsupported Transaction Isolation Level: " + actionValue);
	}
    }

    private static int getHoldability(String actionValue) {

	if (actionValue.equals(HttpParameter.HOLD_CURSORS_OVER_COMMIT)) {
	    return ResultSet.HOLD_CURSORS_OVER_COMMIT;
	} else if (actionValue.equals(HttpParameter.CLOSE_CURSORS_AT_COMMIT)) {
	    return ResultSet.CLOSE_CURSORS_AT_COMMIT;
	} else {
	    throw new IllegalArgumentException(
		    "Unsupported Holdability: " + actionValue);
	}
    }

    private static String getHoldabilityAsString(int holdability) {

	if (holdability == ResultSet.HOLD_CURSORS_OVER_COMMIT) {
	    return HttpParameter.HOLD_CURSORS_OVER_COMMIT;
	} else if (holdability == ResultSet.CLOSE_CURSORS_AT_COMMIT) {
	    return HttpParameter.CLOSE_CURSORS_AT_COMMIT;
	} else {
	    throw new IllegalArgumentException(
		    "Unsupported Holdability: " + holdability);
	}
    }

    /**
     * Method called by children Servlet for debug purpose println is done only
     * if class name name is in kawansoft-debug.ini
     */
    public static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
