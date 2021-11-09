/**
 * 
 */
package org.kawanfw.sql.api.server.listener;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Default implementation of {@code UpdateListener}. The
 * {@code updateActionPerformed(SqlActionEvent, Connection)}
 * does nothing for the sake of speed execution.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class DefaultUpdateListener implements UpdateListener {

    /**
     * Does nothing.
     */
    @Override
    public void updateActionPerformed(SqlActionEvent evt, Connection connection) throws IOException, SQLException {

    }

}
