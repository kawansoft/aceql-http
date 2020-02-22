/**
 *
 */
package org.kawanfw.sql.api.server;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Interface that defines how to authenticate a remote client that
 * wants to create an AceQL session.
 
 * @author Nicolas de Pomereu
 *
 */
public interface UserAuthenticator {

    /**
     * Allows to authenticate the remote {@code (username, password)} couple
     * sent by the client side.
     * <p>
     * The AceQL HTTP Server will call the method in order to grant or not
     * client access.
     * <p>
     * Typical usage would be to check the (username, password) couple against a
     * table in a SQL database or against a LDAP, etc.
     *
     * It's possible to use current Connection with a database by calling
     * {@link #getConnection(String)}. <br>
     * <br>
     * The method allows to retrieve:
     * <ul>
     * <li>The name of the database to which the client wants to connect.</li>
     * <li>The IP address of the client.</li>
     * </ul>
     *
     * @param username
     *            the username sent by the client
     * @param password
     *            the password to connect to the server
     * @param database
     *            the database name to which the client wants to connect
     * @param ipAddress
     *            the IP address of the client user
     * @return <code>true</code> if the (username, password) couple is
     *         correct/valid. If false, the client side will not be authorized
     *         to send any command.
     * @throws IOException
     *             if an IOException occurs
     * @throws SQLException
     *             if a SQLException occurs
     */
    public boolean login(String username, char[] password, String database,
	    String ipAddress) throws IOException, SQLException;

}
