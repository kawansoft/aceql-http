/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.kawanfw.sql.metadata.AceQLMetaData;
import org.kawanfw.sql.metadata.sc.info.AceQLOutputFormat;
import org.kawanfw.sql.metadata.sc.info.SchemaInfoAccessor;
import org.kawanfw.sql.metadata.sc.info.SchemaInfoSC;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;

public class MetadataQuerySchemaDownloader {

    private HttpServletRequest request = null;
    private HttpServletResponse response = null;
    private Connection connection = null;
    private AceQLMetaData aceQLMetaData = null;
    private OutputStream out;

    public MetadataQuerySchemaDownloader(HttpServletRequest request, HttpServletResponse response, OutputStream out, Connection connection, AceQLMetaData aceQLMetaData) {
	this.request = request;
	this.response = response;
	this.out = out;
	this.connection = connection;
	this.aceQLMetaData = aceQLMetaData;
    }

    /**
     * @throws IOException
     * @throws SQLException
     * @throws FileNotFoundException
     */
    public void schemaDowload() throws IOException, SQLException, FileNotFoundException {
	String format = request.getParameter(HttpParameter.FORMAT);
	String tableName = request.getParameter(HttpParameter.TABLE_NAME);

	AceQLOutputFormat aceQLOutputFormat = null;

	if (format == null || format.isEmpty()) {
	    format = AceQLOutputFormat.html.toString();
	}

	if (format.contentEquals(AceQLOutputFormat.html.toString())) {
	    aceQLOutputFormat = AceQLOutputFormat.html;
	} else if (format.contentEquals(AceQLOutputFormat.text.toString())) {
	    aceQLOutputFormat = AceQLOutputFormat.text;
	} else {

	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.INVALID_OUTPUT_FORMAT);
	    ServerSqlManager.writeLine(out, errorReturn.build());
	    return;
	}

	SchemaInfoAccessor schemaInfoAccessor = new SchemaInfoAccessor(connection);

	if (! checkBaseValues(tableName, schemaInfoAccessor)) {
	    return;
	}

	buildSchema(tableName, aceQLOutputFormat, schemaInfoAccessor);
    }

    /**
     * Builds  the schema.
     * @param tableName
     * @param aceQLOutputFormat
     * @param schemaInfoAccessor
     * @throws SQLException
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void buildSchema(String tableName, AceQLOutputFormat aceQLOutputFormat,
	    SchemaInfoAccessor schemaInfoAccessor) throws SQLException, IOException, FileNotFoundException {
	SchemaInfoSC schemaInfoSC = schemaInfoAccessor.getSchemaInfoSC();
	File tempFile = File.createTempFile("sc_output", null);
	schemaInfoSC.buildOnFile(tempFile, aceQLOutputFormat, tableName);

	String type = aceQLOutputFormat == AceQLOutputFormat.html ? "text/html" : "text/plain";
	response.setContentType(type);

	try (InputStream in = new BufferedInputStream(new FileInputStream(tempFile))) {
	    IOUtils.copy(in, out);
	}
    }

    /**
     * Check that he base values tableName & schemaInfoAccessor are OK
     * @param tableName
     * @param schemaInfoAccessor
     * @throws SQLException
     * @throws IOException
     */
    private boolean checkBaseValues(String tableName, SchemaInfoAccessor schemaInfoAccessor)
	    throws SQLException, IOException {
	if (tableName != null && !exists(aceQLMetaData, tableName)) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.INVALID_TABLE_NAME);
	    ServerSqlManager.writeLine(out, errorReturn.build());
	    return false;
	}

	if (!schemaInfoAccessor.isAccessible()) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_ACEQL_FAILURE, schemaInfoAccessor.getFailureReason());
	    ServerSqlManager.writeLine(out, errorReturn.build());
	    return false;
	}

	return true;
    }

    private static boolean exists(AceQLMetaData aceQLMetaData, String tableName) throws SQLException {
	List<String> tableNames = aceQLMetaData.getTableNames();
	for (String theTableName : tableNames) {
	    if (theTableName.contains(tableName.toLowerCase())) {
		return true;
	    }
	}
	return false;
    }

}
