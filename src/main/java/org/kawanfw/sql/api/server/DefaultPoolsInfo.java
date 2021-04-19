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
package org.kawanfw.sql.api.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.tomcat.jdbc.pool.DataSourceProxy;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.servlet.sql.json_return.ExceptionReturner;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonUtil;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 *
 * Allows to display current JDBC pool status and info for each database.<br>
 * Includes 3 methods to modify the JDBC pools. <br>
 * <br>
 * Values are accessed using methods in
 * {@code org.apache.tomcat.jdbc.pool.DataSource} class.<br>
 * The {@code DataSource} instances are retrieved in this servlet using
 * {@link DataSourceStore#getDataSources()} static method. <br>
 * <br>
 * See Tomcat JDBC Pool <a href=
 * "https://tomcat.apache.org/tomcat-9.0-doc/api/org/apache/tomcat/jdbc/pool/DataSourceProxy.html"
 * >DataSourceProxy</a> for the meaning of the displayed values. <br>
 * <br>
 *
 * It is also possible to interact with the pool and call three
 * {@code DataSource} set methods:
 * <ul>
 * <li>{@code DataSource.setMinIdle(int)}.</li>
 * <li>{@code DataSource.setMaxIdle(int)}.</li>
 * <li>{@code DataSource.setMaxActive(int)}.</li>
 * </ul>
 * <br>
 * To call the servlet from a browser, cURL or a program: <br>
 *
 * {@code http(s)://host:port/default_pools_info?password=<password_value>} <br>
 * <br>
 * Where:<br>
 * password_value = value of "password" property stored in
 * user.home/.kawansoft/default_pools_info.properties <br>
 * <br>
 *
 * To modify the pool for a database:<br>
 * {@code http(s)://host:port/default_pools_info?password=<password_value>&database=database_name&set_method=int_value}
 * <br>
 * <br>
 * Where:
 * <ul>
 * <li>database_name=the database as defined in aceql-server.properties. If not
 * specified, set_method will be applied to all databases.</li>
 * <li>set_method = setMinIdle or setMaxIdle or setMaxActive.</li>
 * <li>int_value= the number to pass to the set_method.</li>
 * </ul>
 *
 * <br>
 * Note that You can create your own servlet if you want to develop you own
 * interaction with the JDBC pools.<br>
 * Just add you servlet name, class and url-pattern in the Servlets Section of
 * your aceql-server.properties file.<br>
 * <br>
 *
 * @author Nicolas de Pomereu
 * @since 1.0
 * @see <a href=
 *      "https://tomcat.apache.org/tomcat-9.0-doc/api/org/apache/tomcat/jdbc/pool/DataSourceProxy.html">DataSourceProxy</a>
 */
public class DefaultPoolsInfo extends HttpServlet {

    private static final long serialVersionUID = 6129302507495768396L;
    private static boolean DEBUG = FrameworkDebug.isSet(DefaultPoolsInfo.class);

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.
     * HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

	executeRequest(request, response);
    }

    /**
     * Execute the client sent SQL request. Exception are trapped, cleanly returned
     * to client side and logged on DatabaseConfigurator.getLogger() Logger.
     *
     * @param request  the http request
     * @param response the http response
     * @throws IOException if any IOException occurs
     */
    private void executeRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {

	OutputStream out = null;

	try {
	    out = response.getOutputStream();
	    executeRequestInTryCatch(request, response, out);
	} catch (Exception e) {
	    ExceptionReturner.logAndReturnException(request, response, out, e);
	}
    }

    /**
     * Execute the client request
     *
     * @param request  the http request
     * @param response the http response
     * @param out      TODO
     * @throws IOException         if any IOException occurs
     * @throws SQLException
     * @throws FileUploadException
     */
    private void executeRequestInTryCatch(HttpServletRequest request, HttpServletResponse response, OutputStream out)
	    throws IOException, SQLException, FileUploadException {

	debug("Starting...");
	request.setCharacterEncoding("UTF-8");

	// Prepare the response
	response.setContentType("text/plain; charset=UTF-8");

	String password = request.getParameter("password");

	if (password == null || password.isEmpty()) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_FORBIDDEN,
		    JsonErrorReturn.ERROR_ACEQL_UNAUTHORIZED, JsonErrorReturn.INVALID_USERNAME_OR_PASSWORD);
	    ServerSqlManager.writeLine(out, errorReturn.build());

	    return;
	}

	String storedPassword = null;

	try {
	    File file = new File(SystemUtils.USER_HOME + File.separator + ".kawansoft" + File.separator
		    + "default_pools_info.properties");
	    Properties properties = new Properties();
	    try (InputStream in = new FileInputStream(file);) {
		properties.load(in);
	    }

	    storedPassword = properties.getProperty("password");

	    // Support previous version
	    if (storedPassword == null || storedPassword.isEmpty()) {
		storedPassword = FileUtils.readFileToString(new File(SystemUtils.USER_HOME + File.separator
			+ ".kawansoft" + File.separator + "default_pools_info_password.txt"), "UTF-8");
	    }

	    debug("stored_password: " + storedPassword + ":");

	    if (storedPassword == null || !storedPassword.trim().equals(password)) {
		throw new IllegalArgumentException(JsonErrorReturn.INVALID_USERNAME_OR_PASSWORD);
	    }
	} catch (Exception e) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_FORBIDDEN,
		    JsonErrorReturn.ERROR_ACEQL_UNAUTHORIZED, JsonErrorReturn.INVALID_USERNAME_OR_PASSWORD);
	    ServerSqlManager.writeLine(out, errorReturn.build());
	    return;
	}

	String setDatabase = request.getParameter("database");

	Map<String, DataSource> dataSources = DataSourceStore.getDataSources();

	if (dataSources == null || dataSources.isEmpty()) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.NO_DATASOURCES_DEFINED);
	    ServerSqlManager.writeLine(out, errorReturn.build());

	    return;
	}

	writeOutpuMain(request, out, setDatabase, dataSources);

    }

    /**
     * @param request
     * @param setDatabase
     * @param dataSources
     * @throws NumberFormatException
     * @throws IOException
     */
    private void writeOutpuMain(HttpServletRequest request, OutputStream out, String setDatabase,
	    Map<String, DataSource> dataSources) throws NumberFormatException, IOException {
	StringWriter writer = new StringWriter();

	JsonGeneratorFactory jf = JsonUtil.getJsonGeneratorFactory(true);
	JsonGenerator gen = jf.createGenerator(writer);

	Set<String> databases = dataSources.keySet();

	gen.writeStartObject();
	gen.write("status", "OK");
	gen.write("see",
		"https://tomcat.apache.org/tomcat-9.0-doc/api/org/apache/tomcat/jdbc/pool/DataSourceProxy.html");

	gen.writeStartArray("databases");

	for (String database : databases) {
	    writeOutputElementInLoop(request, setDatabase, dataSources, gen, database);
	}

	gen.writeEnd();
	gen.writeEnd();
	gen.close();

	String outString = writer.toString();
	ServerSqlManager.writeLine(out, outString);
    }

    /**
     * @param request
     * @param setDatabase
     * @param dataSources
     * @param gen
     * @param database
     * @throws NumberFormatException
     */
    private void writeOutputElementInLoop(HttpServletRequest request, String setDatabase,
	    Map<String, DataSource> dataSources, JsonGenerator gen, String database) throws NumberFormatException {
	DataSource datasource = dataSources.get(database);
	DataSourceProxy dataSourceProxy = (org.apache.tomcat.jdbc.pool.DataSource) datasource;

	if (setDatabase == null || setDatabase.equals(database)) {
	    String doSet = request.getParameter("setMinIdle");
	    if (doSet != null && !doSet.isEmpty() && StringUtils.isNumeric(doSet) && StringUtils.isNumeric(doSet)) {
		dataSourceProxy.setMinIdle(Integer.parseInt(doSet));
	    }
	    doSet = request.getParameter("setMaxIdle");
	    if (doSet != null && !doSet.isEmpty() && StringUtils.isNumeric(doSet) && StringUtils.isNumeric(doSet)) {
		dataSourceProxy.setMaxIdle(Integer.parseInt(doSet));
	    }

	    doSet = request.getParameter("setMaxActive");
	    if (doSet != null && !doSet.isEmpty() && StringUtils.isNumeric(doSet) && StringUtils.isNumeric(doSet)) {
		dataSourceProxy.setMaxActive(Integer.parseInt(doSet));
	    }
	}

	gen.writeStartObject().write("database", database).writeEnd();

	gen.writeStartObject().write("getBorrowedCount()", dataSourceProxy.getBorrowedCount()).writeEnd();
	gen.writeStartObject().write("getMaxActive()", dataSourceProxy.getMaxActive()).writeEnd();
	gen.writeStartObject().write("getMaxIdle()", dataSourceProxy.getMaxIdle()).writeEnd();
	gen.writeStartObject().write("getMinIdle()", dataSourceProxy.getMinIdle()).writeEnd();
	gen.writeStartObject().write("getNumActive()", dataSourceProxy.getNumActive()).writeEnd();
	gen.writeStartObject().write("getNumIdle()", dataSourceProxy.getNumIdle()).writeEnd();
	gen.writeStartObject().write("getReconnectedCount()", dataSourceProxy.getReconnectedCount()).writeEnd();
	gen.writeStartObject().write("getReleasedCount()", dataSourceProxy.getReleasedCount()).writeEnd();
	gen.writeStartObject().write("getReleasedIdleCount()", dataSourceProxy.getReleasedIdleCount()).writeEnd();
	gen.writeStartObject().write("getRemoveAbandonedCount()", dataSourceProxy.getRemoveAbandonedCount()).writeEnd();
	gen.writeStartObject().write("getReturnedCount()", dataSourceProxy.getReturnedCount()).writeEnd();
	gen.writeStartObject().write("getSize()", dataSourceProxy.getSize()).writeEnd();
	gen.writeStartObject().write("getWaitCount()", dataSourceProxy.getWaitCount()).writeEnd();
    }

    /**
     * Method called by children Servlet for debug purpose Println is done only if
     * class name name is in kawansoft-debug.ini
     */
    private static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }
}
