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
package org.kawanfw.sql.servlet.sql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.SqlEventWrapper;
import org.kawanfw.sql.api.server.listener.DefaultUpdateListener;
import org.kawanfw.sql.api.server.listener.UpdateListener;

/**
 * Calls all UpdateListener instances.
 * @author Nicolas de Pomereu
 *
 */
public class UpdateListenersCaller {

    private List<UpdateListener> updateListeners;
    private Connection connection;


    /**
     * Constructor
     * @param updateListeners	the list of UpdateListener to call
     * @param connection	the JDBC Connection
     */
    public UpdateListenersCaller(List<UpdateListener> updateListeners, Connection connection) {
	this.updateListeners = updateListeners;
	this.connection = connection;
    }

    /**
     * Call the UpdateListener updateActionPerformed method
     * 
     * @param username
     * @param database
     * @param sqlOrder
     * @param ipAddress
     * @param isPreparedStatement               
     * @param serverPreparedStatementParameters
     * @throws SQLException
     * @throws IOException
     */
    public void callUpdateListeners(String username, String database, String sqlOrder, List<Object> parameterValues,
	    String ipAddress, boolean isPreparedStatement) throws SQLException, IOException {
	if (updateListeners.size() != 1 || !(updateListeners.get(0) instanceof DefaultUpdateListener)) {
	    SqlEvent sqlEvent = SqlEventWrapper.sqlEventBuild(username, database, ipAddress, sqlOrder,
		    isPreparedStatement, parameterValues, false);
	    for (UpdateListener updateListener : updateListeners) {
		updateListener.updateActionPerformed(sqlEvent, connection);
	    }
	}
    }

}
