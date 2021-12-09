/**
 * 
 */
package org.kawanfw.sql.api.server.executor;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Allows client side to call a server side programmed class that returns a {@code ResultSet}.
 * This a kind of stored procedure in Java.
 * <br>
 * @author Nicolas de Pomereu
 * @since 9.1
 *
 */
public interface RemoteQueryExecutor {
    
    /**
     * Executes a a query and return {@code ResultSet}.
     * @param args	the parameters to pass to the method
     * @return a <code>ResultSet</code> object that contains the data produced by the
     *         query; never <code>null</code>
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     */
    public ResultSet executeQuery(Object... params) throws IOException, SQLException;

}
