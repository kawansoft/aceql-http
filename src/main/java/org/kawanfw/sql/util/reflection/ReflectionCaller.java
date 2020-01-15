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
package org.kawanfw.sql.util.reflection;

import java.sql.SQLException;

import org.kawanfw.sql.api.server.DatabaseConfigurator;

/**
 * @author Nicolas de Pomereu
 *
 *         A class that allows to call DatabaseConfigurator methods per
 *         reflection using Invoker.
 */
public class ReflectionCaller {

    /**
     * Protected constructor
     */
    protected ReflectionCaller() {

    }

    /**
     * Return the result of getDelayBeforeNextLogin method of
     * DatabaseConfigurator
     * 
     * @param databaseConfigurator
     *            the DatabaseConfigurator instance
     * @return the maximum of attempts before login delay
     * 
     * @throws SQLException
     *             if any Exception occurs, it is wrapped into an SQLException
     */
    public static int getMaxLoginAttemptsBeforeDelay(
	    DatabaseConfigurator databaseConfigurator) throws SQLException {
	String methodName = new Object() {
	}.getClass().getEnclosingMethod().getName();

	if (!Invoker.existsMethod(
		org.kawanfw.sql.api.server.DatabaseConfigurator.class.getName(),
		methodName)) {
	    return 0;
	}

	Object result = null;

	try {
	    result = Invoker.getMethodResult(databaseConfigurator, methodName);
	} catch (Exception e) {
	    throw new SQLException(e);
	}

	if (result == null) {
	    return 0;
	} else {
	    return (Integer) result;
	}
    }

    /**
     * Return the result of getDelayBeforeNextLogin method of
     * DatabaseConfigurator
     * 
     * @param databaseConfigurator
     *            the DatabaseConfigurator instance
     * @return the delay in seconds before the next authorized login
     * 
     * @throws SQLException
     *             if any Exception occurs, it is wrapped into an SQLException
     */
    public static int getDelayBeforeNextLogin(
	    DatabaseConfigurator databaseConfigurator) throws SQLException {
	String methodName = new Object() {
	}.getClass().getEnclosingMethod().getName();

	if (!Invoker.existsMethod(
		org.kawanfw.sql.api.server.DatabaseConfigurator.class.getName(),
		methodName)) {
	    return 0;
	}

	Object result = null;

	try {
	    result = Invoker.getMethodResult(databaseConfigurator, methodName);
	} catch (Exception e) {
	    throw new SQLException(e);
	}

	if (result == null) {
	    return 0;
	} else {
	    return (Integer) result;
	}
    }

}
