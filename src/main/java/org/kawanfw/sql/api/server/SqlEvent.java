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
package org.kawanfw.sql.api.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Allows to get all details of a SQL event asked by the client side.
 * 
 * @author Nicolas de Pomereu
 * @since 9.0
 */
public class SqlEvent {

    private String username;
    private String database;
    private String ipAddress;
    private String sql;
    private boolean isPreparedStatement;
    private List<Object> parameterValues;
    private boolean isMetadataQuery;
    
    /**
     * Package protected constructor.
     * 
     * @param username            the client username that asked for the SQL request
     * @param database            the database name as defined in the JDBC URL field
     * @param ipAddress           the IP address of the client user
     * @param sql                 the SQL statement
     * @param isPreparedStatement says if the statement is a prepared statement
     * @param parameterValues     the parameter values of a prepared statement in
     *                            the natural order, empty list for a (non prepared)
     *                            statement
     * @param isMetadataQuery	  Says if the client request was an AceQL specific
     *                        	   Metadata Query API
     */
    SqlEvent(String username, String database, String ipAddress, String sql, boolean isPreparedStatement,
	    List<Object> parameterValues, boolean isMetadataQuery) {
	this.username = Objects.requireNonNull(username, "username cannnot be null!");
	this.database = Objects.requireNonNull(database, "database cannnot be null!");
	this.ipAddress = Objects.requireNonNull(ipAddress, "ipAddress cannnot be null!");
	this.sql = Objects.requireNonNull(sql, "sql cannnot be null!");
	this.isPreparedStatement = isPreparedStatement;
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
     * Returns the sql statement to execute.
     * 
     * @return the sql statement to execute.
     */
    public String getSql() {
	return sql;
    }

    /**
     * Returns {@code true} if the statement is a {@code PreparedStatement}, else
     * {@code false}.
     * 
     * @return {@code true} if the statement is a {@code PreparedStatement}, else
     *         {@code false}.
     */
    public boolean isPreparedStatement() {
	return isPreparedStatement;
    }

    /**
     * Returns the parameter values of a prepared statement in the natural order,
     * empty list for a (non prepared) statement
     * 
     * @return the parameter values of a prepared statement in the natural order,
     *         empty list for a (non prepared) statement
     */
    public List<Object> getParameterValues() {
	return parameterValues;
    }

    /**
     * Returns the parameter String values of a prepared statement in the natural
     * order, empty list for a (non prepared) statement
     * 
     * @return the parameter String values of a prepared statement in the natural
     *         order, empty list for a (non prepared) statement
     */
    public List<String> getParameterStringValues() {
	return toString(parameterValues);
    }

    
    /**
     * Says if the SQL event is a special Metadata query
     * @return {@code true} if the SQL event is a special Metadata query, else {@code false}
     */
    public boolean isMetadataQuery() {
        return isMetadataQuery;
    }

    @Override
    public String toString() {
	return "SqlEvent [username=" + username + ", database=" + database + ", ipAddress=" + ipAddress + ", sql=" + sql
		+ ", isPreparedStatement=" + isPreparedStatement + ", parameterValues=" + parameterValues
		+ ", isMetadataQuery=" + isMetadataQuery + "]";
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
