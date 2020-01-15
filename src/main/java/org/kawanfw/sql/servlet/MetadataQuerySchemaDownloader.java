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

    public MetadataQuerySchemaDownloader(HttpServletRequest request, HttpServletResponse response, Connection connection, AceQLMetaData aceQLMetaData) {
	this.request = request;
	this.response = response;
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
	    OutputStream out = response.getOutputStream();

	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.INVALID_OUTPUT_FORMAT);
	    ServerSqlManager.writeLine(out, errorReturn.build());
	    return;
	}

	if (tableName != null && !exists(aceQLMetaData, tableName)) {
	    OutputStream out = response.getOutputStream();
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.INVALID_TABLE_NAME);
	    ServerSqlManager.writeLine(out, errorReturn.build());
	    return;
	}

	SchemaInfoAccessor schemaInfoAccessor = new SchemaInfoAccessor(connection);
	if (!schemaInfoAccessor.isAccessible()) {
	    OutputStream out = response.getOutputStream();
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_ACEQL_FAILURE, schemaInfoAccessor.getFailureReason());
	    ServerSqlManager.writeLine(out, errorReturn.build());
	    return;
	}

	SchemaInfoSC schemaInfoSC = schemaInfoAccessor.getSchemaInfoSC();
	File tempFile = File.createTempFile("sc_output", null);
	schemaInfoSC.buildOnFile(tempFile, aceQLOutputFormat, tableName);

	String type = aceQLOutputFormat == AceQLOutputFormat.html ? "text/html" : "text/plain";
	response.setContentType(type);

	try (InputStream in = new BufferedInputStream(new FileInputStream(tempFile));
		OutputStream out = response.getOutputStream();) {
	    IOUtils.copy(in, out);
	}
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
