/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2020,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.
 *
 * AceQL HTTP is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * AceQL HTTP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301  USA
 *
 * Any modifications to this file must keep this entire header
 * intact.
 */
package org.kawanfw.sql.api.server.firewall;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kawanfw.sql.api.server.DefaultDatabaseConfigurator;

/**
 * Firewall manager that denies any update of the database. The database is
 * thus guaranteed to be accessed in read only from client side.
 *
 * @author Nicolas de Pomereu
 * @since 4.0
 */
public class DenyExecuteUpdateManager extends DefaultSqlFirewallManager implements SqlFirewallManager {

    /**
     * @return <code>false</code>. (Nobody is allowed to update the database.).
     */
    @Override
    public boolean allowExecuteUpdate(String username, String database, Connection connection)
	    throws IOException, SQLException {
	return false;
    }
    /**
     * Logs the info using {@link DefaultDatabaseConfigurator#getLogger()} {@code Logger}.
     */
    @Override
    public void runIfStatementRefused(String username, String database, Connection connection, String ipAddress,
	    boolean isMetadataQuery, String sql, List<Object> parameterValues) throws IOException, SQLException {
	String logInfo = "Client username " + username + " (IP: " + ipAddress
		+ ") has been denied by DenyExecuteUpdateManager SqlFirewallManager executing the datadase write statement: "
		+ sql + ".";

	DefaultDatabaseConfigurator defaultDatabaseConfigurator = new DefaultDatabaseConfigurator();
	Logger logger = defaultDatabaseConfigurator.getLogger();
	logger.log(Level.WARNING, logInfo);
    }
}
