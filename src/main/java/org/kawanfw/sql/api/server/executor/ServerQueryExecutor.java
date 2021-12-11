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

package org.kawanfw.sql.api.server.executor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementation of this interface allow client side to call a server side
 * programmed class that returns a {@code ResultSet}. 
 * <br>This is a a kind of
 * <i>AceQL stored procedure</i> written in Java. <br>
 * 
 * @author Nicolas de Pomereu
 * @since 9.1
 *
 */
public interface ServerQueryExecutor {

    /**
     * Executes a query and returns {@code ResultSet} for the client-side
     * 
     * @param username   the client username that asks for the query. Allows to
     *                   perform security checks.
     * @param database   the database name as defined in the JDBC URL field. Allows
     *                   to perform security checks.
     * @param connection The current SQL/JDBC <code>Connection</code>.
     * @param ipAddress  the IP address of the client user. Allows to perform
     *                   security checks.
     * @param args       the parameters list passed by the client side.
     * @return a <code>ResultSet</code> object that contains the data produced by
     *         the query; never <code>null</code>
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     */
    public ResultSet executeQuery(String username, String database, Connection connection, String ipAddress,
	    List<Object> params) throws IOException, SQLException;

}
