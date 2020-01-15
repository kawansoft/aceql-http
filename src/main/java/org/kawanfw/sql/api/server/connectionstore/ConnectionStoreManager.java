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
package org.kawanfw.sql.api.server.connectionstore;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.servlet.connection.ConnectionStore;

/**
 *
 * Class that allows to manage the server Connection Store that stores in memory
 * the JDBC Connections of the client users during their session.
 * <p>
 * Class allows to:
 * <ul>
 * <li>Retrieve all the {@link ConnectionKey} of the Connection Store.</li>
 * <li>Retrieve the age of JDBC <code>Connection</code> stored in the Connection
 * Store.</li>
 * <li>Remove a JDBC <code>Connection</code> from the Connection Store and
 * release it into the connection pool.</li>
 * </ul>
 *
 * @author Nicolas de Pomereu
 * @since 1.0
 */

public class ConnectionStoreManager {

    /**
     * Protected Constructor
     */
    protected ConnectionStoreManager() {

    }

    /**
     * Returns the keys of the Connection Store.
     *
     * @return the keys of the Connection Store
     */
    public static Set<ConnectionKey> getKeys() {
	return ConnectionStore.getKeys();
    }

    /**
     * Removes from the Connection Store a JDBC {@code Connection} identified by
     * a {@link ConnectionKey} and release it in the connection pool.
     *
     * @param connectionKey
     *            the key of the Connection Store
     * @param databaseConfigurator
     *            the SQL Configurator
     * @throws SQLException if any SQL Exception
     */
    public static void remove(ConnectionKey connectionKey,
	    DatabaseConfigurator databaseConfigurator) throws SQLException {

	if (connectionKey == null) {
	    throw new IllegalArgumentException("connectionKey is null!");
	}

	if (databaseConfigurator == null) {
	    throw new IllegalArgumentException("databaseConfigurator is null!");
	}

	ConnectionStore connectionStore = new ConnectionStore(
		connectionKey.getUsername(), connectionKey.getSessionId(), connectionKey.getConnectionId());

	// Get the Connection before removing it from the store..
	Connection connection = connectionStore.get();

	// Release the Connection into the pool
	connectionStore.remove();
	databaseConfigurator.close(connection);
	//ConnectionCloser.freeConnection(connection, databaseConfigurator);

    }

    /**
     * Returns the size of the Connection Store
     *
     * @return the size of the Connection Store
     */
    public static int size() {
	return ConnectionStore.getKeys().size();
    }

}
