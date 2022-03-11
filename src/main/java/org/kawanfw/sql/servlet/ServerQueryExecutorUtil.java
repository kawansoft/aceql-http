/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP                                     
 * Copyright (C) 2021,  KawanSoft SAS
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
