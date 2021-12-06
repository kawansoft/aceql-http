/**
 * 
 */
package org.kawanfw.sql.api.server.executor;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Allows client side to call a server side programmed class that returns a {@code ResultSet}.
 * This a kind of stored procedure in Java.
 * <br>
 * @author Nicolas de Pomereu
 * @since 9.1
 *
 */
public interface ServerQueryExecutor {
    
    /**
     * Executes a a query and return {@code ResultSet}.
     * @param args	the parameters to pass to the method
     * @return a <code>ResultSet</code> object that contains the data produced by the
     *         query; never <code>null</code>
     * @throws IOException
     * @throws IOException           if an IOException occurs
     */
    public ResultSet executeQuery(List<String> args) throws IOException, SQLException;

}
