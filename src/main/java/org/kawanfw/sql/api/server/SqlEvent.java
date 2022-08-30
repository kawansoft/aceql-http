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
package org.kawanfw.sql.api.server;

import java.util.List;
import java.util.Objects;

import org.kawanfw.sql.util.SqlEventUtil;

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
     * @param isMetadataQuery	  says if the client request was an AceQL specific
     *                        	  Metadata Query API
     */
    SqlEvent(String username, String database, String ipAddress, String sql, boolean isPreparedStatement,
	    List<Object> parameterValues, boolean isMetadataQuery) {
	this.username = Objects.requireNonNull(username, "username cannnot be null!");
	this.database = Objects.requireNonNull(database, "database cannnot be null!");
	this.ipAddress = Objects.requireNonNull(ipAddress, "ipAddress cannnot be null!");
	this.sql = Objects.requireNonNull(sql, "sql cannnot be null!");
	this.isPreparedStatement = isPreparedStatement;
	this.parameterValues = Objects.requireNonNull(parameterValues, "parameterValues cannnot be null!");
	this.isMetadataQuery =  isMetadataQuery;
    }

    /**
     * Returns the client username that asked for the SQL request.
     * 
     * @return the client username that asked for the SQL request
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
     * Returns the  IP address of the client user.
     * 
     * @return the IP address of the client user
     */
    public String getIpAddress() {
	return ipAddress;
    }

    /**
     * Returns the SQL statement to execute.
     * 
     * @return the SQL statement to execute
     */
    public String getSql() {
	return sql;
    }

    /**
     * Returns {@code true} if the statement is a {@code PreparedStatement}, else
     * {@code false}.
     * 
     * @return {@code true} if the statement is a {@code PreparedStatement}, else
     *         {@code false}
     */
    public boolean isPreparedStatement() {
	return isPreparedStatement;
    }

    /**
     * Returns the parameter values of a prepared statement in the natural order,
     * empty list for a (non prepared) statement.
     * 
     * @return the parameter values of a prepared statement in the natural order,
     *         empty list for a (non prepared) statement
     */
    public List<Object> getParameterValues() {
	return parameterValues;
    }

    /**
     * Returns the parameter String values of a prepared statement in the natural
     * order, empty list for a (non prepared) statement.
     * 
     * @return the parameter String values of a prepared statement in the natural
     *         order, empty list for a (non prepared) statement
     */
    public List<String> getParameterStringValues() {
	return SqlEventUtil.toString(parameterValues);
    }

    
    /**
     * Says if the SQL event is a special Metadata query.
     * @return {@code true} if the SQL event is a special Metadata query, else {@code false}
     */
    public boolean isMetadataQuery() {
        return isMetadataQuery;
    }

    @Override
    public String toString() {
	return "ClientEvent [username=" + username + ", database=" + database + ", ipAddress=" + ipAddress + ", sql=" + sql
		+ ", isPreparedStatement=" + isPreparedStatement + ", parameterValues=" + SqlEventUtil.toString(parameterValues) 
		+ ", isMetadataQuery=" + isMetadataQuery + "]";
    }

}
