/**
 *
 */
package org.kawanfw.sql.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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

    private HttpServletRequest request = null;
    private HttpServletResponse response = null;

    public BlobUploader(HttpServletRequest request, HttpServletResponse response) {
	this.request = request;
	this.response = response;
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
	    PrintWriter out = response.getWriter();
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_NOT_FOUND,
		    JsonErrorReturn.ERROR_ACEQL_ERROR,
		    JsonErrorReturn.BLOB_DIRECTORY_DOES_NOT_EXIST + blobDirectory.getName());
	    out.println(errorReturn.build());
	    return;
	}

	PrintWriter out = response.getWriter();
	try {
	    BlobUploadConfigurator blobUploadConfigurator = ServerSqlManager.getBlobUploadConfigurator();
	    blobUploadConfigurator.upload(request, response, blobDirectory);

	    // Say it's OK to the client
	    out.println(JsonOkReturn.build());
	} catch (Exception e) {

	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.ERROR_UPLOADING_BLOB + e.getMessage(),
		    ExceptionUtils.getStackTrace(e));
	    out.println(errorReturn.build());

	    LoggerUtil.log(request, e);
	}
    }


}
