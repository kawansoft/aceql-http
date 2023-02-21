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
package org.kawanfw.sql.servlet.sql.json_return;

import java.io.StringWriter;

import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.servlet.http.HttpServletResponse;

import schemacrawler.Version;

public class JsonErrorReturn {

    public static boolean LOG_JSON_ERROR = false;

    public static final String ACEQL_SERVLET_NOT_FOUND_IN_PATH = "AceQL main servlet not found in path: ";
    public static final String BLOB_DIRECTORY_DOES_NOT_EXIST = "Blob directory defined in DatabaseConfigurator.getBlobDirectory() does not exist: ";
    public static final String INVALID_CONNECTION = "Invalid or exipred Connection.";
    public static final String DATABASE_DOES_NOT_EXIST = "Database does not exist: ";
    public static final String ERROR_DOWNLOADING_BLOB = "An error occurred during Blob download: ";
    public static final String ERROR_UPLOADING_BLOB = "An error occurred during Blob upload: ";
    public static final String INVALID_BLOB_ID_DOWNLOAD = "Invalid blob_id. No Blob corresponding to blob_id: ";
    public static final String INVALID_BLOB_ID_UPLOAD = "Invalid blob_id. Cannot be used to create a file: ";
    public static final String INVALID_SESSION_ID = "Invalid session_id.";
    public static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password.";
    public static final String NO_ACTION_FOUND_IN_REQUEST = "No action found in request.";
    public static final String UNABLE_TO_GET_A_CONNECTION = "Unable to get a Connection.";
    public static final String UNKNOWN_SQL_ACTION = "Unknown SQL action or not supported by software";
    public static final String NO_DATASOURCES_DEFINED = "No databases have been defined in \"Tomcat JDBC Connection Pool Section\" in properties file.";
    public static final String UNKNOWN_SERVLET = "This servlet is unknown and has not been declared in properties file: ";
    public static final String PLEASE_UPDATE_CLIENT_SDK = "This HTTP AceQL server version ("
    + Version.getVersion() + ") is not compatible with Client SDK. Please upgrade Client SDK.";
    public static final String INVALID_OUTPUT_FORMAT = "The output format is invalid. Should be \"html\" or \"text\".";
    public static final String INVALID_TABLE_NAME = "SQL table not found in database.";
    public static final String ORACLE_SCHEMA_NOT_SUPPORTED = "SchemaCrawler schema generation is not supported with Oracle Database in this AceQL version.";
    
    public static final String ACCESS_FORBIDDEN_FOR_USERNAME = "Access Forbidden for Username";

    public static final int ERROR_JDBC_ERROR = 1;
    public static final int ERROR_ACEQL_ERROR = 2;
    public static final int ERROR_ACEQL_UNAUTHORIZED = 3;
    public static final int ERROR_ACEQL_FAILURE = 4;

  









    /*
     * { "httpStatus":"FAIL", "error_type":[code erreur numérique],
     * "error_message":"message d'erreur renvoyé par le serveur",
     * "stack_trace":"java stack trace" }
     */

    private int errorType = -1;
    private String errorMessage = null;
    private String stackTrace = null;
    private int httpStatus;

    /**
     * Constructor
     *
     * @param response
     *            the servlet response
     * @param httpStatus
     *            the http response httpStatus
     * @param errorType	the type of the error
     * @param errorMessage	the detailled text error message
     */
    public JsonErrorReturn(HttpServletResponse response, int httpStatus,
	    int errorType, String errorMessage) {
	super();

	response.setStatus(httpStatus);

	this.errorType = errorType;
	this.errorMessage = errorMessage;
	this.httpStatus = httpStatus;
    }

    /**
     * Constructor
     *
     * @param response
     * @param httpStatus
     *            http response httpStatus
     * @param errorType
     * @param errorMessage
     * @param stackTrace
     */
    public JsonErrorReturn(HttpServletResponse response, int httpStatus,
	    int errorType, String errorMessage, String stackTrace) {

	response.setStatus(httpStatus);

	this.errorType = errorType;
	this.errorMessage = errorMessage;
	this.stackTrace = stackTrace;
	this.httpStatus = httpStatus;
    }

    /**
     * Builds the error message
     *
     * @return the error message
     */
    public String build() {

	JsonGeneratorFactory jf = JsonUtil
		.getJsonGeneratorFactory(JsonUtil.DEFAULT_PRETTY_PRINTING);

	StringWriter sw = new StringWriter();
	JsonGenerator gen = jf.createGenerator(sw);

	gen.writeStartObject().write("status", "FAIL").write("error_type",
		errorType);

	if (errorMessage != null) {
	    gen.write("error_message", errorMessage);
	} else {
	    gen.write("error_message", JsonValue.NULL);
	}

	if (stackTrace != null) {
	    gen.write("stack_trace", stackTrace);
	    System.err.println(stackTrace);
	}

	gen.write("http_status", httpStatus);

	gen.writeEnd();
	gen.close();

	String doc = sw.toString();

	if (LOG_JSON_ERROR) {
	    System.err.println(doc);
	}

	return doc;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "JsonErrorReturn [errorType=" + errorType + ", errorMessage="
		+ errorMessage + ", stackTrace=" + stackTrace + ", httpStatus="
		+ httpStatus + "]";
    }

}
