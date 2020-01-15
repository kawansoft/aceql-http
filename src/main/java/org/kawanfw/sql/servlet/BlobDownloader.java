/**
 *
 */
package org.kawanfw.sql.servlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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

    private HttpServletRequest request = null;
    private HttpServletResponse response = null;
    private String username = null;
    private DatabaseConfigurator databaseConfigurator = null;

    public BlobDownloader(HttpServletRequest request, HttpServletResponse response, String username,
	    DatabaseConfigurator databaseConfigurator) {
	super();
	this.request = request;
	this.response = response;
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
	    PrintWriter prinWriter = response.getWriter();
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_NOT_FOUND,
		    JsonErrorReturn.ERROR_ACEQL_ERROR,
		    JsonErrorReturn.BLOB_DIRECTORY_DOES_NOT_EXIST + blobDirectory.getName());
	    prinWriter.println(errorReturn.build());
	    return;
	}

	String fileName = databaseConfigurator.getBlobsDirectory(username).toString() + File.separator + blobId;

	File file = new File(fileName);

	if (!file.exists()) {
	    PrintWriter prinWriter = response.getWriter();
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_NOT_FOUND,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.INVALID_BLOB_ID_DOWNLOAD + blobId);
	    prinWriter.println(errorReturn.build());
	    return;
	}

	OutputStream out = response.getOutputStream();
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
