/**
 * 
 */
package org.kawanfw.sql.servlet.connection;

import java.sql.Connection;

/**
 * 
 * @author Nicolas de Pomereu
 *
 */
public class ConnectionIdUtil {

    /**
     * A simple wrapper for connection.hashCode();
     * @param connection
     * @return connection.hashCode() in String value
     */
    public static String getConnectionId(Connection connection) {
        return "" + connection.hashCode();
    }

    /**
     * Says if the Connection Id is a stateless one
     * @param connectionId the Connection Id
     * @return true if the Connection Id is a stateless one, else false
     */
    public static boolean isStateless(String connectionId) {
	return connectionId.equals("stateless");
    }

}
