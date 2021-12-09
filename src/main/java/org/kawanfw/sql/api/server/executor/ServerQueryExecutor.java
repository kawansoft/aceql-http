/**
 * 
 */
package org.kawanfw.sql.api.server.executor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation of this interface allow client side to call a server side
 * programmed class that returns a {@code ResultSet}. This is a a kind of
 * <i>AceQL stored procedure</i> written in Java. <br>
 * 
 * @author Nicolas de Pomereu
 * @since 9.1
 *
 */
public interface ServerQueryExecutor {

    /**
     * Executes a a query and return {@code ResultSet}.
     * 
     * @param username   the client username that asks for the query. Allows to
     *                   perform security checks.
     * @param database   the database name as defined in the JDBC URL field. Allows
     *                   to perform security checks.
     * @param connection The current SQL/JDBC <code>Connection</code>.
     * @param ipAddress  the IP address of the client user. Allows to perform
     *                   security checks.
     * @param args       the parameters passed by the client side.
     * @return a <code>ResultSet</code> object that contains the data produced by
     *         the query; never <code>null</code>
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     */
    public ResultSet executeQuery(String username, String database, Connection connection, String ipAddress,
	    Object... params) throws IOException, SQLException;

}
