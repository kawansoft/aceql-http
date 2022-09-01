/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.sql;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.DefaultDatabaseConfigurator;
import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.injection.classes.InjectedClassesStore;

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
     * @param exception
     * @param aceQLErrorMessage
     * @throws IOException
     */
    public static void log(HttpServletRequest request,
	    Exception exception, String aceQLErrorMessage)
	    throws IOException {

	String database = request.getParameter(HttpParameter.DATABASE);

	DatabaseConfigurator databaseConfigurator = InjectedClassesStore.get().getDatabaseConfigurators().get(database);

	Logger logger = databaseConfigurator.getLogger();
	logger.log(Level.WARNING, aceQLErrorMessage);
	logger.log(Level.WARNING, exception.toString());

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

	DatabaseConfigurator databaseConfigurator = null;
	if (database == null) {
	    databaseConfigurator = new DefaultDatabaseConfigurator();
	}
	else {
	    databaseConfigurator = InjectedClassesStore.get().getDatabaseConfigurators().get(database);

	    if (databaseConfigurator == null) {
		databaseConfigurator = new DefaultDatabaseConfigurator();
	    }
	}

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
