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
	Connection connection = ConnectionParms.getConnection(ConnectionParms.SQL_SERVER_CONNECTION);

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
