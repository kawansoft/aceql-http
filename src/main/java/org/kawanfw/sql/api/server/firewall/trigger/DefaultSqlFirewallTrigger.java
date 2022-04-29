/**
 * 
 */
package org.kawanfw.sql.api.server.firewall.trigger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.kawanfw.sql.api.server.DefaultDatabaseConfigurator;
import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;

/**
 * Default trigger for a SQL database when a {@code SqlFirewallManager} detects
 * an attack. <br>
 * 
 * @author Nicolas de Pomereu
 * @since 11.0
 */
public class DefaultSqlFirewallTrigger implements SqlFirewallTrigger {

    /**
     * Logs the info using {@link DefaultDatabaseConfigurator#getLogger()}
     * {@code Logger}.
     */
    @Override
    public void runIfStatementRefused(SqlEvent sqlEvent, SqlFirewallManager sqlFirewallManager, Connection connection)
	    throws IOException, SQLException {
	// Default implementation does nothing for the sake of speed execution
    }

}
