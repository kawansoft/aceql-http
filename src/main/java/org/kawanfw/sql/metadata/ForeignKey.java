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
package org.kawanfw.sql.metadata;

import java.sql.DatabaseMetaData;

/**
 * Metadata object that wraps the result of:
 * {@link DatabaseMetaData#getExportedKeys(String, String, String)} and:
 * {@link DatabaseMetaData#getImportedKeys(String, String, String)}
 *
 * @author Nicolas de Pomereu
 *
 */
public class ForeignKey extends CatalogAndSchema {

    public static final String importedKeyCascade = "importedKeyCascade";
    public static final String importedKeyRestrict = "importedKeyRestrict";
    public static final String importedKeySetNull = "importedKeySetNull";
    public static final String importedKeyNoAction = "importedKeyNoAction";
    public static final String importedKeySetDefault = "importedKeySetDefault";
    public static final String importedKeyInitiallyDeferred = "importedKeyInitiallyDeferred";
    public static final String importedKeyInitiallyImmediate = "importedKeyInitiallyImmediate";
    public static final String importedKeyNotDeferrable = "importedKeyNotDeferrable";

    /**
     * <code><pre>
        1.PKTABLE_CAT String => primary key table catalog (may be null)
        2.PKTABLE_SCHEM String => primary key table schema (may be null)
        3.PKTABLE_NAME String => primary key table name
        4.PKCOLUMN_NAME String => primary key column name
        5.FKTABLE_CAT String => foreign key table catalog (may be null)being exported (may be null)
        6.FKTABLE_SCHEM String => foreign key table schema (may be null)being exported (may be null)
        7.FKTABLE_NAME String => foreign key table namebeing exported
        8.FKCOLUMN_NAME String => foreign key column namebeing exported
        9.KEY_SEQ short => sequence number within foreign key( a valueof 1 represents the first column of the foreign key, a value of 2 wouldrepresent the second column within the foreign key).
        10.UPDATE_RULE short => What happens toforeign key when primary is updated: ◦ importedNoAction - do not allow update of primarykey if it has been imported
        ◦ importedKeyCascade - change imported key to agreewith primary key update
        ◦ importedKeySetNull - change imported key to NULL ifits primary key has been updated
        ◦ importedKeySetDefault - change imported key to default valuesif its primary key has been updated
        ◦ importedKeyRestrict - same as importedKeyNoAction(for ODBC 2.x compatibility)

        11.DELETE_RULE short => What happens tothe foreign key when primary is deleted. ◦ importedKeyNoAction - do not allow delete of primarykey if it has been imported
        ◦ importedKeyCascade - delete rows that import a deleted key
        ◦ importedKeySetNull - change imported key to NULL ifits primary key has been deleted
        ◦ importedKeyRestrict - same as importedKeyNoAction(for ODBC 2.x compatibility)
        ◦ importedKeySetDefault - change imported key to default ifits primary key has been deleted

        12.FK_NAME String => foreign key name (may be null)
        13.PK_NAME String => primary key name (may be null)
        14.DEFERRABILITY short => can the evaluation of foreign keyconstraints be deferred until commit ◦ importedKeyInitiallyDeferred - see SQL92 for definition
        ◦ importedKeyInitiallyImmediate - see SQL92 for definition
        ◦ importedKeyNotDeferrable - see SQL92 for definition
        </code>
     * </pre>
     */

    private String primaryKeyTable = null;
    private String primaryKeyColumn = null;
    private String foreignKeyCatalog = null;
    private String foreignKeySchema = null;
    private String foreignKeyTable = null;
    private String foreignKeyColumn = null;
    private int keySequence = 0;
    private String updateRule = null;
    private String deleteRule = null;
    private String foreignKeyName = null;
    private String primaryKeyName = null;
    private int deferrability = 0;

    public String getPrimaryKeyTable() {
	return primaryKeyTable;
    }

    public String getPrimaryKeyColumn() {
	return primaryKeyColumn;
    }

    public String getForeignKeyCatalog() {
	return foreignKeyCatalog;
    }

    public String getForeignKeySchema() {
	return foreignKeySchema;
    }

    public String getForeignKeyTable() {
	return foreignKeyTable;
    }

    public String getForeignKeyColumn() {
	return foreignKeyColumn;
    }

    public int getKeySequence() {
	return keySequence;
    }

    public String getUpdateRule() {
	return updateRule;
    }

    public String getDeleteRule() {
	return deleteRule;
    }

    public String getForeignKeyName() {
	return foreignKeyName;
    }

    public String getPrimaryKeyName() {
	return primaryKeyName;
    }

    public int getDeferrability() {
	return deferrability;
    }

    void setPrimaryKeyTable(String primaryKeyTable) {
	this.primaryKeyTable = primaryKeyTable;
    }

    void setPrimaryKeyColumn(String primaryKeyColumn) {
	this.primaryKeyColumn = primaryKeyColumn;
    }

    void setForeignKeyCatalog(String foreignKeyCatalog) {
	this.foreignKeyCatalog = foreignKeyCatalog;
    }

    void setForeignKeySchema(String foreignKeySchema) {
	this.foreignKeySchema = foreignKeySchema;
    }

    void setForeignKeyTable(String foreignKeyTable) {
	this.foreignKeyTable = foreignKeyTable;
    }

    void setForeignKeyColumn(String foreignKeyColumn) {
	this.foreignKeyColumn = foreignKeyColumn;
    }

    void setKeySequence(int keySequence) {
	this.keySequence = keySequence;
    }

    void setUpdateRule(String updateRule) {
	this.updateRule = updateRule;
    }

    void setDeleteRule(String deleteRule) {
	this.deleteRule = deleteRule;
    }

    void setForeignKeyName(String foreignKeyName) {
	this.foreignKeyName = foreignKeyName;
    }

    void setPrimaryKeyName(String primaryKeyName) {
	this.primaryKeyName = primaryKeyName;
    }

    void setDeferrability(int deferrability) {
	this.deferrability = deferrability;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((foreignKeyName == null) ? 0 : foreignKeyName.hashCode());
	result = prime * result + ((foreignKeyTable == null) ? 0 : foreignKeyTable.hashCode());
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
	ForeignKey other = (ForeignKey) obj;
	if (foreignKeyName == null) {
	    if (other.foreignKeyName != null)
		return false;
	} else if (!foreignKeyName.equals(other.foreignKeyName))
	    return false;
	if (foreignKeyTable == null) {
	    if (other.foreignKeyTable != null)
		return false;
	} else if (!foreignKeyTable.equals(other.foreignKeyTable))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "ForeignKey [primaryKeyTable=" + primaryKeyTable + ", primaryKeyColumn=" + primaryKeyColumn
		+ ", foreignKeyCatalog=" + foreignKeyCatalog + ", foreignKeySchema=" + foreignKeySchema
		+ ", foreignKeyTable=" + foreignKeyTable + ", foreignKeyColumn=" + foreignKeyColumn + ", keySequence="
		+ keySequence + ", updateRule=" + updateRule + ", deleteRule=" + deleteRule + ", foreignKeyName="
		+ foreignKeyName + ", primaryKeyName=" + primaryKeyName + ", deferrability=" + deferrability
		+ ", getCatalog()=" + getCatalog() + ", getSchema()=" + getSchema() + "]";
    }

}
