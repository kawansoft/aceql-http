/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2021,  KawanSoft SAS
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
package org.kawanfw.sql.servlet.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.api.util.JsqlParserWrapper;
import org.kawanfw.sql.api.util.StatementAnalyzerUtil;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.Tag;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

/**
*  Derived from StatementAnalyzer. Simple form for out internal tests.
 */

public class ReducedStatementAnalyzer {
    /** Set to true to display/log debug info */
    private static boolean DEBUG = FrameworkDebug.isSet(ReducedStatementAnalyzer.class);

    private static final String BLANK = " ";

    /** The base parsed Statement name */
    private String statementName;

    /** The SQL parsed Statement in String format */
    private final String sql;

    private List<String> tables = new ArrayList<>();

    /** The underlying parsed/wrapped parsedStatement */
    private Statement parsedStatement = null;

    private SQLException parseException;

    /**
     * Constructor.
     *
     * @param sql             the string content of the SQL parsedStatement.
     * @param parameterValues the parameter values of a prepared parsedStatement in
     *                        the natural order, empty list for a (non prepared)
     *                        parsedStatement
     * @throws SQLException if the parsed Statement can not be parsed
     */
    public ReducedStatementAnalyzer(final String sql, List<Object> parameterValues) throws SQLException {

	if (sql == null) {
	    throw new IllegalArgumentException(Tag.PRODUCT_PRODUCT_FAIL + "sql can not be null!");
	}

	if (parameterValues == null) {
	    throw new IllegalArgumentException(Tag.PRODUCT_PRODUCT_FAIL + "parameterValues can not be null!");
	}

	
	this.sql = trimAndremoveTrailingSemicolons(sql);

	this.tables = new ArrayList<>();
	String theStatementName = StringUtils.substringBefore(this.sql, BLANK);

	// Can not treat GRANT, REVOKE or ROLLBACK here, not supported by
	// CCJSqlParserUtil
	if (theStatementName.equalsIgnoreCase("GRANT")) {
	    this.statementName = "GRANT";
	    this.tables = new ArrayList<>();
	} else if (theStatementName.equalsIgnoreCase("REVOKE")) {
	    this.statementName = "REVOKE";
	    this.tables = new ArrayList<>();
	} else if (theStatementName.equalsIgnoreCase("ROLLBACK")) {
	    this.statementName = "ROLLBACK";
	    this.tables = new ArrayList<>();
	} else if (theStatementName.equalsIgnoreCase("DROP")) {
	    this.statementName = "DROP";
	    this.tables = new ArrayList<>();
	} else {
	    parsedStatement = null;
	    try {
		parsedStatement = CCJSqlParserUtil.parse(this.sql);
		JsqlParserWrapper jsqlParserWrapper = new JsqlParserWrapper(parsedStatement);

		this.tables = jsqlParserWrapper.getTables();
		this.statementName = jsqlParserWrapper.getStatementName();

	    } catch (JSQLParserException e) {
		if (DEBUG) {
		    e.printStackTrace(System.err);
		}
		this.parseException = new SQLException(e);
	    }
	}

	if (this.statementName == null) {
	    this.statementName = StringUtils.substringBefore(this.sql, BLANK);
	}

    }

  
    /**
     * Remove all trailing ";" from SQL command
     *
     * @param sql the sql command
     * @return the sql command without the trailing ";"
     */
    private static String trimAndremoveTrailingSemicolons(final String sql) {
	String sqlNew = sql.trim();
	// Remove the trailing ";", there may be some blanks, so we always trim
	while (sqlNew.endsWith(";")) {
	    sqlNew = StringUtils.removeEnd(sqlNew, ";");
	    sqlNew = sqlNew.trim();
	}
	
	sqlNew = StatementAnalyzerUtil.fixForJsqlparser(sqlNew);
	return sqlNew;
    }


    /**
     * Says a if a statement is a statement of certain type.
     *
     * @param statementTypeToMatch the parsed Statement type to match: DELETE / ...
     * @return true if the parsed Statement type is matched.
     */
    private boolean isStatementType(String statementTypeToMatch) {
	if (statementTypeToMatch == null) {
	    throw new IllegalArgumentException("statementTypeToMatch can not be null!");
	}

	if (statementName == null) {
	    return false;
	}

	return statementName.equalsIgnoreCase(statementTypeToMatch);
    }

    /**
     * Says if the statement is a <code>DELETE</code>.
     *
     * @return true if the parsed Statement is a <code>DELETE</code>
     */
    public boolean isDelete() {
	return isStatementType("DELETE");
    }

    /**
     * Says if the statement is an <code>INSERT</code>.
     *
     * @return true if the parsed Statement is an <code>INSERT</code>
     */
    public boolean isInsert() {
	return isStatementType("INSERT");
    }

    /**
     * Says if the statement is a <code>SELECT</code>.
     *
     * @return true if the parsed Statement is a <code>SELECT</code>
     */
    public boolean isSelect() {
	return isStatementType("SELECT");
    }

    /**
     * Says if the statement is an <code>UPDATE</code>.
     *
     * @return true if the parsed Statement is an <code>UPDATE</code>
     */
    public boolean isUpdate() {
	return isStatementType("UPDATE");
    }

    /**
     * Returns the list of tables in the statement. Returns an empty list if no
     * tables found.
     *
     * @return the list of tables in the statement.
     */
    public List<String> getTables() {
	return tables;
    }

    /**
     * Returns the parse Exception if any.
     *
     * @return the parse Exception. null if none.
     */
    public SQLException getParseException() {
	return parseException;
    }

    /**
     * Debug tool
     *
     * @param s
     */

    @SuppressWarnings("unused")
    private void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
