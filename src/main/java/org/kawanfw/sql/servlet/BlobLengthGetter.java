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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonOkReturn;
import org.kawanfw.sql.servlet.util.BlobUtil;

/**
 * @author Nicolas de Pomereu
 *
 */
public class BlobLengthGetter {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private OutputStream out;
    private String username;
    private DatabaseConfigurator databaseConfigurator;


    public BlobLengthGetter(HttpServletRequest request, HttpServletResponse response, OutputStream out,
	    String username, DatabaseConfigurator databaseConfigurator) {
	super();
	this.request = request;
	this.response = response;
	this.out = out;
	this.username = username;
	this.databaseConfigurator = databaseConfigurator;
    }

    /**
     * @param request
     * @param response
     * @param username
     * @param databaseConfigurator
     * @throws IOException
     * @throws SQLException
     */
    public void getLength() throws IOException, SQLException {
	String blobId = request.getParameter(HttpParameter.BLOB_ID);
	long length = -1;

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
	    length = BlobUtil.getBlobLength(blobId, blobDirectory);
	} catch (Exception e) {
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_NOT_FOUND,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.INVALID_BLOB_ID_DOWNLOAD + blobId);
	    ServerSqlManager.writeLine(out, errorReturn.build());
	    return;
	}

	ServerSqlManager.writeLine(out, JsonOkReturn.build("length", length + ""));
    }

}
