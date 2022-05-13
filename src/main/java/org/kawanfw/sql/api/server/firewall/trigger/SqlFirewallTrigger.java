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
package org.kawanfw.sql.api.server.firewall.trigger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;

/**
 * Interface that allows to define a trigger if for the specified
 * {@code SqlFirewallManager} the {@code allowSqlRunAfterAnalysis()} method call
 * returns false. <br>
 * <br>
 * Multiple {@code SqlFirewallTrigger} may be defined and chained in property
 * value by separating class names by a comma. <br>
 * When {@code SqlFirewallTrigger} classes are chained, all of them are
 * successively executed in the declared order. 
 * <p>
 * Note that the framework comes with a default <code>SqlFirewallTrigger</code>
 * implementation that does nothing.
 * <p>
 * Built in and ready to use classes that don't require any coding are included.
 * The classes may be chained. See each Javadoc for more details:
 * <ul>
 * <li>{@link BanUserSqlFirewallTrigger}: a trigger that inserts the username
 * and other info into a SQL table. The SQL table is scanned/controlled at each
 * request, so the banned user cannot access any more the AceQL server.</li>
 * <li>{@link BeeperSqlFirewallTrigger}: a trigger that simply beeps on the
 * terminal if an attack is detected by a {@code SqlFirewallManager}.</li>
 * <li>{@link JdbcLoggerSqlFirewallTrigger}: a trigger that logs into a SQL
 * table all info about the denied SQL request.</li>
 * <li>{@link JsonLoggerSqlFirewallTrigger}: a trigger that logs in JSON format
 * all info about the denied SQL request.</li>
 * </ul>
 * <p>
 * 
 * @author Nicolas de Pomereu
 * @since 11.0
 */
public interface SqlFirewallTrigger {

    /**
     * Allows to implement specific a Java rule immediately after a SQL statement
     * described by a SqlEvent has been refused because one of the
     * <code>SqlFirewallManager.allowXxx</code> method returned false. <br>
     * <br>
     * Examples:
     * <ul>
     * <li>Delete the user from the username SQL table so that he never comes
     * back.</li>
     * <li>Log the IP address.</li>
     * <li>Log the info.</li>
     * <li>Send an alert message/email to a Security Officer.</li>
     * <li>Etc.</li>
     * </ul>
     * <p>
     * 
     * @param sqlEvent           the SQL event asked by the client side. Contains
     *                           all info about the SQL call (client username,
     *                           database name, IP Address of the client, and SQL
     *                           statement details).
     * @param sqlFirewallManager the instance that that triggers this call.
     * @param connection         The current SQL/JDBC <code>Connection</code>
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     */
    public void runIfStatementRefused(SqlEvent sqlEvent, SqlFirewallManager sqlFirewallManager, Connection connection)
	    throws IOException, SQLException;

}
