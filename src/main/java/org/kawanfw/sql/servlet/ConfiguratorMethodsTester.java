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

import java.util.Date;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * Tests that all configurators methods are correct. set properties if not, with
 * Exception & associated message.
 * 
 * @author Nicolas de Pomereu
 * 
 */
public class ConfiguratorMethodsTester {

    private static boolean DEBUG = FrameworkDebug
	    .isSet(ConfiguratorMethodsTester.class);

    private DatabaseConfigurator databaseConfigurator = null;

    /** The Exception */
    private Exception exception = null;

    /** The init error message trapped */
    private String initErrrorMesage = null;

    public ConfiguratorMethodsTester(
	    DatabaseConfigurator databaseConfigurator) {
	super();
	this.databaseConfigurator = databaseConfigurator;
    }

    /**
     * 
     * @return the Exception thrown
     */
    public Exception getException() {
	return exception;
    }

    /**
     * 
     * @return the the label of the exception thrown
     */
    public String getInitErrrorMesage() {
	return initErrrorMesage;
    }

    /**
     * Test the configurators main methods to see if they throw Exceptions
     */

    public void testMethods() {
	// Fist thing to do: Stores in static memory the user settings for this
	// transaction
	// This method will also test the Connection is created, otw a
	// detailed Exception is thrown

	debug("initErrrorMesage: " + initErrrorMesage);

	debug("Before commonsConfigurator.login.");

	if (exception == null) {
	    // Test that the login method does not throw an Exception
	    @SuppressWarnings("unused")
	    boolean isOk = false;

	    try {

		debug("In commonsConfigurator.login.");

		isOk = databaseConfigurator.login("dummy",
			"dummy".toCharArray(), "dummy", "127.0.0.1");

		debug("After new commonsConfigurator.login.");

	    } catch (Exception e) {
		debug("Exception thrown: " + e.toString());
		initErrrorMesage = e.getMessage();
		exception = e;
	    }
	}

    }

    /**
     * debug
     */
    public static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
