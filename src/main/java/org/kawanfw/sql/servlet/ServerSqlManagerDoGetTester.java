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
package org.kawanfw.sql.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Date;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.api.server.DefaultDatabaseConfigurator;
import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesStore;
import org.kawanfw.sql.tomcat.TomcatSqlModeStore;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.version.VersionWrapper;

/**
 * @author Nicolas de Pomereu Test ServerSqlManager doGet method
 */
public class ServerSqlManagerDoGetTester {

    private static boolean DEBUG = FrameworkDebug.isSet(ServerSqlManagerDoGetTester.class);

    /** Color used by servlet display in all KawanSoft Frameworks */
    public static final String KAWANSOFT_COLOR = "E7403E";

    public static String CR_LF = System.getProperty("line.separator");
    public static final String DATABASE_CONFIGURATOR_CLASS_NAME = "databaseConfiguratorClassName";

    /** The init error message trapped */
    private String initErrrorMesage = "";

    public void doGetTest(HttpServletResponse response, String servletCallName, Exception exception

    ) throws IOException {

	response.setContentType("text/html");
	PrintWriter out = response.getWriter();

	debug("doGetTest begin");

	try {
	    // ok for servlet display
	    String status = "</font><font face=\"Arial\" color=\"green\">" + "OK & Running.";

	    // Test configurators, only if not already thrown Exception:

	    if (exception != null) {

		debug("exception != null");

		if (TomcatSqlModeStore.isTomcatEmbedded()) {
		    // status = initErrrorMesage + CR_LF + stackTrace;
		    status = exception.toString();
		    out.println(status);
		    debug("after out.println(status)");
		    return;

		} else {

		    String exceptionDisplay = null;

		    if (exception instanceof DatabaseConfigurationException) {
			exceptionDisplay = exception.getMessage();
		    } else {
			exceptionDisplay = ExceptionUtils.getStackTrace(exception);
		    }

		    BufferedReader bufferedReader = new BufferedReader(new StringReader(exceptionDisplay));
		    StringBuffer sb = new StringBuffer();

		    String line = null;
		    while ((line = bufferedReader.readLine()) != null) {
			// All subsequent lines contain the result
			sb.append(line);
			sb.append("<br>");
		    }

		    status = "</font><font face=\"Arial\" color=\"red\">" + initErrrorMesage + "<br>" + sb.toString();
		}
	    }

	    printResult(servletCallName, out, status);
	} catch (Exception e) {
	    e.printStackTrace(out);
	}

    }

    /**
     * @param servletCallName
     * @param out
     * @param status
     */
    private void printResult(String servletCallName, PrintWriter out, String status) {
	Set<String> databases = ConfPropertiesStore.get().getDatabaseNames();

	out.println("<!--OK-->");
	out.println("<br>");
	out.println("<font face=\"Arial\">");
	out.println("<b>");
	out.println("<font color=\"#" + KAWANSOFT_COLOR + "\">" + VersionWrapper.getServerVersion() + "</font>");
	out.println("<br>");
	out.println("<br>");
	out.println(servletCallName + " Servlet Configuration");
	out.println("</b>");

	out.println("<br><br>");
	out.println("<table cellpadding=\"3\" border=\"1\">");

	out.println("<tr>");
	out.println("<td align=\"center\"> <b>Database Name</b> </td>");
	out.println("<td align=\"center\"> <b>Configurator Parameter</b> </td>");
	out.println("<td align=\"center\"> <b>Configurator Value</b> </td>");
	out.println("</tr>");

	printDatabaseConfigurators(out, databases);

	out.println("</table>");
	out.println("<br><br>");
	out.println("<table cellpadding=\"3\" border=\"1\">");
	out.println("<tr>");
	out.println("<td align=\"center\"> <b>SQL Configuration Status</b> </td>");
	out.println("</tr>");
	out.println("<tr>");
	out.println("<td> " + status + "</td>");
	out.println("</tr>");
	out.println("</table>");
	out.println("</font>");
    }

    /**
     * @param out
     * @param databases
     */
    private void printDatabaseConfigurators(PrintWriter out, Set<String> databases) {
	for (String database : databases) {
	    String databaseConfiguratorClassName = ConfPropertiesStore.get().getDatabaseConfiguratorClassName(database);

	    if (databaseConfiguratorClassName == null || databaseConfiguratorClassName.isEmpty()) {
		databaseConfiguratorClassName = DefaultDatabaseConfigurator.class.getName();
	    }

	    out.println("<tr>");
	    out.println("<td> " + database + "</td>");
	    out.println("<td> " + DATABASE_CONFIGURATOR_CLASS_NAME + "</td>");
	    out.println("<td> " + databaseConfiguratorClassName + "</td>");
	    out.println("</tr>");
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
