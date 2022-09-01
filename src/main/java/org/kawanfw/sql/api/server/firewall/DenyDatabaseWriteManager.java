/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.api.server.firewall;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.StatementAnalyzer;

/**
 * Firewall manager that denies any update of the database for the passed user.
 * The database is thus guaranteed to be accessed in read only from client side.
 * <br>
 * {@code DenyDatabaseWriteManager} should be used only in order to monitor
 * users who try to force writes on database. <br>
 * If you don't need to monitor users and detect hackers, it's better to set the
 * property {@code database.defaultReadOnly=true} in the
 * {@code aceql-server.properties} file: it will launch a
 * {@link Connection#setReadOnly(boolean)} JDBC call at server startup that will
 *  write-protect efficiently the SQL database.
 *
 * @author Nicolas de Pomereu
 * @since 11.0
 */
public class DenyDatabaseWriteManager implements SqlFirewallManager {

    /**
     * @return <code>false</code> if the passed SQL statement tries to update the
     *         database, else <code>true</code>
     */
    @Override
    public boolean allowSqlRunAfterAnalysis(SqlEvent sqlEvent, Connection connection) throws IOException, SQLException {
	StatementAnalyzer analyzer = new StatementAnalyzer(sqlEvent.getSql(), sqlEvent.getParameterValues());
	return !(analyzer.isDelete() || analyzer.isInsert() || analyzer.isUpdate() || analyzer.isDcl()
		|| analyzer.isDdl() || analyzer.isTcl());
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
     * @return <code><b>true</b></code>. (Client programs will be allowed to call
     *         the Metadata Query API).
     */
    @Override
    public boolean allowMetadataQuery(String username, String database, Connection connection)
	    throws IOException, SQLException {
	return true;
    }
}
