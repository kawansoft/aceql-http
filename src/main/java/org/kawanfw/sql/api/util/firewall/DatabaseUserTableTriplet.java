/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2020,  KawanSoft SAS
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
package org.kawanfw.sql.api.util.firewall;

/**
 * A triplet that allows to identify a CSV rule of CsvRulesManager SQL Firewall Manager.
 * @author Nicolas de Pomereu
 *
 */
public class DatabaseUserTableTriplet implements Comparable<DatabaseUserTableTriplet> {

    private String database = null;
    private String username = null;
    private String table = null;

    /**
     * Constructor.
     * @param database
     * @param username
     * @param table
     */
    public DatabaseUserTableTriplet(String database, String username, String table) {

	if (database == null) {
	    throw new NullPointerException("database is null!");
	}
	if (username == null) {
	    throw new NullPointerException("username is null!");
	}
	if (table == null) {
	    throw new NullPointerException("table is null!");
	}

	this.database = database;
	this.username = username;
	this.table = table;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getTable() {
        return table;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((database == null) ? 0 : database.hashCode());
	result = prime * result + ((table == null) ? 0 : table.hashCode());
	result = prime * result + ((username == null) ? 0 : username.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	DatabaseUserTableTriplet other = (DatabaseUserTableTriplet) obj;
	if (database == null) {
	    if (other.database != null)
		return false;
	} else if (!database.equals(other.database))
	    return false;
	if (table == null) {
	    if (other.table != null)
		return false;
	} else if (!table.equals(other.table))
	    return false;
	if (username == null) {
	    if (other.username != null)
		return false;
	} else if (!username.equals(other.username))
	    return false;
	return true;
    }

    @Override
    public int compareTo(DatabaseUserTableTriplet other) {
	return this.toString().compareTo(other.toString());
    }

    @Override
    public String toString() {
	return "DatabaseUserTableTriplet [database=" + database + ", username=" + username + ", table=" + table + "]";
    }


}
