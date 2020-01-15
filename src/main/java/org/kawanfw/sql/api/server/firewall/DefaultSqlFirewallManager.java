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
 * Default firewall manager for all SQL databases. <br>
 * <br>
 * <b>WARNING</b>: This default implementation will allow to start immediate
 * remote SQL calls but is <b>*not*</b> at all secured. <br>
 * <b>It is highly recommended to override this class with a secured
 * implementation for all methods.</b>
 *
 * @author Nicolas de Pomereu
 *
 */
public class DefaultSqlFirewallManager implements SqlFirewallManager {

    /**
     * Constructor. {@code SqlFirewallManager} implementation must have no
     * constructor or a unique no parameters constructor.
     */
    public DefaultSqlFirewallManager() {

    }

    /**
     * @return <code><b>true</b></code>. (Client programs will be allowed to create
     *         raw <code>Statement</code>, i.e. call statements without parameters.)
     */
    @Override
    public boolean allowStatementClass(String username, String database, Connection connection)
	    throws IOException, SQLException {
	return true;
    }

    /**
     * @return <code><b>true</b></code>. No analysis is done so all SQL statements
     *         are authorized.
     */
    @Override
    public boolean allowSqlRunAfterAnalysis(String username, String database, Connection connection, String ipAddress,
	    String sql, boolean isPreparedStatement, List<Object> parameterValues) throws IOException, SQLException {
	return true;
    }

    /**
     * @return <code><b>true</b></code>. (Client programs will be allowed to call a
     *         database update statement.)
     */
    @Override
    public boolean allowExecuteUpdate(String username, String database, Connection connection)
	    throws IOException, SQLException {
	return true;
    }

    /**
     * @return <code><b>true</b></code>. (Client programs will be allowed to call
     *         the Metadata Query API).
     */
    @Override
    public boolean allowMetadataQuery(String username, String database, Connection connection)
	    throws IOException, SQLException {
	return true;
    }

    @Override
    public void runIfStatementRefused(String username, String database, Connection connection, String ipAddress,
	    boolean isMetadataQuery, String sql, List<Object> parameterValues) throws IOException, SQLException {

	String logInfo = null;

	if (isMetadataQuery) {
	    logInfo = "Client " + username + " (IP: " + ipAddress + ") has been denied executing a Metadata Query API.";
	} else {
	    logInfo = "Client " + username + " (IP: " + ipAddress + ") has been denied executing sql statement: " + sql
		    + " with parameters: " + parameterValues;
	}

	DefaultDatabaseConfigurator defaultDatabaseConfigurator = new DefaultDatabaseConfigurator();
	Logger logger = defaultDatabaseConfigurator.getLogger();
	logger.log(Level.WARNING, logInfo);

    }

}
