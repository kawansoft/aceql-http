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

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;
import org.kawanfw.sql.servlet.sql.json_return.JsonOkReturn;
import org.kawanfw.sql.servlet.util.BlobUtil;

/**
 * @author Nicolas de Pomereu
 *
 */
public class BlobLengthGetter {

    private HttpServletRequest request = null;
    private HttpServletResponse response = null;
    private String username = null;
    private DatabaseConfigurator databaseConfigurator = null;

    public BlobLengthGetter(HttpServletRequest request, HttpServletResponse response, String username,
	    DatabaseConfigurator databaseConfigurator) {
	super();
	this.request = request;
	this.response = response;
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
	OutputStream out;
	String blobId = request.getParameter(HttpParameter.BLOB_ID);
	long length = -1;

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

	try {
	    length = BlobUtil.getBlobLength(blobId, blobDirectory);
	} catch (Exception e) {
	    out = response.getOutputStream();
	    JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_NOT_FOUND,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.INVALID_BLOB_ID_DOWNLOAD + blobId);
	    ServerSqlManager.writeLine(out, errorReturn.build());
	    return;
	}

	out = response.getOutputStream();
	ServerSqlManager.writeLine(out, JsonOkReturn.build("length", length + ""));
	return;
    }

}
