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
package org.kawanfw.sql.api.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;

/**
 *
 * Interface that defines a trigger that will run Java code after a DELETE, 
 * INSERT or UPDATE call.
 * <p>
 * Note that there is no API for trigger *before* update calls: it would be unnecessarily redundant with 
 * {@link SqlFirewallManager#allowSqlRunAfterAnalysis(String, String, Connection, String, String, boolean, List)}.
 * <p>
 * Note that the framework comes with a default
 * <code>TriggerOnUpdate</code> implementation that does nothing:
 * {@link DefaultTriggerOnUpdate}.
 * <p>
 *
 * @author Nicolas de Pomereu
 */
public interface TriggerOnUpdate {

    /**
     * Allows to execute a trigger code in Java only after a database update ({@code DELETE},
     * {@code INSERT}, {@code UPDATE}) operation.<br>
     * <br>
     * Parameters allow for the passed client username and its IP address, to know
     * if statement is a prepared statement and to analyze the string representation
     * of the SQL statement that is received on the server. <br>
     *
     * @param username            the client username that sends the request
     * @param database            the database name as defined in the JDBC URL field
     * @param connection          The current SQL/JDBC <code>Connection</code>
     * @param ipAddress           the IP address of the client user
     * @param sql                 the SQL statement
     * @param isPreparedStatement Says if the statement is a prepared statement
     * @param parameterValues     the parameter values of a prepared statement in
     *                            the natural order, empty list for a (non prepared)
     *                            statement
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     */
    public void runAfter(String username, String database, Connection connection, String ipAddress, String sql,
	    boolean isPreparedStatement, List<Object> parameterValues) throws IOException, SQLException;
}
