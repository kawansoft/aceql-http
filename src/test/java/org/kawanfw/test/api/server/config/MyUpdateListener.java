/*
 * Copyright (c)2023 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-02-21
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.test.api.server.config;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.listener.UpdateListener;

/**
 * Concrete implementation of {@code UpdateListener}. The
 * {@code updateActionPerformed(ClientEvent, Connection)} logs
 * {@code ClientEvent} on stdout.
 * 
 * @author Nicolas de Pomereu
 * @since 9.0
 */

public class MyUpdateListener implements UpdateListener {

    /**
     * Prints all successful SQL updates main info on stdout
     */
    @Override
    public void updateActionPerformed(SqlEvent sqlEvent, Connection connection) throws IOException, SQLException {

	System.out.println(new Date() + " client username: " + sqlEvent.getUsername() + " database: " + sqlEvent.getDatabase()
		+ " SQL statement:  " + sqlEvent.getSql() + " user IP address: " + sqlEvent.getIpAddress());
    }

}
