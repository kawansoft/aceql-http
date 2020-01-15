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
package org.kawanfw.sql.api.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.Tag;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

/**
 * Class that allows the analysis of the string content of a SQL statement,
 * mainly for security reasons. <br>
 * <br>
 * Analysis methods include:
 * <ul>
 * <li>Says if a statement contains SQL comments.</li>
 * <li>Says if a statement contains semicolons (statement separator) that are
 * not trailing.</li>
 * <li>Extract the parsed statement name:&nbsp;
 * <code>DELETE / INSERT / SELECT / UPDATE, CREATE / ALTER / DROP...</code></li>
 * <li>Says if the parsed statement is a DML (Data Management Language)
 * statement.</li>
 * <li>Counts the number of parameters.</li>
 * <li>Methods to get the first, the last or any parameter.</li>
 * <li>Says if the parsed Statement is a DDL (Data Definition Language)
 * statement.</li>
 * <li>Says if the parsed Statement is a DCL (Data Control Language)
 * statement.</li>
 * <li>Says if the parsed Statement is a TCL (Transaction Control Language)
 * statement/</li>
 * <li>Extract the table name in use in the statement.</li>
 * </ul>
 *
 * @author Nicolas de Pomereu
 * @since 1.0
 */

public class StatementAnalyzer {
    /** Set to true to display/log debug info */
    private static boolean DEBUG = FrameworkDebug.isSet(StatementAnalyzer.class);

    private static final String BLANK = " ";

    /** The base parsed Statement name */
    private String statementName;

    /** The SQL parsed Statement in String format */
    private final String sql;

    /** The parameter values */
    private List<Object> parameterValues = null;

    private List<String> tables = new ArrayList<>();

    private boolean isDCL = false;
    private boolean isDDL = false;
    private boolean isDML = false;
    private boolean isTCL = false;

    /** The underlying parsed/wrapped parsedStatement */
    private Statement parsedStatement = null;

    /** if true, we could not say the parsed Statement type (DCL, DDL, DML, TCL) */
    private boolean statementTypeNotParsed = false;

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
    public StatementAnalyzer(String sql, List<Object> parameterValues) throws SQLException {

	if (sql == null) {
	    throw new IllegalArgumentException(Tag.PRODUCT_PRODUCT_FAIL + "sql can not be null!");
	}

	if (parameterValues == null) {
	    throw new IllegalArgumentException(Tag.PRODUCT_PRODUCT_FAIL + "parameterValues can not be null!");
	}

	sql = trimAndremoveTrailingSemicolons(sql);
	this.sql = sql;

	this.tables = new ArrayList<>();
	String theStatementName = StringUtils.substringBefore(sql, BLANK);

	// Can not treat GRANT, REVOKE or ROLLBACK here, not supported by
	// CCJSqlParserUtil
	if (theStatementName.equalsIgnoreCase("GRANT")) {
	    this.statementName = "GRANT";
	    this.isDCL = true;
	    this.tables = new ArrayList<>();
	} else if (theStatementName.equalsIgnoreCase("REVOKE")) {
	    this.statementName = "REVOKE";
	    this.isDCL = true;
	    this.tables = new ArrayList<>();
	} else if (theStatementName.equalsIgnoreCase("ROLLBACK")) {
	    this.statementName = "ROLLBACK";
	    this.isTCL = true;
	    this.tables = new ArrayList<>();
	} else if (theStatementName.equalsIgnoreCase("DROP")) {
	    this.statementName = "DROP";
	    this.isDDL = true;
	    this.tables = new ArrayList<>();
	} else {
	    parsedStatement = null;
	    try {
		parsedStatement = CCJSqlParserUtil.parse(sql);
		JsqlParserWrapper jsqlParserWrapper = new JsqlParserWrapper(parsedStatement);
		this.isDCL = jsqlParserWrapper.isDCL();
		this.isDDL = jsqlParserWrapper.isDDL();
		this.isDML = jsqlParserWrapper.isDML();
		this.isTCL = jsqlParserWrapper.isTCL();

		this.tables = jsqlParserWrapper.getTables();
		this.statementName = jsqlParserWrapper.getStatementName();

	    } catch (JSQLParserException e) {
		if (DEBUG) {
		    e.printStackTrace(System.err);
		}
		this.parseException = new SQLException(e);
	    }
	}

	// If returned parsedStatement is null, let's assume it was not parsed
	if (parsedStatement == null) {
	    this.statementTypeNotParsed = true;
	}

	if (this.statementName == null) {
	    this.statementName = StringUtils.substringBefore(sql, BLANK);
	}

	this.parameterValues = parameterValues;
    }

    /**
     * Returns the statement parsed with JSQLParser.
     *
     * @return statement parsed with JSQLParser
     */
    public Statement getParsedStatement() {
	return parsedStatement;
    }

    /**
     * Says if a statement contains semicolons (';') that are not trailing. Use this
     * to prevent attacks when a parsedStatement is multi-statements.
     *
     * @return true if the SQL statement contains semicolons that are not trailing.
     */
    public boolean isWithSemicolons() {
	String localSql = sql;
	localSql = trimAndremoveTrailingSemicolons(localSql);
	return localSql.contains(";");
    }

    /**
     * Remove all trailing ";" from SQL command
     *
     * @param sql the sql command
     * @return the sql command without the trailing ";"
     */
    private String trimAndremoveTrailingSemicolons(String sql) {
	sql = sql.trim();
	// Remove the trailing ";", there may be some blanks, so we always trim
	while (sql.endsWith(";")) {
	    sql = StringUtils.removeEnd(sql, ";");
	    sql = sql.trim();
	}
	return sql;
    }

    /**
     * Says if a statement contains SQL comments.
     *
     * @return true if the SQL parsedStatement contains SQL comments
     */
    public boolean isWithComments() {
	return ((sql.contains("/*") && sql.contains("*/") || (sql.contains("({") && sql.contains("})"))
		|| sql.contains(" --")));
    }

    /**
     * Extracts the statement name from a SQL order.
     *
     * @return the statement name: <code>DELETE, INSERT, SELECT, UPDATE,</code>
     *         etc...
     */
    public String getStatementName() {
	return statementName;
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

	if (statementName.equalsIgnoreCase(statementTypeToMatch)) {
	    return true;
	} else {
	    return false;
	}
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
     * Returns the number of parameters in the statement.
     *
     * @return the number of parameters in the statement
     */
    public int getParameterCount() {
	return parameterValues.size();
    }

    /**
     * Returns the value in string of the last parameter of the parameters list.
     *
     * @return the value in string of the last parameter of the parameters list
     * @throws IndexOutOfBoundsException if there is no parameter
     */
    public Object getLastParameter() {

	int size = parameterValues.size();
	if (size == 0) {
	    throw new IndexOutOfBoundsException("There is no parameter.");
	}
	return parameterValues.get(size - 1);
    }

    /**
     * Returns the value in string of the first parameter of the parameters list.
     *
     * @return the value in string of the first parameter of the parameters list.
     * @throws IndexOutOfBoundsException if there is no parameter
     */
    public Object getFirstParameter() {
	int size = parameterValues.size();
	if (size == 0) {
	    throw new IndexOutOfBoundsException("There is no parameter.");
	}
	return parameterValues.get(0);
    }

    /**
     * Returns the value as object of the parameter index in the list.
     *
     * @param index index of parameter as in a list: starts at 0.
     * @return the value as object of the parameter index.
     * @throws IndexOutOfBoundsException if the index is out of range (
     *                                   <tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    public Object getParameter(int index) {
	int size = parameterValues.size();
	if (size == 0) {
	    throw new IndexOutOfBoundsException("There is no parameter.");
	}
	try {
	    return parameterValues.get(index);
	} catch (IndexOutOfBoundsException e) {
	    throw new IndexOutOfBoundsException(
		    "Parameter index is out of bounds: " + index + ". Number of parameters: " + size);
	}
    }

    /**
     * Says if the statement is a DML (Data Manipulation Language) parsedStatement.
     *
     * @return true if the parsed Statement is DML parsedStatement
     */
    public boolean isDml() {
	return isDML;
    }

    /**
     * Says if the statement is a DCL (Data Control Language) parsedStatement
     *
     * @return true if the parsed Statement is DCL parsedStatement
     */
    public boolean isDcl() {
	return isDCL;
    }

    /**
     * Says if the Statement is a DDL (Data Definition Language) parsedStatement.
     *
     * @return true if the parsed Statement is DDL parsedStatement
     */
    public boolean isDdl() {
	return isDDL;
    }

    /**
     * Says if the statement is a TCL (Transaction Control Language)
     * parsedStatement.
     *
     * @return true if the parsed Statement is DDL parsedStatement
     */
    public boolean isTcl() {
	return isTCL;
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
     * Says if the statement type (DDL, DML, DCL, TCL) could not be parsed
     *
     * @return true if the parsed Statement type could no be parsed.
     */
    public boolean isStatementTypeNotParsed() {
	return statementTypeNotParsed;
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
     * Returns the string content of the SQL statement.
     *
     * @return the string content of the SQL statement
     */
    public String getSql() {
	return this.sql;
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
