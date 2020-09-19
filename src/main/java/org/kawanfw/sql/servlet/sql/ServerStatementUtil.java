/**
 *
 */
package org.kawanfw.sql.servlet.sql;

import javax.servlet.http.HttpServletRequest;

import org.kawanfw.sql.servlet.HttpParameter;

/**
 * Utility class for ServerStatement & ServerExecute.
 * @author Nicolas de Pomereu
 *
 */
public class ServerStatementUtil {

    /**
     * Static class.
     */
    protected ServerStatementUtil() {

    }

    public static boolean isPreparedStatement(HttpServletRequest request) {
        String preparedStatement = request.getParameter(HttpParameter.PREPARED_STATEMENT);
        return Boolean.parseBoolean(preparedStatement);
    }

}
