/*
 * Copyright (c)2022 KawanSoft S.A.S.
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-30
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.test.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import org.kawanfw.sql.servlet.metadata.JsonDatabaseMetaData;
import org.kawanfw.test.parms.ConnectionLoader;

/**
 * @author Nicolas de Pomereu
 *
 */
public class JsonDatabaseMetaDataTest {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

	Connection connection = ConnectionLoader.getLocalConnection();
	DatabaseMetaData databaseMetaData = connection.getMetaData();

	JsonDatabaseMetaData jsonDatabaseMetaData = new JsonDatabaseMetaData(
		databaseMetaData);
	String JsonString = jsonDatabaseMetaData.build();
	System.out.println(JsonString);

    }

}
