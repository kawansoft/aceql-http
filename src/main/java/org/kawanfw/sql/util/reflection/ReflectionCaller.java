/*
 * Copyright (c)2023 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-02-21
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
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
		DatabaseConfigurator.class.getName(),
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
		DatabaseConfigurator.class.getName(),
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
