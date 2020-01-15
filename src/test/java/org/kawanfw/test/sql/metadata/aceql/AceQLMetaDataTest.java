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
package org.kawanfw.test.sql.metadata.aceql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.List;

import org.kawanfw.sql.metadata.AceQLMetaData;
import org.kawanfw.sql.metadata.Column;
import org.kawanfw.sql.metadata.ExportedKey;
import org.kawanfw.sql.metadata.ImportedKey;
import org.kawanfw.sql.metadata.Index;
import org.kawanfw.sql.metadata.PrimaryKey;
import org.kawanfw.sql.metadata.Table;

public class AceQLMetaDataTest {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	Connection connection = ConnectionParms.getConnection(3);

	DatabaseMetaData databaseMetaData = connection.getMetaData();
	String databaseProductName = databaseMetaData.getDatabaseProductName();
	System.out.println("databaseMetaData.databaseProductName: " + databaseProductName);

	AceQLMetaData aceQLMetaData = new AceQLMetaData(connection);

	List<String> catalogs =  aceQLMetaData.getCatalogs();
	System.out.println("catalogs: " +  catalogs);

	List<String> schemas =  aceQLMetaData.getSchemas();
	System.out.println("schemas: " +  schemas);

	System.out.println();

	List<String> tableTypes = aceQLMetaData.getTableTypes();
	System.out.println("tableTypes: " + tableTypes);

	List<String> tables = aceQLMetaData.getTableNames();
	System.out.println("All Tables:" + tables);

	List<String> filteredTables = aceQLMetaData.getTableNames("VIEW");
	System.out.println("Filtered" + filteredTables);

	for (String tableName : tables) {
	    // System.out.println();
	    Table table = aceQLMetaData.getTable(tableName);
	    List<Column> columns = table.getColumns();
	    System.out.println();
	    System.out.println("Table: " + tableName);
	    for (Column column : columns) {
		System.out.println(column);
	    }
	    List<PrimaryKey> primaryKeys = table.getPrimaryKeys();
	    for (PrimaryKey primaryKey : primaryKeys) {
		System.out.println(primaryKey);
	    }
	    List<Index> indexes = aceQLMetaData.getIndexes(tableName);
	    for (Index index : indexes) {
		System.out.println(index);
	    }
	    List<ImportedKey> importedKeys = aceQLMetaData.getImportedKeys(tableName);
	    if (importedKeys != null && !importedKeys.isEmpty()) {
		for (ImportedKey importedKey : importedKeys) {
		    System.out.println(importedKey);
		}
	    }
	    List<ExportedKey> exportedKeys = aceQLMetaData.getExportedKeys(tableName);
	    if (exportedKeys != null && !exportedKeys.isEmpty()) {
		for (ExportedKey exportedKey : exportedKeys) {
		    System.out.println(exportedKey);
		}
	    }
	}

    }

}
