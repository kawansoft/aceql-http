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

import org.kawanfw.sql.metadata.AceQLMetaData;
import org.kawanfw.sql.metadata.JdbcDatabaseMetaData;

public class AceQLMetaDataTestDatabaseMetaData {

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
	    JdbcDatabaseMetaData jdbcDatabaseMetaData = aceQLMetaData.getJdbcDatabaseMetaData();

	    System.out.println("jdbcDatabaseMetaData  : " + jdbcDatabaseMetaData);
	}

    }

}
