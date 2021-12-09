/**
 * 
 */
package org.kawanfw.sql.api.server.executor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Allows client side to call a server side programmed class that returns a
 * {@code ResultSet}. This is ad a kind of stored procedure in Java. <br>
 * <br>
 * Client usage is limited to the Client JDBC Driver for this version.<br>
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
     *                   perform a security check.
     * @param connection The current SQL/JDBC <code>Connection</code>.
     * @param args       the parameters passed by the client side.
     * @return a <code>ResultSet</code> object that contains the data produced by
     *         the query; never <code>null</code>
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     */
    public ResultSet executeQuery(String username, Connection connection, Object... params)
	    throws IOException, SQLException;

}
