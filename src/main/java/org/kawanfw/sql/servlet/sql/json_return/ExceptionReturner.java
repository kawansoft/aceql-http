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
package org.kawanfw.sql.servlet.sql.json_return;

import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.servlet.sql.LoggerUtil;

public class ExceptionReturner {

    protected ExceptionReturner() {

    }

    /**
     * Clean return of Exception in JSon format & log Exception.
     * 
     * @param request
     * @param response
     * @param out
     * @param exception
     */
    public static void logAndReturnException(HttpServletRequest request,
	    HttpServletResponse response, PrintWriter out,
	    Exception exception) {

	try {
	    JsonErrorReturn jsonErrorReturn = new JsonErrorReturn(response,
		    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, exception.getMessage(),
		    ExceptionUtils.getStackTrace(exception));

	    out.println(jsonErrorReturn.build());
	    LoggerUtil.log(request, exception);
	} catch (Exception e) {
	    // Should never happen
	    e.printStackTrace();
	}

    }

    /**
     * Clean return of Exception in JSon format & log Exception.
     * 
     * @param request
     * @param response
     * @param out
     * @param exception
     */
    public static void logAndReturnException(HttpServletRequest request,
	    HttpServletResponse response, OutputStream out,
	    Throwable exception) {
	try {

	    JsonErrorReturn jsonErrorReturn = new JsonErrorReturn(response,
		    HttpServletResponse.SC_OK,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, exception.getMessage(),
		    ExceptionUtils.getStackTrace(exception));

	    ServerSqlManager.writeLine(out, jsonErrorReturn.build());
	    LoggerUtil.log(request, exception);

	} catch (Exception e) {
	    // Should never happen
	    e.printStackTrace();
	}
    }

}
