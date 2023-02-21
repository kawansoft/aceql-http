/*
 * Copyright (c)2023 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-02-21
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.api.util.firewall;

import java.util.Objects;

/**
 * Stores the allowed statements of a table for an username.
 *
 * @author Nicolas de Pomereu
 *
 */
public class TableAllowStatements implements Comparable<TableAllowStatements> {

    // Says for each statement if it is allows
    private String database = null;
    private String username = null;
    private String table = null;
    private boolean delete = false;
    private boolean insert = false;
    private boolean select = false;
    private boolean update = false;

    /**
     * Constructor.
     *
     * @param database    the database name.
     * @param username    the username for the rule.
     * @param table       the table name for the rule.
     * @param elete if true, delete will be allowed on table.
     * @param insert if true, insert will be allowed on table.
     * @param select if true, select will be allowed on table.
     * @param update if true, update will be allowed on table.
     */
    public TableAllowStatements(String database, String username, String table, boolean delete,
	    boolean insert, boolean select, boolean update) {

	this.database = Objects.requireNonNull(database, "database cannot be null!");
	this.username = Objects.requireNonNull(username, "username cannot be null!");
	this.table = Objects.requireNonNull(table, "table cannot be null!");

	this.delete = delete;
	this.insert = insert;
	this.select = select;
	this.update = update;
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

    public boolean isDeleteAllowed() {
        return delete;
    }

    public boolean isInsertAllowe() {
        return insert;
    }

    public boolean isSelectAllowed() {
        return select;
    }

    public boolean isUpdateAllowed() {
        return update;
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
	TableAllowStatements other = (TableAllowStatements) obj;
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
    public int compareTo(TableAllowStatements other) {
	return this.toString().compareTo(other.toString());
    }

    @Override
    public String toString() {
	return "[database=" + database + ", username=" + username + ", table=" + table
		+ ", delete=" + delete + ", insert=" + insert + ", select=" + select
		+ ", update=" + update + "]";
    }
}
