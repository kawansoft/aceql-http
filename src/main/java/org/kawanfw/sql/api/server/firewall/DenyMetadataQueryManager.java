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

/**
 * Firewall manager that denies the use of the AceQL Metadata Query API.
 *
 * @author Nicolas de Pomereu
 * @since 4.0
 */
public class DenyMetadataQueryManager implements SqlFirewallManager {

    
    /**
     * @return <code><b>true</b></code>. No analysis is done so all SQL statements
     *         are authorized.
     */
    @Override
    public boolean allowSqlRunAfterAnalysis(SqlEvent sqlEvent, Connection connection) throws IOException, SQLException {
	return true;
    }
    
    /**
     * @return <code><b>false</b></code>. (Client programs will never allowed to
     *         call the Metadata Query API).
     */
    @Override
    public boolean allowMetadataQuery(String username, String database, Connection connection)
	    throws IOException, SQLException {
	return false;
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



}
