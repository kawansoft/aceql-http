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
package org.kawanfw.sql.api.server.executor;

import java.util.List;
import java.util.Objects;

import org.kawanfw.sql.util.SqlEventUtil;

/**
 * Allows to get all details of a {@link ServerQueryExecutor} call asked by the client side.
 * 
 * @author Nicolas de Pomereu
 * @since 9.1
 */
public class ClientEvent {

    private String username;
    private String database;
    private String ipAddress;
    private List<Object> parameterValues;
    
    /**
     * Package protected constructor.
     * 
     * @param username            the client username that asked for the SQL request
     * @param database            the database name as defined in the JDBC URL field
     * @param ipAddress           the IP address of the client user
     * @param parameterValues     the parameter values sent by the client side
     */
    ClientEvent(String username, String database, String ipAddress, List<Object> parameterValues) {
	this.username = Objects.requireNonNull(username, "username cannnot be null!");
	this.database = Objects.requireNonNull(database, "database cannnot be null!");
	this.ipAddress = Objects.requireNonNull(ipAddress, "ipAddress cannnot be null!");
	this.parameterValues = Objects.requireNonNull(parameterValues, "parameterValues cannnot be null!");
    }

    /**
     * Returns the client username that asked to execute the server query.
     * 
     * @return the client username that asked to execute the server query
     */
    public String getUsername() {
	return username;
    }

    /**
     * Returns the database name.
     * 
     * @return the database name
     */
    public String getDatabase() {
	return database;
    }

    /**
     * Returns the IP address of the client user.
     * 
     * @return the ipAddress of the client user
     */
    public String getIpAddress() {
	return ipAddress;
    }

     /**
     * Returns the parameter values to pass to the server query.
     * 
     * @return the parameter values to pass to the server query
     */
    public List<Object> getParameterValues() {
	return parameterValues;
    }

    /**
     * Returns the parameter String values.
     * 
     * @return the parameter String values
     */
    public List<String> getParameterStringValues() {
	return SqlEventUtil.toString(parameterValues);
    }

    
    @Override
    public String toString() {
	return "ClientEvent [username=" + username + ", database=" + database + ", ipAddress=" + ipAddress
		+ ", parameterValues=" + SqlEventUtil.toString(parameterValues) + "]";
    }

}
