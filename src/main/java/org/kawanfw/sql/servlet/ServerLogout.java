/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.session.SessionConfigurator;
import org.kawanfw.sql.servlet.connection.ConnectionStore;
import org.kawanfw.sql.servlet.injection.classes.InjectedClassesStore;
import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesUtil;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonOkReturn;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ServerLogout {

    private static boolean DEBUG = FrameworkDebug.isSet(ServerLogout.class);;

    // A space
    public static final String SPACE = " ";

    private static final long TWENTY_MINUTES_IN_MILLISECONDS = 1000 * 60 * 20;

    public static void logout(HttpServletRequest request, HttpServletResponse response, OutputStream out,
	    DatabaseConfigurator databaseConfigurator) throws IOException {

	try {
	    response.setContentType("text/html");

	    String username = request.getParameter(HttpParameter.USERNAME);
	    String sessionId = request.getParameter(HttpParameter.SESSION_ID);

	    SessionConfigurator sessionConfigurator = InjectedClassesStore.get().getSessionConfigurator();
	    sessionConfigurator.remove(sessionId);

	    deleteOldBlobFiles(databaseConfigurator, username);

	    if (!ConfPropertiesUtil.isStatelessMode()) {
		Set<Connection> connections = ConnectionStore.getAllConnections(username, sessionId);

		for (Connection connection : connections) {
		    databaseConfigurator.close(connection);
		}
	    }

	    try {
		ConnectionStore.removeAll(username, sessionId);
	    } catch (Exception e) {
		System.out.println(new Date() + " Failure on ConnectionStore.removeAll: " + e.toString());
	    }

	    String jSonReturn = JsonOkReturn.build();

	    if (DEBUG) {
		System.err.println("jSonReturn: " + jSonReturn);
		System.err.println(sessionId);
	    }

	    ServerSqlManager.writeLine(out, jSonReturn);

	} catch (Exception e) {

	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
		    JsonErrorReturn.ERROR_ACEQL_FAILURE, e.getMessage(), ExceptionUtils.getStackTrace(e));
	    ServerSqlManager.writeLine(out, errorReturn.build());

	}
    }

    /**
     * Delete all files, but do throw error if problem, except development control
     * null pointer exception
     *
     * @param databaseConfigurator
     * @param username
     */
    private static void deleteOldBlobFiles(DatabaseConfigurator databaseConfigurator, String username)
	    throws IOException, SQLException {

	Objects.requireNonNull(databaseConfigurator, "databaseConfigurator cannot be null!");
	Objects.requireNonNull(username, "username cannot be null!");

	// Delete all files
	File blobDirectory = databaseConfigurator.getBlobsDirectory(username);
	if (blobDirectory == null || !blobDirectory.exists()) {
	    return;
	}

	File[] files = blobDirectory.listFiles();

	if (files == null) {
	    return;
	}

	for (File file : files) {
	    if (file.lastModified() < System.currentTimeMillis() - TWENTY_MINUTES_IN_MILLISECONDS) {
		file.delete();
	    }
	}

    }

}
