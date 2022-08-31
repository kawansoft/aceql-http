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
package org.kawanfw.sql.metadata;

import java.sql.DatabaseMetaData;

/**
 * Metadata object that wraps the result of {@link DatabaseMetaData#getPrimaryKeys(String, String, String)}
 * @author Nicolas de Pomereu
 */

public class PrimaryKey extends CatalogAndSchema {

    /**
     * <pre><code>
	1.TABLE_CAT String => table catalog (may be null)
	2.TABLE_SCHEM String => table schema (may be null)
	3.TABLE_NAME String => table name
	4.COLUMN_NAME String => column name
	5.KEY_SEQ short => sequence number within primary key( a valueof 1 represents the first column of the primary key, a value of 2 wouldrepresent the second column within the primary key).
	6.PK_NAME String => primary key name (may be null)

        databaseMetaData.getPrimaryKeys( user_login) 1: null
        databaseMetaData.getPrimaryKeys( user_login) 2: public
        databaseMetaData.getPrimaryKeys( user_login) 3: user_login		table
        databaseMetaData.getPrimaryKeys( user_login) 4: username		column
        databaseMetaData.getPrimaryKeys( user_login) 5: 1			key sequence
        databaseMetaData.getPrimaryKeys( user_login) 6: user_login_pkey		primary key name
      </code></pre>
     */

    private String tableName = null;
    private String columnName = null;
    private int keySequence = 0;
    private String primaryKeyName = null;

    public String getTableName() {
        return tableName;
    }
    void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public String getColumnName() {
        return columnName;
    }
    void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    public int getKeySequence() {
        return keySequence;
    }
    void setKeySequence(int keySequence) {
        this.keySequence = keySequence;
    }
    public String getPrimaryKeyName() {
        return primaryKeyName;
    }
    void setPrimaryKeyName(String primaryKeyName) {
        this.primaryKeyName = primaryKeyName;
    }
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((primaryKeyName == null) ? 0 : primaryKeyName.hashCode());
	result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
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
	PrimaryKey other = (PrimaryKey) obj;
	if (primaryKeyName == null) {
	    if (other.primaryKeyName != null)
		return false;
	} else if (!primaryKeyName.equals(other.primaryKeyName))
	    return false;
	if (tableName == null) {
	    if (other.tableName != null)
		return false;
	} else if (!tableName.equals(other.tableName))
	    return false;
	return true;
    }
    @Override
    public String toString() {
	return "PrimaryKey [tableName=" + tableName + ", columnName=" + columnName + ", keySequence=" + keySequence
		+ ", primaryKeyName=" + primaryKeyName + ", getCatalog()=" + getCatalog() + ", getSchema()="
		+ getSchema() + "]";
    }


}
