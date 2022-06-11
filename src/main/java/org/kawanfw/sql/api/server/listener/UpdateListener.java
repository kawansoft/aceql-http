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
