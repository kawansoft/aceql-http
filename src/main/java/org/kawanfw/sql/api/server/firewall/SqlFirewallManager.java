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
import java.sql.Statement;
import java.util.List;

import org.kawanfw.sql.api.server.StatementAnalyzer;

/**
 * Interface that allows to define firewall rules for AceQL HTTP SQL calls. <br>
 * Concrete implementations are defined in aceql-server.properties. <br><br>
 * A concrete implementation should be developed on the server side in order to:
 * <ul>
 * <li>Define if a client user has the right to call a
 * <code>Statement.executeUpdate</code> (i.e. call a statement that updates the
 * database).</li>
 * <li>Define if a client user has the right to call a raw
 * <code>Statement</code> that is not a <code>PreparedStatement</code>.</li>
 * <li>Define if a client user has the right to call a the AceQL Metadata
 * API..</li>
 * <li>Define a specific piece of Java code to analyze the source code of the
 * SQL statement before allowing or not it's execution.</li>
 * </ul>
 * <p>
 * Multiple {@code SqlFirewallManager} may be defined and chained.
 * <p>
 * Note that the framework comes with a Default <code>SqlFirewallManager</code>
 * implementation that is *not* secured and should be extended:
 * {@link DefaultSqlFirewallManager}.
 * <p>
 * The framework is also provided with built in / ready to use classes that
 * don't require any coding. The classes may be chained. See each Javadoc for
 * more details:
 * <ul>
 * <li>{@link DenyDclManager}</li>
 * <li>{@link DenyDdlManager}</li>
 * <li>{@link DenyExecuteUpdateManager}</li>
 * <li>{@link DenyMetadataQueryManager}</li>
 * <li>{@link DenyStatementClassManager}</li>
 * </ul>
 * <p>
 * Note that the helper class {@link StatementAnalyzer} allows to do some simple
 * tests on the SQL statement string representation.
 */

public interface SqlFirewallManager {

    /**
     * Says if the username is allowed call the Metadata Query API for the passed
     * database.
     *
     * @param username   the client username to check the rule for.
     * @param database   the database name as defined in the JDBC URL field
     * @param connection The current SQL/JDBC <code>Connection</code>
     * @return <code>true</code> if the user has the right to call the Metada Query
     *         API, else <code>false</code>.
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     */
    public boolean allowMetadataQuery(String username, String database, Connection connection)
	    throws IOException, SQLException;

    /**
     * Allows to define if the passed username is allowed to create and use a
     * {@link Statement} instance that is not a <code>PreparedStatement</code>
     *
     * @param username   the client username to check the rule for.
     * @param database   the database name as defined in the JDBC URL field
     * @param connection The current SQL/JDBC <code>Connection</code>
     * @return <code>true</code> if the user has the right to call a raw
     *         <code>execute</code>
     *         <p>
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     *
     */
    public boolean allowStatementClass(String username, String database, Connection connection)
	    throws IOException, SQLException;

    /**
     * Allows, for the passed client username and its IP address, to know if
     * statement is a prepared statement an to analyze the string representation of
     * the SQL statement that is received on the server. <br>
     * If the analysis defined by the method returns false, the SQL statement won't
     * be executed.
     *
     * @param username            the client username to check the rule for.
     * @param database            the database name as defined in the JDBC URL field
     * @param connection          The current SQL/JDBC <code>Connection</code>
     * @param ipAddress           the IP address of the client user
     * @param sql                 the SQL statement
     * @param isPreparedStatement Says if the statement is a prepared statement
     * @param parameterValues     the parameter values of a prepared statement in
     *                            the natural order, empty list for a (non prepared)
     *                            statement
     * @return <code>true</code> if the analyzed statement or prepared statement is
     *         validated and authorized to run, else <code>false</code>.
     *         <p>
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     */
    public boolean allowSqlRunAfterAnalysis(String username, String database, Connection connection, String ipAddress,
	    String sql, boolean isPreparedStatement, List<Object> parameterValues) throws IOException, SQLException;

    /**
     * Allows to define if the passed username is allowed to call a statement that
     * updates the database.
     *
     * @param username   the client username to check the rule for.
     * @param database   the database name as defined in the JDBC URL field
     * @param connection The current SQL/JDBC <code>Connection</code>
     * @return <code>true</code> if the user has the right call a database update
     *         statement.
     *
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     *
     */
    public boolean allowExecuteUpdate(String username, String database, Connection connection)
	    throws IOException, SQLException;

    /**
     * Allows to implement specific a Java rule immediately after a SQL statement
     * has been refused because one of the <code>SqlFirewallManager.allowXxx</code>
     * method returned false. <br>
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
     * @param username        the discarded client username
     * @param database        the database name as defined in the JDBC URL field
     * @param connection      The current SQL/JDBC <code>Connection</code>
     * @param ipAddress       the IP address of the client user
     * @param isMetadataQuery Says if the client request was an AceQL specific
     *                        Metadata Query API
     * @param sql             the SQL statement
     * @param parameterValues the parameter values of a prepared statement in the
     *                        natural order, empty list for a (non prepared)
     *                        statement
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     */
    public void runIfStatementRefused(String username, String database, Connection connection, String ipAddress,
	    boolean isMetadataQuery, String sql, List<Object> parameterValues) throws IOException, SQLException;
}
