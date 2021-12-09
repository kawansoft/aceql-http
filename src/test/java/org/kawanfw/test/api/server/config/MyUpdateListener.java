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

package org.kawanfw.test.api.server.config;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.kawanfw.sql.api.server.listener.SqlActionEvent;
import org.kawanfw.sql.api.server.listener.UpdateListener;

/**
 * Concrete implementation of {@code UpdateListener}. The
 * {@code updateActionPerformed(SqlActionEvent, Connection)} logs
 * {@code SqlActionEvent} on stdout.
 * 
 * @author Nicolas de Pomereu
 * @since 9.0
 */

public class MyUpdateListener implements UpdateListener {

    /**
     * Prints all successful SQL updates main info on stdout
     */
    @Override
    public void updateActionPerformed(SqlActionEvent evt, Connection connection) throws IOException, SQLException {

	System.out.println(new Date() + " client username: " + evt.getUsername() + " database: " + evt.getDatabase()
		+ " SQL statement:  " + evt.getSql() + " user IP address: " + evt.getIpAddress());
    }

}
