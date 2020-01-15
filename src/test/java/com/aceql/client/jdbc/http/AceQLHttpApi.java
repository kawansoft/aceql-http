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
package com.aceql.client.jdbc.http;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;

/**
 * @author Nicolas de Pomereu
 * 
 *         AceQL Rest wrapper for AceQL http/REST apis that take care of all
 *         http calls and operations.
 * 
 *         All Exceptions are trapped with a {#link AceQLException} that allows
 *         to retrieve the detail of the Exceptions
 */
public class AceQLHttpApi {

    public static boolean DEBUG = false;

    private static boolean TRACE_ON = false;

    // private values
    private String serverUrl;
    private String username;
    private char[] password;
    private String database;

    /** Proxy to use with HttpUrlConnection */
    private Proxy proxy = null;
    /** For authenticated proxy */
    private PasswordAuthentication passwordAuthentication = null;

    private static int connectTimeout = 0;
    private static int readTimeout = 0;

    private boolean prettyPrinting = false;
    private boolean gzipResult = true;

    private String url = null;
    private int httpStatusCode = HttpURLConnection.HTTP_OK;
    private String httpStatusMessage;

    private AtomicBoolean cancelled;
    private AtomicInteger progress;

    /**
     * Sets the read timeout.
     * 
     * @param readTimeout
     *            an <code>int</code> that specifies the read timeout value, in
     *            milliseconds, to be used when an http connection is
     *            established to the remote server. See
     *            {@link URLConnection#setReadTimeout(int)}
     */
    public static void setReadTimeout(int readTimeout) {
	AceQLHttpApi.readTimeout = readTimeout;
    }

    /**
     * Sets the connect timeout.
     * 
     * @param connectTimeout
     *            Sets a specified timeout value, in milliseconds, to be used
     *            when opening a communications link to the remote server. If
     *            the timeout expires before the connection can be established,
     *            a java.net.SocketTimeoutException is raised. A timeout of zero
     *            is interpreted as an infinite timeout. See
     *            {@link URLConnection#setConnectTimeout(int)}
     */
    public static void setConnectTimeout(int connectTimeout) {
	AceQLHttpApi.connectTimeout = connectTimeout;
    }

    /**
     * Login on the AceQL server and connect to a database
     * 
     * @param serverUrl
     *            the url of the AceQL server. Example:
     *            http://localhost:9090/aceql
     * @param database
     *            the server database to connect to.
     * @param username
     *            the login
     * @param password
     *            the password
     * @param proxy
     *            the proxy to use. null if none.
     * @param passwordAuthentication
     *            the username and password holder to use for authenticated
     *            proxy. Null if no proxy or if proxy
     * @throws AceQLException
     *             if any Exception occurs
     */
    public AceQLHttpApi(String serverUrl, String database, String username,
	    char[] password, Proxy proxy,
	    PasswordAuthentication passwordAuthentication)
	    throws AceQLException {

	try {
	    if (database == null) {
		throw new NullPointerException("database is null!");
	    }
	    if (serverUrl == null) {
		throw new NullPointerException("serverUrl is null!");
	    }
	    if (username == null) {
		throw new NullPointerException("username is null!");
	    }
	    if (password == null) {
		throw new NullPointerException("password is null!");
	    }

	    this.serverUrl = serverUrl;
	    this.username = username;
	    this.password = password;
	    this.database = database;
	    this.proxy = proxy;
	    this.passwordAuthentication = passwordAuthentication;

	    setProxyCredentials();

	    String url = serverUrl + "/database/" + database + "/username/"
		    + username + "/connect" + "?password="
		    + new String(password);

	    String result = callWithGet(url);

	    trace("result: " + result);

	    ResultAnalyzer resultAnalyzer = new ResultAnalyzer(result,
		    httpStatusCode, httpStatusMessage);

	    if (!resultAnalyzer.isStatusOk()) {

		throw new AceQLException(resultAnalyzer.getErrorMessage(),
			resultAnalyzer.getErrorType(), null,
			resultAnalyzer.getStackTrace(), httpStatusCode);
	    }

	    trace("Ok. Connected! ");
	    String sessionId = resultAnalyzer.getValue("session_id");
	    trace("sessionId: " + sessionId);

	    this.url = serverUrl + "/session/" + sessionId + "/";

	} catch (Exception e) {
	    if (e instanceof AceQLException) {
		throw (AceQLException) e;
	    } else {
		throw new AceQLException(e.getMessage(), 0, e, null,
			httpStatusCode);
	    }
	}

    }

    /**
     * Login on the AceQL server and connect to a database
     * 
     * @param serverUrl
     *            the url of the AceQL server. Example:
     *            http://localhost:9090/aceql
     * @param username
     *            the login
     * @param password
     *            the password
     * @param database
     *            the server database to connect to.
     * @throws AceQLException
     *             if any Exception occurs
     */
    public AceQLHttpApi(String serverUrl, String database, String username,
	    char[] password) throws AceQLException {
	this(serverUrl, database, username, password, null, null);
    }

    public void trace() {
	if (TRACE_ON) {
	    System.out.println();
	}
    }

    public void trace(String s) {
	if (TRACE_ON) {
	    System.out.println(s);
	}
    }

    private void callApiNoResult(String commandName, String commandOption)
	    throws AceQLException {

	try {

	    if (commandName == null) {
		throw new NullPointerException("commandName is null!");
	    }

	    String result = callWithGet(commandName, commandOption);

	    ResultAnalyzer resultAnalyzer = new ResultAnalyzer(result,
		    httpStatusCode, httpStatusMessage);
	    if (!resultAnalyzer.isStatusOk()) {
		throw new AceQLException(resultAnalyzer.getErrorMessage(),
			resultAnalyzer.getErrorType(), null,
			resultAnalyzer.getStackTrace(), httpStatusCode);
	    }

	} catch (Exception e) {
	    if (e instanceof AceQLException) {
		throw (AceQLException) e;
	    } else {
		throw new AceQLException(e.getMessage(), 0, e, null,
			httpStatusCode);
	    }
	}
    }

    private String callApiWithResult(String commandName, String commandOption)
	    throws AceQLException {

	try {

	    if (commandName == null) {
		throw new NullPointerException("commandName is null!");
	    }

	    String result = callWithGet(commandName, commandOption);

	    ResultAnalyzer resultAnalyzer = new ResultAnalyzer(result,
		    httpStatusCode, httpStatusMessage);
	    if (!resultAnalyzer.isStatusOk()) {
		throw new AceQLException(resultAnalyzer.getErrorMessage(),
			resultAnalyzer.getErrorType(), null,
			resultAnalyzer.getStackTrace(), httpStatusCode);
	    }

	    return resultAnalyzer.getResult();

	} catch (Exception e) {

	    if (e instanceof AceQLException) {
		throw (AceQLException) e;
	    } else {
		throw new AceQLException(e.getMessage(), 0, e, null,
			httpStatusCode);
	    }
	}
    }

    private String callWithGet(String action, String actionParameter)
	    throws IOException {

	String urlWithaction = url + action;

	if (actionParameter != null && !actionParameter.isEmpty()) {
	    urlWithaction += "/" + actionParameter;
	}

	return callWithGet(urlWithaction);

    }

    private InputStream callWithGetReturnStream(String url)
	    throws MalformedURLException, IOException,
	    UnsupportedEncodingException {

	URL theUrl = new URL(url);
	HttpURLConnection conn = null;

	if (this.proxy == null) {
	    conn = (HttpURLConnection) theUrl.openConnection();
	} else {
	    conn = (HttpURLConnection) theUrl.openConnection(proxy);
	}

	conn.setRequestProperty("Accept-Charset", "UTF-8");
	conn.setReadTimeout(readTimeout);
	conn.setRequestMethod("GET");
	conn.setDoOutput(true);

	trace();
	trace("Executing request " + url);

	httpStatusCode = conn.getResponseCode();
	httpStatusMessage = conn.getResponseMessage();

	InputStream in = null;
	if (httpStatusCode == HttpURLConnection.HTTP_OK) {
	    in = conn.getInputStream();
	} else {
	    in = conn.getErrorStream();
	}

	return in;
    }

    private String callWithGet(String url) throws MalformedURLException,
	    IOException, ProtocolException, UnsupportedEncodingException {

	String responseBody;

	try (InputStream in = callWithGetReturnStream(url);) {
	    if (in == null)
		return null;

	    ByteArrayOutputStream out = new ByteArrayOutputStream();

	    IOUtils.copy(in, out);

	    responseBody = out.toString("UTF-8");
	    if (responseBody != null) {
		responseBody = responseBody.trim();
	    }

	    trace("----------------------------------------");
	    trace(responseBody);
	    trace("----------------------------------------");

	    return responseBody;
	}

    }

    private InputStream callWithPost(String action,
	    Map<String, String> parameters) throws MalformedURLException,
	    IOException, ProtocolException, UnsupportedEncodingException {

	String urlWithaction = url + action;

	URL theUrl = new URL(urlWithaction);

	HttpURLConnection conn = null;

	if (this.proxy == null) {
	    conn = (HttpURLConnection) theUrl.openConnection();
	} else {
	    conn = (HttpURLConnection) theUrl.openConnection(proxy);
	}

	conn.setRequestProperty("Accept-Charset", "UTF-8");
	conn.setReadTimeout(readTimeout);
	conn.setRequestMethod("POST");
	conn.setDoOutput(true);

	TimeoutConnector timeoutConnector = new TimeoutConnector(conn,
		connectTimeout);

	try (OutputStream connOut = timeoutConnector.getOutputStream();) {
	    BufferedWriter writer = new BufferedWriter(
		    new OutputStreamWriter(connOut, "UTF-8"));
	    writer.write(AceQLHttpApi.getPostDataString(parameters));

	    // writer.flush();
	    writer.close();

	    trace();
	    trace("Executing request: " + urlWithaction);

	    if (parameters.containsKey("sql")) {
		trace("sql..............: " + parameters.get("sql"));
	    }

	    trace("parameters.......: " + parameters);

	    // Analyze the error after request execution
	    httpStatusCode = conn.getResponseCode();
	    httpStatusMessage = conn.getResponseMessage();

	    InputStream in = null;
	    if (httpStatusCode == HttpURLConnection.HTTP_OK) {
		in = conn.getInputStream();
	    } else {
		in = conn.getErrorStream();
	    }

	    return in;
	}

    }

    private void setProxyCredentials() {

	if (proxy == null) {
	    return;
	}

	// Sets the credential for authentication
	if (passwordAuthentication != null) {
	    final String proxyAuthUsername = passwordAuthentication
		    .getUserName();
	    final char[] proxyPassword = passwordAuthentication.getPassword();

	    Authenticator authenticator = new Authenticator() {

		public PasswordAuthentication getPasswordAuthentication() {
		    return new PasswordAuthentication(proxyAuthUsername,
			    proxyPassword);
		}
	    };

	    if (DEBUG) {
		System.out.println("passwordAuthentication: "
			+ proxyAuthUsername + " " + new String(proxyPassword));
	    }

	    Authenticator.setDefault(authenticator);
	}

    }

    // ////////////////////////////////////////////////////
    // PUBLIC METHODS //
    // ///////////////////////////////////////////////////

    @Override
    public AceQLHttpApi clone() {
	AceQLHttpApi aceQLHttpApi;
	try {
	    aceQLHttpApi = new AceQLHttpApi(serverUrl, database, username,
		    password, proxy, passwordAuthentication);

	    aceQLHttpApi.setPrettyPrinting(prettyPrinting);
	    aceQLHttpApi.setGzipResult(gzipResult);
	} catch (SQLException e) {
	    throw new IllegalStateException(e);
	}
	return aceQLHttpApi;
    }

    /**
     * Says if trace is on
     * 
     * @return true if trace is on
     */
    public static boolean isTraceOn() {
	return TRACE_ON;
    }

    /**
     * Sets the trace on/off
     * 
     * @param TRACE_ON
     *            if true, trace will be on
     */
    public static void setTraceOn(boolean traceOn) {
	TRACE_ON = traceOn;
    }

    /**
     * Returns the cancelled value set by the progress indicator
     * 
     * @return the cancelled value set by the progress indicator
     */
    public AtomicBoolean getCancelled() {
	return cancelled;
    }

    /**
     * Sets the shareable canceled variable that will be used by the progress
     * indicator to notify this instance that the user has cancelled the current
     * blob/clob upload or download.
     * 
     * @param cancelled
     *            the shareable canceled variable that will be used by the
     *            progress indicator to notify this instance that the end user
     *            has cancelled the current blob/clob upload or download
     * 
     */
    public void setCancelled(AtomicBoolean cancelled) {
	this.cancelled = cancelled;
    }

    /**
     * Returns the sharable progress variable that will store blob/clob upload
     * or download progress between 0 and 100
     * 
     * @return the sharable progress variable that will store blob/clob upload
     *         or download progress between 0 and 100
     * 
     */
    public AtomicInteger getProgress() {
	return progress;
    }

    /**
     * Sets the sharable progress variable that will store blob/clob upload or
     * download progress between 0 and 100. Will be used by progress indicators
     * to show the progress.
     * 
     * @param progress
     *            the sharable progress variable
     */
    public void setProgress(AtomicInteger progress) {
	this.progress = progress;
    }

    /**
     * @return the prettyPrinting
     */
    public boolean isPrettyPrinting() {
	return prettyPrinting;
    }

    /**
     * Says the query result is returned compressed with the GZIP file format.
     * 
     * @return the gzipResult
     */
    public boolean isGzipResult() {
	return gzipResult;
    }

    /**
     * Says if JSON contents are to be pretty printed. Defaults to false.
     * 
     * @param prettyPrinting
     *            if true, JSON contents are to be pretty printed
     */
    public void setPrettyPrinting(boolean prettyPrinting) {
	this.prettyPrinting = prettyPrinting;
    }

    /**
     * Define if result sets are compressed before download. Defaults to true.
     * 
     * @param gzipResult
     *            if true, sets are compressed before download
     */
    public void setGzipResult(boolean gzipResult) {
	this.gzipResult = gzipResult;
    }

    /**
     * Calls /get_version API
     * 
     * @throws AceQLException
     *             if any Exception occurs
     */
    public String getServerVersion() throws AceQLException {
	String result = callApiWithResult("get_version", null);
	return result;
    }

    /**
     * Gets the SDK version
     * 
     * @throws AceQLException
     *             if any Exception occurs
     */
    public String getClientVersion() {
	return org.kawanfw.sql.version.Version.getVersion();
    }

    /**
     * Calls /disconnect API
     * 
     * @throws AceQLException
     *             if any Exception occurs
     */
    public void disconnect() throws AceQLException {
	callApiNoResult("disconnect", null);
    }

    /**
     * Calls /commit API
     * 
     * @throws AceQLException
     *             if any Exception occurs
     */
    public void commit() throws AceQLException {
	callApiNoResult("commit", null);
    }

    /**
     * Calls /rollback API
     * 
     * @throws AceQLException
     *             if any Exception occurs
     */
    public void rollback() throws AceQLException {
	callApiNoResult("rollback", null);
    }

    /**
     * Calls /set_transaction_isolation_level API
     * 
     * @param level
     *            the isolation level
     * @throws AceQLException
     *             if any Exception occurs
     */
    public void setTransactionIsolation(String level) throws AceQLException {
	callApiNoResult("set_transaction_isolation_level", level);
    }

    /**
     * Calls /set_holdability API
     * 
     * @param holdability
     *            the holdability
     * @throws AceQLException
     *             if any Exception occurs
     */
    public void setHoldability(String holdability) throws AceQLException {
	callApiNoResult("set_holdability", holdability);
    }

    /**
     * Calls /set_auto_commit API
     *
     * @param autoCommit
     *            <code>true</code> to enable auto-commit mode;
     *            <code>false</code> to disable it
     * @throws AceQLException
     *             if any Exception occurs
     */
    public void setAutoCommit(boolean autoCommit) throws AceQLException {
	callApiNoResult("set_auto_commit", autoCommit + "");
    }

    /**
     * Calls /get_auto_commit API
     *
     * @param autoCommit
     *            <code>true</code> to enable auto-commit mode;
     *            <code>false</code> to disable it
     * @return the current state of this <code>Connection</code> object's
     *         auto-commit mode
     * @throws AceQLException
     *             if any Exception occurs
     */
    public boolean getAutoCommit() throws AceQLException {
	String result = callApiWithResult("get_auto_commit", null);
	return Boolean.parseBoolean(result);
    }

    /**
     * Calls /get_auto_commit API
     *
     * @return <code>true</code> if this <code>Connection</code> object is
     *         read-only; <code>false</code> otherwise
     * @throws AceQLException
     *             if any Exception occurs
     */
    public boolean isReadOnly() throws AceQLException {
	String result = callApiWithResult("is_read_only", null);
	return Boolean.parseBoolean(result);
    }

    /**
     * Calls /set_read_only API
     *
     * @param readOnly
     *            {@code true} enables read-only mode; {@code false} disables it
     * @throws AceQLException
     *             if any Exception occurs
     */
    public void setReadOnly(boolean readOnly) throws AceQLException {
	callApiNoResult("set_read_only", readOnly + "");
    }

    /**
     * Calls /get_holdability API
     *
     * @return the holdability, one of <code>hold_cursors_over_commit</code> or
     *         <code>close_cursors_at_commit</code>
     * @throws AceQLException
     *             if any Exception occurs
     */
    public String getHoldability() throws AceQLException {
	String result = callApiWithResult("get_holdability", null);
	return result;
    }

    /**
     * Calls /get_transaction_isolation_level API
     *
     * @return the current transaction isolation level, which will be one of the
     *         following constants: <code>transaction_read_uncommitted</code>,
     *         <code>transaction_read_committed</code>,
     *         <code>transaction_repeatable_read</code>,
     *         <code>transaction_serializable</code>, or
     *         <code>transaction_none</code>.
     * @throws AceQLException
     *             if any Exception occurs
     */
    public String getTransactionIsolation() throws AceQLException {
	String result = callApiWithResult("get_transaction_isolation_level",
		null);
	return result;
    }

    /**
     * Calls /execute_update API
     * 
     * @param sql
     *            an SQL <code>INSERT</code>, <code>UPDATE</code> or
     *            <code>DELETE</code> statement or an SQL statement that returns
     *            nothing
     * @param isPreparedStatement
     *            if true, the server will generate a prepared statement, else a
     *            simple statement
     * @param statementParameters
     *            the statement parameters in JSON format. Maybe null for simple
     *            statement call.
     * @return either the row count for <code>INSERT</code>, <code>UPDATE</code>
     *         or <code>DELETE</code> statements, or <code>0</code> for SQL
     *         statements that return nothing
     * @throws AceQLException
     *             if any Exception occurs
     */
    public int executeUpdate(String sql, boolean isPreparedStatement,
	    Map<String, String> statementParameters) throws AceQLException {

	try {
	    if (sql == null) {
		throw new NullPointerException("sql is null!");
	    }

	    String action = "execute_update";

	    Map<String, String> parametersMap = new HashMap<String, String>();
	    parametersMap.put("sql", sql);
	    // parametersMap.put("prepared_statement", new Boolean(
	    // isPreparedStatement).toString());
	    parametersMap.put("prepared_statement", "" + isPreparedStatement);

	    trace("sql: " + sql);
	    trace("statement_parameters: " + statementParameters);

	    // Add the statement parameters map
	    if (statementParameters != null) {
		parametersMap.putAll(statementParameters);
	    }

	    try (InputStream in = callWithPost(action, parametersMap);) {

		String result = null;

		if (in != null) {
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
		    IOUtils.copy(in, out);

		    result = out.toString("UTF-8");
		    trace("result: " + result);
		}

		ResultAnalyzer resultAnalyzer = new ResultAnalyzer(result,
			httpStatusCode, httpStatusMessage);
		if (!resultAnalyzer.isStatusOk()) {
		    throw new AceQLException(resultAnalyzer.getErrorMessage(),
			    resultAnalyzer.getErrorType(), null,
			    resultAnalyzer.getStackTrace(), httpStatusCode);
		}

		int rowCount = resultAnalyzer.getIntvalue("row_count");
		return rowCount;

	    }
	} catch (Exception e) {
	    if (e instanceof AceQLException) {
		throw (AceQLException) e;
	    } else {
		throw new AceQLException(e.getMessage(), 0, e, null,
			httpStatusCode);
	    }
	}

    }

    /**
     * Calls /execute_query API
     * 
     * @param sql
     *            an SQL <code>INSERT</code>, <code>UPDATE</code> or
     *            <code>DELETE</code> statement or an SQL statement that returns
     *            nothing
     * @param isPreparedStatement
     *            if true, the server will generate a prepared statement, else a
     *            simple statement
     * @param statementParameters
     *            the statement parameters in JSON format. Maybe null for simple
     *            statement call.
     * @return the input stream containing either an error, or the result set in
     *         JSON format. See user documentation.
     * @throws AceQLException
     *             if any Exception occurs
     */
    public InputStream executeQuery(String sql, boolean isPreparedStatement,
	    Map<String, String> statementParameters) throws AceQLException {

	try {
	    if (sql == null) {
		throw new NullPointerException("sql is null!");
	    }

	    String action = "execute_query";

	    Map<String, String> parametersMap = new HashMap<String, String>();
	    parametersMap.put("sql", sql);
	    // parametersMap.put("prepared_statement", new Boolean(
	    // isPreparedStatement).toString());
	    parametersMap.put("prepared_statement", "" + isPreparedStatement);

	    // parametersMap
	    // .put("gzip_result", new Boolean(gzipResult).toString());
	    parametersMap.put("gzip_result", "" + gzipResult);

	    // parametersMap.put("pretty_printing",
	    // new Boolean(prettyPrinting).toString());

	    parametersMap.put("pretty_printing", "" + prettyPrinting);

	    // Add the statement parameters map
	    if (statementParameters != null) {
		parametersMap.putAll(statementParameters);
	    }

	    trace("sql: " + sql);
	    trace("statement_parameters: " + statementParameters);

	    InputStream in = callWithPost(action, parametersMap);
	    return in;

	} catch (Exception e) {
	    if (e instanceof AceQLException) {
		throw (AceQLException) e;
	    } else {
		throw new AceQLException(e.getMessage(), 0, e, null,
			httpStatusCode);
	    }
	}

    }

    /**
     * Calls /blob_upload API
     * 
     * @param blobId
     *            the Blob/Clob Id
     * @param inputStream
     *            the local Blob/Clob local file input stream
     * @throws AceQLException
     *             if any Exception occurs
     */
    public void blobUpload(String blobId, InputStream inputStream,
	    long totalLength) throws AceQLException {

	try {
	    if (blobId == null) {
		throw new NullPointerException("blobId is null!");
	    }

	    if (inputStream == null) {
		throw new NullPointerException("inputStream is null!");
	    }

	    // if (file == null) {
	    // throw new NullPointerException("file is null!");
	    // }
	    //
	    // if (!file.exists()) {
	    // throw new FileNotFoundException("file does not exist: " + file);
	    // }

	    URL theURL = new URL(url + "blob_upload");

	    trace("request : " + theURL);
	    HttpURLConnection conn = null;

	    if (proxy == null) {
		conn = (HttpURLConnection) theURL.openConnection();
	    } else {
		conn = (HttpURLConnection) theURL.openConnection(proxy);
	    }

	    conn.setRequestProperty("Accept-Charset", "UTF-8");
	    conn.setRequestMethod("POST");
	    conn.setReadTimeout(readTimeout);
	    conn.setDoOutput(true);

	    final MultipartUtility http = new MultipartUtility(theURL, conn,
		    connectTimeout, progress, cancelled, totalLength);

	    Map<String, String> parameters = new HashMap<String, String>();
	    parameters.put("blob_id", blobId);

	    for (Map.Entry<String, String> entry : parameters.entrySet()) {
		// trace(entry.getKey() + "/" + entry.getValue());
		http.addFormField(entry.getKey(), entry.getValue());
	    }

	    // Server needs a unique file name to store the blob
	    String fileName = UUID.randomUUID().toString() + ".blob";

	    http.addFilePart("file", inputStream, fileName);

	    http.finish();

	    conn = http.getConnection();

	    // Analyze the error after request execution
	    httpStatusCode = conn.getResponseCode();
	    httpStatusMessage = conn.getResponseMessage();

	    trace("blob_id          : " + blobId);
	    trace("httpStatusCode   : " + httpStatusCode);
	    trace("httpStatusMessage: " + httpStatusMessage);

	    InputStream inConn = null;

	    String result;
	    try {
		if (httpStatusCode == HttpURLConnection.HTTP_OK) {
		    inConn = conn.getInputStream();
		} else {
		    inConn = conn.getErrorStream();
		}

		result = null;

		if (inConn != null) {
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
		    IOUtils.copy(inConn, out);
		    result = out.toString("UTF-8");
		}
	    } finally {
		// IOUtils.closeQuietly(inConn);
		if (inConn != null) {
		    try {
			inConn.close();
		    } catch (Exception e) {
			// e.printStackTrace();
		    }
		}
	    }

	    ResultAnalyzer resultAnalyzer = new ResultAnalyzer(result,
		    httpStatusCode, httpStatusMessage);
	    if (!resultAnalyzer.isStatusOk()) {
		throw new AceQLException(resultAnalyzer.getErrorMessage(),
			resultAnalyzer.getErrorType(), null,
			resultAnalyzer.getStackTrace(), httpStatusCode);
	    }

	} catch (Exception e) {
	    if (e instanceof AceQLException) {
		throw (AceQLException) e;
	    } else {
		throw new AceQLException(e.getMessage(), 0, e, null,
			httpStatusCode);
	    }
	}
    }

    /**
     * Calls /get_blob_length API
     * 
     * @param blobId
     *            the Blob/Clob Id
     * @return the server Blob/Clob length
     * @throws AceQLException
     *             if any Exception occurs
     */
    public long getBlobLength(String blobId) throws AceQLException {

	try {

	    if (blobId == null) {
		throw new NullPointerException("blobId is null!");
	    }

	    String action = "get_blob_length";

	    Map<String, String> parameters = new HashMap<String, String>();
	    parameters.put("blob_id", blobId);

	    ByteArrayOutputStream out = new ByteArrayOutputStream();

	    String result = null;

	    try (InputStream in = callWithPost(action, parameters);) {

		if (in != null) {
		    IOUtils.copy(in, out);
		    result = out.toString("UTF-8");
		}

	    }

	    ResultAnalyzer resultAnalyzer = new ResultAnalyzer(result,
		    httpStatusCode, httpStatusMessage);

	    if (!resultAnalyzer.isStatusOk()) {
		throw new AceQLException(resultAnalyzer.getErrorMessage(),
			resultAnalyzer.getErrorType(), null,
			resultAnalyzer.getStackTrace(), httpStatusCode);
	    }

	    String lengthStr = resultAnalyzer.getValue("length");
	    long length = Long.parseLong(lengthStr);
	    return length;

	} catch (Exception e) {
	    if (e instanceof AceQLException) {
		throw (AceQLException) e;
	    } else {
		throw new AceQLException(e.getMessage(), 0, e, null,
			httpStatusCode);
	    }
	}

    }

    /**
     * Calls /blob_download API
     * 
     * @param blobId
     *            the Blob/Clob Id
     * @return the input stream containing either an error, or the result set in
     *         JSON format. See user documentation.
     * @throws AceQLException
     *             if any Exception occurs
     */
    public InputStream blobDownload(String blobId) throws AceQLException {

	try {

	    if (blobId == null) {
		throw new NullPointerException("blobId is null!");
	    }

	    String action = "blob_download";

	    Map<String, String> parameters = new HashMap<String, String>();
	    parameters.put("blob_id", blobId);

	    InputStream in = null;

	    in = callWithPost(action, parameters);

	    // if (httpStatusCode != HttpURLConnection.HTTP_OK) {
	    // throw new AceQLException("HTTP_FAILURE" + " " + httpStatusCode
	    // + " " + httpStatusMessage, 0, httpStatusCode,
	    // httpStatusMessage);
	    // }

	    return in;

	} catch (Exception e) {
	    if (e instanceof AceQLException) {
		throw (AceQLException) e;
	    } else {
		throw new AceQLException(e.getMessage(), 0, e, null,
			httpStatusCode);
	    }
	}
    }

    /**
     * Formats & URL encode the the post data for POST.
     * 
     * @param params
     *            the parameter names and values
     * @return the formated and URL encoded string for the POST.
     * @throws UnsupportedEncodingException
     */
    public static String getPostDataString(Map<String, String> requestParams)
	    throws UnsupportedEncodingException {
	StringBuilder result = new StringBuilder();
	boolean first = true;

	for (Map.Entry<String, String> entry : requestParams.entrySet()) {

	    // trace(entry.getKey() + "/" + entry.getValue());

	    if (first)
		first = false;
	    else
		result.append("&");

	    if (entry.getValue() != null) {
		result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
		result.append("=");
		result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
	    }
	}

	return result.toString();
    }

    public int getHttpStatusCode() {
	return httpStatusCode;
    }

    /**
     * @return the httpStatusMessage
     */
    public String getHttpStatusMessage() {
	return httpStatusMessage;
    }

}
