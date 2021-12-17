package org.kawanfw.sql.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.SqlEventWrapper;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.servlet.sql.ServerStatementUtil;
import org.kawanfw.sql.servlet.sql.json_return.JsonSecurityMessage;

public class ServerSqlDispatchUtil {

    public static boolean isStoredProcedure(HttpServletRequest request) {
	String storedProcedure = request.getParameter(HttpParameter.STORED_PROCEDURE);
	String sql = request.getParameter(HttpParameter.SQL);
	boolean explicitStoredProcedure = Boolean.parseBoolean(storedProcedure);

	if (explicitStoredProcedure) {
	    return true;
	}

	// From Python there maybe an implicit call without any more info
	boolean implicitStoredProcedure = false;

	if (sql != null) {
	    sql = sql.trim().toLowerCase();
	    if (sql.startsWith("{") && sql.endsWith("}") && sql.contains("call ")) {
		implicitStoredProcedure = true;
	    }
	}

	return implicitStoredProcedure;

    }

    public static boolean isActionsSetAutoCommitFalse(HttpServletRequest request) {
	String action = request.getParameter(HttpParameter.ACTION);
	boolean autoCommit = Boolean.parseBoolean(request.getParameter(HttpParameter.ACTION_VALUE));
	return action.equals(HttpParameter.SET_AUTO_COMMIT) && !autoCommit;
    }

    public static boolean isSavepointModifier(String action) {
	return action.equals(HttpParameter.SET_SAVEPOINT) || action.equals(HttpParameter.SET_NAMED_SAVEPOINT)
		|| action.equals(HttpParameter.ROLLBACK_SAVEPOINT) || action.equals(HttpParameter.RELEASE_SAVEPOINT);

    }

    public static boolean isConnectionModifier(String action) {
	return action.equals(HttpParameter.SET_AUTO_COMMIT) || action.equals(HttpParameter.COMMIT)
		|| action.equals(HttpParameter.ROLLBACK) || action.equals(HttpParameter.SET_READ_ONLY)
		|| action.equals(HttpParameter.SET_HOLDABILITY)
		|| action.equals(HttpParameter.SET_TRANSACTION_ISOLATION_LEVEL) || action.equals(HttpParameter.CLOSE);
    }

    public static boolean isConnectionReader(String action) {
	return action.equals(HttpParameter.GET_AUTO_COMMIT) || action.equals(HttpParameter.GET_CATALOG)
		|| action.equals(HttpParameter.GET_SCHEMA) || action.equals(HttpParameter.GET_HOLDABILITY)
		|| action.equals(HttpParameter.IS_READ_ONLY)
		|| action.equals(HttpParameter.GET_TRANSACTION_ISOLATION_LEVEL);
    }

    /**
     * Says if an Action asked by the client is for Awake FILE
     *
     * @param action the action asked by the client side
     * @return true if the action is for Awake FILE
     */
    public static boolean isActionForBlob(String action) {
	return action.equals(HttpParameter.BLOB_UPLOAD) || action.equals(HttpParameter.BLOB_DOWNLOAD);
    }

    public static boolean isExecuteQueryOrExecuteUpdate(String action) {
	return action.equals(HttpParameter.EXECUTE_UPDATE) || action.equals(HttpParameter.EXECUTE_QUERY);
    }

    public static boolean isExecute(String action) {
	return action.equals(HttpParameter.EXECUTE);
    }

    public static boolean isStatementExecuteBatch(String action) {
	return action.equals(HttpParameter.STATEMENT_EXECUTE_BATCH);
    }

    public static boolean isPreparedStatementExecuteBatch(String action) {
	return action.equals(HttpParameter.PREPARED_STATEMENT_EXECUTE_BATCH);
    }

    public static void checkMetadataAuthorized(HttpServletRequest request, Connection connection,
	    List<SqlFirewallManager> sqlFirewallManagers) throws IOException, SQLException {

	String username = request.getParameter(HttpParameter.USERNAME);
	String database = request.getParameter(HttpParameter.DATABASE);
	String ipAddress = request.getRemoteAddr();

	boolean allow = false;
	String sql = "<void>";
	for (SqlFirewallManager sqlFirewallManager : sqlFirewallManagers) {
	    allow = sqlFirewallManager.allowMetadataQuery(username, database, connection);
	    if (!allow) {
		List<Object> parameterValues = new ArrayList<>();

		SqlEvent sqlEvent = SqlEventWrapper.sqlEventBuild(username, database, ipAddress, sql,
			ServerStatementUtil.isPreparedStatement(request), parameterValues, false);
		
		sqlFirewallManager.runIfStatementRefused(sqlEvent, username, database, connection, ipAddress, true,
			sql, parameterValues);
		break;
	    }
	}

	if (!allow) {
	    Map<Integer, String> parameters = new HashMap<>();

	    List<Object> values = new ArrayList<>();
	    String message = JsonSecurityMessage.prepStatementNotAllowedBuild(sql,
		    "Metadata Query API calls are not allowed!", parameters, values, true);
	    throw new SecurityException(message);
	}

    }

}
