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
import org.kawanfw.sql.tomcat.ServletParametersStore;
import org.kawanfw.sql.tomcat.TomcatSqlModeStore;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.version.Version;

/**
 * @author Nicolas de Pomereu Test ServerSqlManager doGet method
 */
public class ServerSqlManagerDoGetTester {

    private static boolean DEBUG = FrameworkDebug.isSet(ServerSqlManager.class);

    /** Color used by servlet display in all KawanSoft Frameworks */
    public static final String KAWANSOFT_COLOR = "E7403E";

    public static String CR_LF = System.getProperty("line.separator");
    public static final String DATABASE_CONFIGURATOR_CLASS_NAME = "databaseConfiguratorClassName";

    /** The init error message trapped */
    private String initErrrorMesage = "";

    public void doGetTest(HttpServletResponse response, String servletName,
	    Exception exception

    ) throws IOException {

	response.setContentType("text/html");
	PrintWriter out = response.getWriter();

	debug("doGetTest begin");

	try {
	    // ok for servlet display
	    String status = "</font><font face=\"Arial\" color=\"green\">"
		    + "OK & Running.";

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
			exceptionDisplay = ExceptionUtils
				.getStackTrace(exception);
		    }

		    BufferedReader bufferedReader = new BufferedReader(
			    new StringReader(exceptionDisplay));
		    StringBuffer sb = new StringBuffer();

		    String line = null;
		    while ((line = bufferedReader.readLine()) != null) {
			// All subsequent lines contain the result
			sb.append(line);
			sb.append("<br>");
		    }

		    status = "</font><font face=\"Arial\" color=\"red\">"
			    + initErrrorMesage + "<br>" + sb.toString();
		}

	    }

	    // ok for tomcat embededed display
	    /*
	     * if (TomcatSqlModeStore.isTomcatEmbedded()) { // Tomcat is
	     * embedded & running OK status = "OK"; out.println(status); return;
	     * }
	     */

	    Set<String> databases = ServletParametersStore.getDatabaseNames();

	    out.println("<!--OK-->");
	    out.println("<br>");
	    out.println("<font face=\"Arial\">");
	    out.println("<b>");
	    out.println("<font color=\"#" + KAWANSOFT_COLOR + "\">"
		    + Version.getServerVersion() + "</font>");
	    out.println("<br>");
	    out.println("<br>");
	    out.println(servletName + " Servlet Configuration");
	    out.println("</b>");

	    out.println("<br><br>");
	    out.println("<table cellpadding=\"3\" border=\"1\">");

	    out.println("<tr>");
	    out.println("<td align=\"center\"> <b>Database Name</b> </td>");
	    out.println(
		    "<td align=\"center\"> <b>Configurator Parameter</b> </td>");
	    out.println(
		    "<td align=\"center\"> <b>Configurator Value</b> </td>");
	    out.println("</tr>");

	    for (String database : databases) {
		String databaseConfiguratorClassName = ServletParametersStore
			.getInitParameter(database,
				DATABASE_CONFIGURATOR_CLASS_NAME);

		if (databaseConfiguratorClassName == null
			|| databaseConfiguratorClassName.isEmpty()) {
		    databaseConfiguratorClassName = DefaultDatabaseConfigurator.class
			    .getName();
		}

		out.println("<tr>");
		out.println("<td> " + database + "</td>");
		out.println(
			"<td> " + DATABASE_CONFIGURATOR_CLASS_NAME + "</td>");
		out.println("<td> " + databaseConfiguratorClassName + "</td>");
		out.println("</tr>");
	    }

	    out.println("</table>");

	    out.println("<br><br>");
	    out.println("<table cellpadding=\"3\" border=\"1\">");
	    out.println("<tr>");
	    out.println(
		    "<td align=\"center\"> <b>SQL Configuration Status</b> </td>");
	    out.println("</tr>");
	    out.println("<tr>");
	    out.println("<td> " + status + "</td>");
	    out.println("</tr>");
	    out.println("</table>");
	    out.println("</font>");
	} catch (Exception e) {
	    e.printStackTrace(out);
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
