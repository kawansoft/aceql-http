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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Allows to get all details of a {@link ServerQueryExecutor} event asked by the client side.
 * 
 * @author Nicolas de Pomereu
 * @since 9.0
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
     * Returns the client username that asked for the SQL request
     * 
     * @return the client username that asked for the SQL request.
     */
    public String getUsername() {
	return username;
    }

    /**
     * Returns the database name
     * 
     * @return the database name.
     */
    public String getDatabase() {
	return database;
    }

    /**
     * Returns the ipAddress of the client user
     * 
     * @return the ipAddress of the client user.
     */
    public String getIpAddress() {
	return ipAddress;
    }

     /**
     * Returns the parameter values
     * 
     * @return the parameter values
     */
    public List<Object> getParameterValues() {
	return parameterValues;
    }

    /**
     * Returns the parameter String values
     * 
     * @return the parameter String values
     */
    public List<String> getParameterStringValues() {
	return toString(parameterValues);
    }

    
    @Override
    public String toString() {
	return "ClientEvent [username=" + username + ", database=" + database + ", ipAddress=" + ipAddress
		+ ", parameterValues=" + parameterValues + "]";
    }

    /**
     * Transforms the Object parameters values into strings
     * 
     * @param parameterValues the Object parameter values
     * @return the converted String parameter values
     */
    private List<String> toString(List<Object> parameterValues) {
	List<String> list = new ArrayList<>();
	for (Object object : parameterValues) {
	    list.add(String.valueOf(object));
	}
	return list;
    }

}
