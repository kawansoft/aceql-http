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
	// Default implementation Does nothing for the sake of speed execution
    }

}
