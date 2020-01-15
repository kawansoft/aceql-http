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
package org.kawanfw.sql.servlet.sql;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.ServerSqlManager;

/**
 * Logs all Exceptions thrown on server side, even user and application
 * Exceptions (SQLException), for ease of debug if any problem.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class LoggerUtil {

    /**
     * 
     */
    protected LoggerUtil() {

    }

    /**
     * Logs the SQL Exception with out internal AceQL errorMessage that details
     * the reason of the SQLException to ease debug.
     * 
     * @param request
     * @param sqlException
     * @param aceQLErrorMessage
     * @throws IOException
     */
    public static void log(HttpServletRequest request,
	    SQLException sqlException, String aceQLErrorMessage)
	    throws IOException {

	String database = request.getParameter(HttpParameter.DATABASE);

	DatabaseConfigurator databaseConfigurator = ServerSqlManager
		.getDatabaseConfigurator(database);

	Logger logger = databaseConfigurator.getLogger();
	logger.log(Level.WARNING, aceQLErrorMessage);
	logger.log(Level.WARNING, sqlException.toString());

    }

    /**
     * Logs the thrown Exception.
     * 
     * @param request
     * @param exception
     * @throws IOException
     */
    public static void log(HttpServletRequest request, Throwable exception)
	    throws IOException {
	String database = request.getParameter(HttpParameter.DATABASE);

	DatabaseConfigurator databaseConfigurator = ServerSqlManager
		.getDatabaseConfigurator(database);

	Logger logger = databaseConfigurator.getLogger();
	if (logger != null) {
	    logger.log(Level.WARNING, "Exception: " + exception);
	}
	else {
	    System.err.println("Logger is null!");
	    System.err.println("Exception: " + exception);
	}

    }

}
