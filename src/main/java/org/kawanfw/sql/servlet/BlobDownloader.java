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
import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.blob.BlobDownloadConfigurator;
import org.kawanfw.sql.servlet.sql.LoggerUtil;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;

/**
 * @author Nicolas de Pomereu
 *
 */
public class BlobDownloader {

    private HttpServletRequest request;
    private HttpServletResponse response ;
    private String username;
    private DatabaseConfigurator databaseConfigurator ;
    private OutputStream out;


    public BlobDownloader(HttpServletRequest request, HttpServletResponse response, OutputStream out,
	    String username, DatabaseConfigurator databaseConfigurator) {
	this.request = request;
	this.response = response;
	this.out = out;
	this.username = username;
	this.databaseConfigurator = databaseConfigurator;
    }

    public void blobDownload() throws IOException, SQLException {
	String blobId = request.getParameter(HttpParameter.BLOB_ID);

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

	String fileName = databaseConfigurator.getBlobsDirectory(username).toString() + File.separator + blobId;

	File file = new File(fileName);

	if (!file.exists()) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_NOT_FOUND,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.INVALID_BLOB_ID_DOWNLOAD + blobId);
	    ServerSqlManager.writeLine(out, errorReturn.build());
	    return;
	}

	try {
	    BlobDownloadConfigurator BlobDownloader = ServerSqlManager.getBlobDownloadConfigurator();
	    BlobDownloader.download(request, file, out);
	} catch (Exception e) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.ERROR_DOWNLOADING_BLOB + e.getMessage(),
		    ExceptionUtils.getStackTrace(e));
	    ServerSqlManager.writeLine(out, errorReturn.build());

	    LoggerUtil.log(request, e);
	}
    }

}
