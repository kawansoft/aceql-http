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

public class AceQLMetaDataTestShort {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

	for (int i = 1; i < 6; i++) {
	    Connection connection = ConnectionParms.getConnection(i);

	    DatabaseMetaData databaseMetaData = connection.getMetaData();
	    String databaseProductName = databaseMetaData.getDatabaseProductName();
	    System.out.println();
	    System.out.println("Product : " + databaseProductName);
	    System.out.println("UserName: " + databaseMetaData.getUserName());

	    AceQLMetaData aceQLMetaData = new AceQLMetaData(connection);

	    List<String> catalogs = aceQLMetaData.getCatalogs();
	    System.out.println("catalogs: " + catalogs);

	    List<String> schemas = aceQLMetaData.getSchemas();
	    System.out.println("schemas : " + schemas);

	    List<String> types = aceQLMetaData.getTableTypes();
	    System.out.println("types   : " + types);

	    List<String> tables = aceQLMetaData.getTableNames();
	    System.out.println("tables  : " + tables);

	    List<String> filteredTables = aceQLMetaData.getTableNames("VIEW");
	    System.out.println("Filtered: " + filteredTables);

	}

    }

}
