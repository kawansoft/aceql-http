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
import java.sql.Statement;

import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.StatementAnalyzer;

/**
 * Interface that allows to define firewall rules for AceQL HTTP SQL calls. <br>
 * <br>
 * Concrete implementations are defined in the {@code aceql-server.properties}
 * file. <br>
 * <br>
 * A concrete implementation should be developed on the server side in order to:
 * <ul>
 * <li>Define a specific piece of Java code to analyze the source code of the
 * SQL statement before allowing or not it's execution.</li>
 * <li>Define if a client user has the right to call a
 * <code>Statement.execute</code> (i.e. call a raw execute).</li>
 * <li>Define if a client user has the right to call a raw
 * <code>Statement</code> that is not a <code>PreparedStatement</code>.</li>
 * <li>Define if a client user has the right to call a the AceQL Metadata
 * API.</li>
 * </ul>
 * <p>
 * Multiple {@code SqlFirewallManager} may be defined and chained. <br>
 * When {@code SqlFirewallManager} classes are chained, an {@code AND} condition
 * is applied to all the SqlFirewallManager execution conditions in order to
 * compute final allow. <br>
 * For example, the {@code allowExecuteUpdate()} of each chained
 * {@code SqlFirewallManager} instance must return true in order to allow
 * updates of the database.
 * <p>
 * Built in and ready to use classes that don't require any coding are included.
 * The classes may be chained. See each Javadoc for more details:
 * <ul>
 * <li>{@link CsvRulesManager}: manager that apply rules written in a CSV
 * file.</li>
 * <li>{@link CsvRulesManagerNoReload}: same as {@code CsvRulesManager}, but
 * dynamic reload of rules is prohibited if the CSV file is updated.</li>
 * <li>{@link DenyDatabaseWriteManager}: manager that denies any update of the
 * database.</li>
 * <li>{@link DenyDclManager}: manager that denies any DCL (Data Control
 * Language) call.</li>
 * <li>{@link DenyDdlManager}: manager that denies any DDL (Data Definition
 * Language) call.</li>
 * <li>{@link DenyExceptOnWhitelistManager}: manager that allows only statements
 * that are listed in a whitelist text file.</li>
 * <li>{@link DenyMetadataQueryManager}: manager that denies the use of the
 * AceQL Metadata Query API.</li>
 * <li>{@link DenyOnBlacklistManager}: manager that denies statements that are
 * listed in a blacklist text file.</li>
 * <li>{@link DenySqlInjectionManager}: manager that allows detecting
 * SQL injection attacks, using
 * <a href="https://www.cloudmersive.com">Cloudmersive</a> third-party API.</li>
 * <li>{@link DenySqlInjectionManagerAsync}: version of
 * {@code DenySqlInjectionManager} that detects SQL injections asynchronously
 * for faster response time.</li>
 * <li>{@link DenyStatementClassManager}: manager that denies any call of the
 * raw Statement Java class. (Calling Statements without parameters is
 * forbidden).</li>
 * </ul>
 * <p>
 * TCL (Transaction Control Language) calls are always authorized.
 * <p>
 * Note that the helper class {@link StatementAnalyzer} allows to do some simple
 * tests on the SQL statement string representation.
 *
 * @author Nicolas de Pomereu
 * @since 4.1
 */

public interface SqlFirewallManager {

    /**
     * Allows to analyze the SQL call event asked by the client side and thus allow
     * or forbid the SQL execution on the server.<br>
     * If the analysis defined by the method returns false, the SQL statement won't
     * be executed.
     * 
     * @param sqlEvent   the SQL event asked by the client side. Contains all info
     *                   about the SQL call (client username, database name, IP
     *                   Address of the client, and SQL statement details)
     * @param connection The current SQL/JDBC <code>Connection</code>
     * @return <code>true</code> if the analyzed statement or prepared statement is
     *         validated and authorized to run, else <code>false</code>
     *         <p>
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     */
    public boolean allowSqlRunAfterAnalysis(SqlEvent sqlEvent, Connection connection) throws IOException, SQLException;

    /**
     * Allows to define if the passed username is allowed to create and use a
     * {@link Statement} instance that is not a <code>PreparedStatement</code>.
     *
     * @param username   the client username to check the rule for
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
     * Says if the username is allowed call the Metadata Query API for the passed
     * database.
     *
     * @param username   the client username to check the rule for
     * @param database   the database name as defined in the JDBC URL field
     * @param connection The current SQL/JDBC <code>Connection</code>
     * @return <code>true</code> if the user has the right to call the Metadata
     *         Query API, else <code>false</code>
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     */
    public boolean allowMetadataQuery(String username, String database, Connection connection)
	    throws IOException, SQLException;
}
