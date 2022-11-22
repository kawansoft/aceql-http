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
package org.kawanfw.sql.servlet.connection;

import java.sql.Connection;

/**
 * 
 * @author Nicolas de Pomereu
 *
 */
public class ConnectionIdUtil {

    private static final String STATELESS = "stateless";

    /**
     * A simple wrapper for connection.hashCode();
     * @param connection
     * @return connection.hashCode() in String value
     */
    public static String getConnectionId(Connection connection) {
        return "" + connection.hashCode();
    }

    /**
     * Returns the Connection Id value for a stateless Connection
     * @return the Connection Id value for a stateless Connection
     */
    public static String getStatelessConnectionId() {
	return STATELESS;
    }

}
