/**
 * 
 */
package org.kawanfw.sql.api.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Default implementation of {@link TriggerOnUpdate}.
 *
 * @author Nicolas de Pomereu
 */
public class DefaultJavaTriggerOnUpdate implements TriggerOnUpdate {

    /**
     * This default implementation does nothing for the sake of speed execution.
     */
    @Override
    public void runAfter(String username, String database, Connection connection, String ipAddress, String sql,
	    boolean isPreparedStatement, List<Object> parameterValues) throws IOException, SQLException {
    }
}
