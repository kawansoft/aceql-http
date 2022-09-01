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
package org.kawanfw.sql.api.server.listener;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.kawanfw.sql.api.server.SqlEvent;

/**
 * Default implementation of {@code UpdateListener}. The
 * {@code updateActionPerformed(ClientEvent, Connection)}
 * does nothing for the sake of speed execution.
 * 
 * @author Nicolas de Pomereu
 * @since 9.0
 */
public class DefaultUpdateListener implements UpdateListener {

    /**
     * This default implementation does nothing for the sake of speed execution.
     */
    @Override
    public void updateActionPerformed(SqlEvent evt, Connection connection) throws IOException, SQLException {
	// Default implementation does nothing for the sake of speed execution
    }

}
