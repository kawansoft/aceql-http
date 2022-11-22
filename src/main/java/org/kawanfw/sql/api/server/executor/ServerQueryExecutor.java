/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.api.server.executor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation of this interface allow client side to call a server side
 * programmed class that returns a {@code ResultSet}. <br>
 * This is a a kind of <i>AceQL stored procedure</i> written in Java. <br>
 * 
 * @author Nicolas de Pomereu
 * @since 10.1
 *
 */
public interface ServerQueryExecutor {

    /**
     * Executes a query and returns {@code ResultSet} for the client-side.
     * 
     * @param clientEvent contains all info about the request asked by the client
     *                    side
     * @param connection  the current SQL/JDBC <code>Connection</code>.
     * @return a <code>ResultSet</code> object that contains the data produced by
     *         the query; never <code>null</code>
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     */
    public ResultSet executeQuery(ClientEvent clientEvent, Connection connection) throws IOException, SQLException;

}
