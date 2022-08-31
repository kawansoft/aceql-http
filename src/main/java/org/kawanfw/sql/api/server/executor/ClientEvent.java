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