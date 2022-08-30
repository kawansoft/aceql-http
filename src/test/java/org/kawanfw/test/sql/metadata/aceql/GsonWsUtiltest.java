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
package org.kawanfw.test.sql.metadata.aceql;

import java.sql.Connection;
import java.util.List;

import org.kawanfw.sql.metadata.AceQLMetaData;
import org.kawanfw.sql.metadata.Column;
import org.kawanfw.sql.metadata.Table;
import org.kawanfw.sql.metadata.util.GsonWsUtil;

/**
 * @author Nicolas de Pomereu
 *
 */
public class GsonWsUtiltest {


    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

	Connection connection = ConnectionParms.getConnection(ConnectionParms.MYSQL_CONNECTION);
	AceQLMetaData aceQLMetaData = new AceQLMetaData(connection);

	List<String> tables = aceQLMetaData.getTableNames();
	System.out.println(tables);

	Table table = aceQLMetaData.getTable("orderlog2");
	List<Column> columns = table.getColumns();

	String jsonString = GsonWsUtil.getJSonString(columns.get(0));
	System.out.println(jsonString);

	System.out.println();
	Column back = GsonWsUtil.fromJson(jsonString, Column.class);
	System.out.println(back);

	System.out.println();

	jsonString = GsonWsUtil.getJSonString(table);
	System.out.println(jsonString);

	System.out.println();
	Table tableBack = GsonWsUtil.fromJson(jsonString, Table.class);
	System.out.println(tableBack);


    }

}
