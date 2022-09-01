/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.test.api.server;

import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Commit;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;

/**
 * @author Nicolas de Pomereu
 *
 */
public class JSQLParserTest {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

	Statement statement = CCJSqlParserUtil.parse("SELECT * from public.CUSTOMER");
	TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
	List<String> tableList = tablesNamesFinder.getTableList(statement);
	System.out.println(tableList);

	if (statement instanceof Commit) {
	    Commit commit = (Commit) statement;
		System.out.println(commit.getClass());
	}
    }

    /**
     * @throws JSQLParserException
     */
    public static void selectTest() throws JSQLParserException {
	Statement statement = CCJSqlParserUtil.parse("SELECT * FROM customer where toto = 'titi' ");

	Select selectStatement = (Select) statement;
	TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
	List<String> tableList = tablesNamesFinder.getTableList(selectStatement);
	System.out.println(tableList);
    }

}
