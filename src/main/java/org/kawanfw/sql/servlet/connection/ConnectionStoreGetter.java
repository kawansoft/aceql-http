package org.kawanfw.sql.servlet.connection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.sql.json_return.JsonErrorReturn;

public class ConnectionStoreGetter {

    private HttpServletResponse response;

    private String username;
    private String sessionId;
    private String connectionId;
    private JsonErrorReturn jsonErrorReturn;

    /**
     * Constructor
     * 
     * @param request
     * @param response
     */
    public ConnectionStoreGetter(HttpServletRequest request, HttpServletResponse response) {
	super();
	this.response = response;

	username = request.getParameter(HttpParameter.USERNAME);
	sessionId = request.getParameter(HttpParameter.SESSION_ID);

	// Fix 16/07/21: this was not done:
	connectionId = request.getParameter(HttpParameter.CONNECTION_ID);

    }

    /**
     * Gets a Connection from the Store
     * 
     * @return A Connection, or null if can not get any
     * @throws IOException
     */
    public Connection getConnection() throws IOException {
	Connection connection = null;
	try {
	    ConnectionStore connectionStore = new ConnectionStore(username, sessionId, connectionId);

	    // Hack to allow version 1.0 to continue to get connection
	    if (connectionId == null || connectionId.isEmpty()) {
		connection = connectionStore.getFirst();
	    } else {
		connection = connectionStore.get();
	    }

	    if (connection == null || connection.isClosed()) {
		jsonErrorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_NOT_FOUND,
			JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.INVALID_CONNECTION);
	    }

	} catch (SQLException e) {
	    RollbackUtil.rollback(connection);

	    jsonErrorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_BAD_REQUEST,
		    JsonErrorReturn.ERROR_ACEQL_ERROR, JsonErrorReturn.UNABLE_TO_GET_A_CONNECTION,
		    ExceptionUtils.getStackTrace(e));
	} catch (Exception e) {
	    
	    RollbackUtil.rollback(connection);
	    
	    jsonErrorReturn = new JsonErrorReturn(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
		    JsonErrorReturn.ERROR_ACEQL_FAILURE, e.getMessage(), ExceptionUtils.getStackTrace(e));
	}

	return connection;
    }

    /**
     * @return the Json error block in case any error when trying to get the
     *         connection
     */
    public JsonErrorReturn getJsonErrorReturn() {
	return jsonErrorReturn;
    }

}
