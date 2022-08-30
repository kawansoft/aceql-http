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

import java.util.Date;

import org.kawanfw.sql.api.server.auth.UserAuthenticator;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * Tests that all configurators methods are correct. set properties if not, with
 * Exception & associated message.
 *
 * @author Nicolas de Pomereu
 *
 */
public class UserAuthenticatorTester {

    private static boolean DEBUG = FrameworkDebug
	    .isSet(UserAuthenticatorTester.class);

    private UserAuthenticator userAuthenticator = null;

    /** The Exception */
    private Exception exception = null;

    /** The init error message trapped */
    private String initErrrorMesage = null;

    public UserAuthenticatorTester(
	    UserAuthenticator userAuthenticator) {
	super();
	this.userAuthenticator = userAuthenticator;
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

		isOk = userAuthenticator.login("dummy",
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
