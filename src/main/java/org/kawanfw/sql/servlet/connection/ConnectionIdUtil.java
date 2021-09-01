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

    private static final String STATELESS = "stateless";

    /**
     * A simple wrapper for connection.hashCode();
     * @param connection
     * @return connection.hashCode() in String value
     */
    public static String getConnectionId(Connection connection) {
        return "" + connection.hashCode();
    }

    /**
     * Returns the Connection Id value for a stateless Connection
     * @return the Connection Id value for a stateless Connection
     */
    public static String getStatelessConnectionId() {
	return STATELESS;
    }

}
