/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2021,  KawanSoft SAS
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
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kawanfw.sql.api.server.DefaultDatabaseConfigurator;
import org.kawanfw.sql.api.server.SqlEvent;

/**
 * Firewall manager that denies any call of the raw <code>Statement</code>
 * class. (Calling Statements without parameters is forbidden).
 *
 * @author Nicolas de Pomereu
 * @since 4.0
 */
public class DenyStatementClassManager extends DefaultSqlFirewallManager implements SqlFirewallManager {

    /**
     * @return <code>false</code>. (Nobody is allowed to create raw
     *         <code>Statement</code>, i.e. call statements without parameters.)
     */
    @Override
    public boolean allowStatementClass(String username, String database, Connection connection)
	    throws IOException, SQLException {
	return false;
    }

    /**
     * Logs the info using {@link DefaultDatabaseConfigurator#getLogger()} {@code Logger}.
     */
    @Override
    public void runIfStatementRefused(SqlEvent sqlEvent, Connection connection) throws IOException, SQLException {
	
//	String logInfo = "Client username " + username + " (IP: " + ipAddress
//		+ ") has been denied by DenyStatementClassManager SqlFirewallManager executing the statement: " + sql
//		+ ".";
	
	Objects.requireNonNull(sqlEvent, "sqlEvent cannot be null!");
	String logInfo = "Client username " + sqlEvent.getUsername() + " (IP: " + sqlEvent.getIpAddress()
		+ ") has been denied by DenyStatementClassManager SqlFirewallManager executing the statement: " + sqlEvent.getSql()
		+ ".";
	
	DefaultDatabaseConfigurator defaultDatabaseConfigurator = new DefaultDatabaseConfigurator();
	Logger logger = defaultDatabaseConfigurator.getLogger();
	logger.log(Level.WARNING, logInfo);
    }

}
