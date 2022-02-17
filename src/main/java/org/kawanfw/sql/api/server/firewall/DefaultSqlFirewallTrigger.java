/**
 * 
 */
package org.kawanfw.sql.api.server.firewall;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kawanfw.sql.api.server.DefaultDatabaseConfigurator;
import org.kawanfw.sql.api.server.SqlEvent;

/**
 * Default trigger for all SQL databases when a {@code SqlFirewallManager}
 * detects an attack. <br>
 * 
 * @author Nicolas de Pomereu
 * @since 11.0
 */
public class DefaultSqlFirewallTrigger implements SqlFirewallTrigger {

    /**
     * Logs the info using {@code DefaultDatabaseConfigurator#getLogger()}
     * {@code Logger}.
     */
    @Override
    public void runIfStatementRefused(SqlEvent sqlEvent, SqlFirewallManager sqlFirewallManager, Connection connection)
	    throws IOException, SQLException {
	String logInfo = null;

	String sqlFirewallManagerClassName = sqlFirewallManager.getClass().getName();

	if (sqlEvent.isMetadataQuery()) {
	    logInfo = "Client username " + sqlEvent.getUsername() + " (IP: " + sqlEvent.getIpAddress()
		    + ") has been denied by " + sqlFirewallManagerClassName
		    + " SqlFirewallManager executing a Metadata Query API.";
	} else {
	    logInfo = "Client username " + sqlEvent.getUsername() + " (IP: " + sqlEvent.getIpAddress()
		    + ") has been denied by " + sqlFirewallManagerClassName
		    + " SqlFirewallManager executing sql statement: " + sqlEvent.getSql() + " with parameters: "
		    + sqlEvent.getParameterStringValues();
	}

	DefaultDatabaseConfigurator defaultDatabaseConfigurator = new DefaultDatabaseConfigurator();
	Logger logger = defaultDatabaseConfigurator.getLogger();
	logger.log(Level.WARNING, logInfo);

    }

}
