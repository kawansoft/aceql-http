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
package org.kawanfw.sql.servlet.util.operation_type;

import org.kawanfw.sql.api.util.JsqlParserWrapper;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

/**
 * @author Nicolas de Pomereu
 *
 */
public class DefaultOperationType implements OperationType {

    @Override
    public boolean isOperationAuthorized(String sql) {

	if (sql == null) {
	    return true;
	}
	
	try {
	    Statement parsedStatement = CCJSqlParserUtil.parse(sql);
	    JsqlParserWrapper jsqlParserWrapper = new JsqlParserWrapper(parsedStatement);
	    return ! jsqlParserWrapper.isDCL() && ! jsqlParserWrapper.isDDL();
	} catch (JSQLParserException e) {
	    System.err.println("sql: " + sql);
	    e.printStackTrace();
	    return true;
	}
    }

}
