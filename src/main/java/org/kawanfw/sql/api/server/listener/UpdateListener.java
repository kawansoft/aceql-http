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
 * The listener interface for receiving SQL update events.
 * <br><br>
 * Concrete implementations are defined in the {@code aceql-server.properties}
 * file. <br><br>
 * Note that the framework comes with a default <code>DefaultUpdateListener</code>
 * implementation that does nothing.
 * <p>
 * A built in and ready to use class that doesn't require any coding is included:
 * <ul>
 * <li>{@link JsonLoggerUpdateListener}: a listener that insert in logs using JSON format
 * the {@code SqlEvent}.</li>
 * </ul>
 * 
 * Multiple {@code UpdateListener} may be defined and chained in property
 * value by separating class names by a comma. <br>
 * When {@code UpdateListener} classes are chained, all of them are
 * successively executed in the declared order. 
 * 
 * @author Nicolas de Pomereu
 * @since 9.0
 *
 */
public interface UpdateListener {

    /**
     * Invoked when a SQL update event is successfully executed.
     * 
     * @param sqlEvent   the SQL update event that is successfully processed
     * @param connection the Connection in use for the SQL update event
     * 
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     */
    public void updateActionPerformed(SqlEvent sqlEvent, Connection connection) throws IOException, SQLException;
}
