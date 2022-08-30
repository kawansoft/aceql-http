/*
 * Copyright (c)2022 KawanSoft S.A.S.
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-30
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
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
