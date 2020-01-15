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
