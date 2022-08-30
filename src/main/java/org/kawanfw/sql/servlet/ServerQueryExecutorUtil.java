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

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.kawanfw.sql.servlet.sql.callable.aceqlproc.ServerQueryExecutorWrapper;
import org.kawanfw.sql.servlet.sql.callable.aceqlproc.ServerQueryExecutorWrapperCreator;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ServerQueryExecutorUtil {

    private static boolean DEBUG = FrameworkDebug.isSet(ServerQueryExecutorUtil.class);
    
    /**
     * Static class.
     */
    protected ServerQueryExecutorUtil() {

    }

    public static boolean isExecuteServerQuery(HttpServletRequest request, OutputStream out, String action,
	    Connection connection) throws SQLException, IOException {

	if (action.equals(HttpParameter.EXECUTE_SERVER_QUERY)) {
	    
	    try {
		ServerQueryExecutorWrapper serverQueryExecutorWrapper = ServerQueryExecutorWrapperCreator
			.createInstance();
		serverQueryExecutorWrapper.executeQuery(request, out, action, connection);
	    } catch (SQLException exception) {
		throw exception;
	    }
	    catch (Exception exception) {
		throw new SQLException(exception);
	    }

	    return true;
	} else {
	    return false;
	}
    }

  
    public static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
