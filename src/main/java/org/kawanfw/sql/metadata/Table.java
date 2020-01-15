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
import java.util.List;

/**
 * Metadata object that wraps the result of {@link DatabaseMetaData#getTables(String, String, String, String[])}.
 * @author Nicolas de Pomereu.
 */

public class Table {

	/**
	 * <pre>
	 * <code>
	// 	   1.TABLE_CAT String => table catalog (may be null)
	// 	   2.TABLE_SCHEM String => table schema (may be null)
	// 	   3.TABLE_NAME String => table name
	// 	   4.TABLE_TYPE String => table type. Typical types are "TABLE","VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY","LOCAL TEMPORARY", "ALIAS", "SYNONYM".
	// 	   5.REMARKS String => explanatory comment on the table
	// 	   6.TYPE_CAT String => the types catalog (may be null)
	// 	   7.TYPE_SCHEM String => the types schema (may be null)
	// 	   8.TYPE_NAME String => type name (may be null)
	// 	   9.SELF_REFERENCING_COL_NAME String => name of the designated "identifier" column of a typed table (may be null)
	// 	   10.REF_GENERATION String => specifies how values inSELF_REFERENCING_COL_NAME are created. Values are"SYSTEM", "USER", "DERIVED". (may be null)
	</code>
	 * </pre>
	 */

    public static final String TABLE = "TABLE";
    public static final String VIEW = "VIEW";

    private String tableName = null;
    private String tableType = null;
    private String remarks = null;

    private List<Column> columns = null;
    private List<PrimaryKey> primaryKeys = null;
    private List<Index> indexes = null;

    private List<ImportedKey> importedforeignKeys = null;
    private List<ExportedKey> exportedforeignKeys = null;

    private String catalog = null;
    private String schema = null;

    public String getTableName() {
        return tableName;
    }
    public String getTableType() {
        return tableType;
    }
    public String getRemarks() {
        return remarks;
    }
    public List<Column> getColumns() {
        return columns;
    }
    public List<PrimaryKey> getPrimaryKeys() {
        return primaryKeys;
    }
    public List<Index> getIndexes() {
        return indexes;
    }
    public List<ImportedKey> getImportedforeignKeys() {
        return importedforeignKeys;
    }
    public List<ExportedKey> getExportedforeignKeys() {
        return exportedforeignKeys;
    }
    public String getCatalog() {
        return catalog;
    }
    public String getSchema() {
        return schema;
    }
    void setTableName(String tableName) {
        this.tableName = tableName;
    }
    void setTableType(String tableType) {
        this.tableType = tableType;
    }
    void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    void setColumns(List<Column> columns) {
        this.columns = columns;
    }
    void setPrimaryKeys(List<PrimaryKey> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }
    void setIndexes(List<Index> indexes) {
        this.indexes = indexes;
    }
    void setImportedforeignKeys(List<ImportedKey> importedforeignKeys) {
        this.importedforeignKeys = importedforeignKeys;
    }
    void setExportedforeignKeys(List<ExportedKey> exportedforeignKeys) {
        this.exportedforeignKeys = exportedforeignKeys;
    }
    void setCatalog(String catalog) {
        this.catalog = catalog;
    }
    void setSchema(String schema) {
        this.schema = schema;
    }
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((catalog == null) ? 0 : catalog.hashCode());
	result = prime * result + ((schema == null) ? 0 : schema.hashCode());
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
	Table other = (Table) obj;
	if (catalog == null) {
	    if (other.catalog != null)
		return false;
	} else if (!catalog.equals(other.catalog))
	    return false;
	if (schema == null) {
	    if (other.schema != null)
		return false;
	} else if (!schema.equals(other.schema))
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
	return "Table [tableName=" + tableName + ", tableType=" + tableType + ", remarks=" + remarks + ", columns="
		+ columns + ", primaryKeys=" + primaryKeys + ", indexes=" + indexes + ", importedforeignKeys="
		+ importedforeignKeys + ", exportedforeignKeys=" + exportedforeignKeys + ", catalog=" + catalog
		+ ", schema=" + schema + "]";
    }

}
