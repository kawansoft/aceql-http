/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.api.util.firewall;

import java.util.Objects;

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
	this.database = Objects.requireNonNull(database, "database cannot be null!");
	this.username = Objects.requireNonNull(username, "username cannot be null!");
	this.table = Objects.requireNonNull(table, "table cannot be null!");
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
