package org.kawanfw.sql.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.sql.servlet.connection.ConnectionStore;
import org.kawanfw.sql.servlet.sql.LoggerUtil;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;

public class ConnectionGetter {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private OutputStream out;

    private String username;
    private String sessionId;
    private String connectionId;

    private Connection connection;


    public ConnectionGetter(HttpServletRequest request, HttpServletResponse response, OutputStream out) {
	super();
	this.request = request;
	this.response = response;
	this.out = out;

	username = request.getParameter(HttpParameter.USERNAME);
	sessionId = request.getParameter(HttpParameter.SESSION_ID);
    }

    public boolean treatAndContinue() throws IOException {
	// TODO Auto-generated method stub
	try {
	    ConnectionStore connectionStore = new ConnectionStore(username, sessionId, connectionId);

	    // Hack to allow version 1.0 to continue to get connection
	    if (connectionId == null || connectionId.isEmpty()) {
		connection = connectionStore.getFirst();
	    } else {
		connection = connectionStore.get();
	    }

	    if (connection == null || connection.isClosed()) {
		JsonErrorReturn errorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_NOT_FOUND,
			JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.INVALID_CONNECTION);
		ServerSqlManager.writeLine(out, errorReturn.build());
		return false;
	    }

	} catch (SQLException e) {
	    JsonErrorReturn jsonErrorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.UNABLE_TO_GET_A_CONNECTION,
		    ExceptionUtils.getStackTrace(e));
	    ServerSqlManager.writeLine(out, jsonErrorReturn.build());
	    LoggerUtil.log(request, e);

	    return false;
	}

	return true;

    }

    public Connection getConnection() {
        return connection;
    }



}
