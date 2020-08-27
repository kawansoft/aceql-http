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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.blob.BlobUploadConfigurator;
import org.kawanfw.sql.servlet.sql.LoggerUtil;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonOkReturn;

/**
 * @author Nicolas de Pomereu
 *
 */
public class BlobUploader {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private OutputStream out;

    public BlobUploader(HttpServletRequest request, HttpServletResponse response, OutputStream out) {
	this.request = request;
	this.response = response;
	this.out = out;
    }

    public void blobUpload()
	    throws IOException, FileUploadException, SQLException {
	//debug("BlobUploadConfigurator Start");

	// Pass Username & Database because they can't be recovered from
	// stream. The underlying HttpServletRequest is a
	// HttpServletRequestHolder that stores parameters in map

	String username = request.getParameter(HttpParameter.USERNAME);
	String database = request.getParameter(HttpParameter.DATABASE);
	DatabaseConfigurator databaseConfigurator = ServerSqlManager.getDatabaseConfigurator(database);

	File blobDirectory = databaseConfigurator.getBlobsDirectory(username);

	if (blobDirectory != null && !blobDirectory.exists()) {
	    blobDirectory.mkdirs();
	}

	if (blobDirectory == null || !blobDirectory.exists()) {

	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_NOT_FOUND,
		    JsonErrorReturn.ERROR_ACEQL_ERROR,
		    JsonErrorReturn.BLOB_DIRECTORY_DOES_NOT_EXIST + blobDirectory.getName());
	    ServerSqlManager.writeLine(out, errorReturn.build());
	    return;
	}

	try {
	    BlobUploadConfigurator blobUploadConfigurator = ServerSqlManager.getBlobUploadConfigurator();
	    blobUploadConfigurator.upload(request, response, blobDirectory);

	    // Say it's OK to the client
	    ServerSqlManager.writeLine(out, JsonOkReturn.build());
	} catch (Exception e) {

	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.ERROR_UPLOADING_BLOB + e.getMessage(),
		    ExceptionUtils.getStackTrace(e));
	    ServerSqlManager.writeLine(out, errorReturn.build());

	    LoggerUtil.log(request, e);
	}
    }


}
