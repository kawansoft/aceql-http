package org.kawanfw.sql.servlet;

import javax.servlet.http.HttpServletRequest;

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
    
        if (implicitStoredProcedure) {
            return true;
        } else {
            return false;
        }
    
    }

    public static  boolean isSavepointModifier(String action) {
    
        if (action.equals(HttpParameter.SET_SAVEPOINT) || action.equals(HttpParameter.SET_SAVEPOINT_NAME)
        	|| action.equals(HttpParameter.ROLLBACK_SAVEPOINT) || action.equals(HttpParameter.RELEASE_SAVEPOINT)) {
            return true;
        } else {
            return false;
        }
    
    }

    public static  boolean isConnectionModifier(String action) {
        if (action.equals(HttpParameter.SET_AUTO_COMMIT) || action.equals(HttpParameter.COMMIT)
        	|| action.equals(HttpParameter.ROLLBACK) || action.equals(HttpParameter.SET_READ_ONLY)
        	|| action.equals(HttpParameter.SET_HOLDABILITY)
        	|| action.equals(HttpParameter.SET_TRANSACTION_ISOLATION_LEVEL) || action.equals(HttpParameter.CLOSE)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isConnectionReader(String action) {
        if (action.equals(HttpParameter.GET_AUTO_COMMIT) || action.equals(HttpParameter.GET_CATALOG)
        	|| action.equals(HttpParameter.GET_HOLDABILITY) || action.equals(HttpParameter.IS_READ_ONLY)
        	|| action.equals(HttpParameter.GET_TRANSACTION_ISOLATION_LEVEL)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Says if an Action asked by the client is for Awake FILE
     *
     * @param action the action asked by the client side
     * @return true if the action is for Awake FILE
     */
    @SuppressWarnings("unused")
    public static boolean isActionForBlob(String action) {
        if (action.equals(HttpParameter.BLOB_UPLOAD) || action.equals(HttpParameter.BLOB_DOWNLOAD)) {
            return true;
        } else {
            return false;
        }
    }

    public static  boolean isStatement(String action) {
        if (action.equals(HttpParameter.EXECUTE_UPDATE) || action.equals(HttpParameter.EXECUTE_QUERY)) {
            return true;
        } else {
            return false;
        }
    
    }

}
