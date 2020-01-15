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
