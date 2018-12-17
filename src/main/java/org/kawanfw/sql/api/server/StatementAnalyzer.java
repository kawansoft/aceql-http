/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP                                     
 * Copyright (C) 2018, KawanSoft SAS
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

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.Tag;

/**
 * Class that allows the analysis of the string content of a SQL statement,
 * mainly for security reasons. <br>
 * <br>
 * Analysis methods include:
 * <p>
 * <ul>
 * <li>Says if a statement contains SQL comments.</li>
 * <li>Says if a statement contains semicolons (statement separator) that are
 * not trailing.</li>
 * <li>Extract the statement type:
 * <code>DELETE / INSERT / SELECT / UPDATE, CREATE / ALTER / DROP...</code></li>
 * <li>Says if the statement is a DML statement (exclusively:
 * <code>DELETE / INSERT / SELECT / UPDATE</code>).</li>
 * <li>Counts the number of parameters.</li>
 * <li>Methods to get the first, the last or any parameter.</li>
 * <li>Says if the statement is a DDL statement (exclusively:
 * <code>CREATE / ALTER / DROP / TRUNCATE / COMMENT / RENAME</code>).</li>
 * <li>Says if the statement is a DCL statement (exclusively:
 * <code>GRANT / REVOKE</code>).</li>
 * <li>Extract the table name in use for a DML statement;</li>
 * </ul>
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */

public class StatementAnalyzer {
    /** Set to true to display/log debug info */
    private static boolean DEBUG = FrameworkDebug
	    .isSet(StatementAnalyzer.class);

    // DML
    public final static String DELETE = "DELETE";
    public final static String INSERT = "INSERT";
    public final static String SELECT = "SELECT";
    public final static String UPDATE = "UPDATE";

    // DDL
    public final static String CREATE = "CREATE";
    public final static String ALTER = "ALTER";
    public final static String DROP = "DROP";
    public final static String TRUNCATE = "TRUNCATE";
    public final static String COMMENT = "COMMENT";
    public final static String RENAME = "RENAME";

    // DCL
    public final static String GRANT = "GRANT";
    public final static String REVOKE = "REVOKE";

    // TCL
    // public final static String COMMIT = "COMMIT ";
    // public final static String ROLLBACK = "ROLLBACK";
    // public final static String SET_TRANSACTION = "SAVEPOINT";
    // public final static String savepoint = "savepoint";

    private static final String BLANK = " ";

    /** The statement type */
    private final String statementType;

    /** The Sql statement in string format */
    private final String sql;

    /** The parameter values */
    private List<Object> parameterValues = null;

    /**
     * Constructor.
     * 
     * @param sql
     *            the string content of the SQL statement.
     * @param parameterValues
     *            the parameter values of a prepared statement in the natural
     *            order, empty list for a (non prepared) statement
     */
    public StatementAnalyzer(String sql, List<Object> parameterValues) {

	if (sql == null) {
	    throw new IllegalArgumentException(
		    Tag.PRODUCT_PRODUCT_FAIL + "sql can not be null!");
	}

	if (parameterValues == null) {
	    throw new IllegalArgumentException(Tag.PRODUCT_PRODUCT_FAIL
		    + "parameterValues can not be null!");
	}

	sql = sql.trim();

	// Remove last ";" because may cause a problem for getting table
	// name with getTableNameFromDmlStatement()

	sql = removeTrailingSemicolons(sql);
	this.sql = sql;

	// Remove tab on input parameter only for testing statement types
	sql = sql.replace('\t', ' ');
	sql = sql.trim(); // re-trim!
	this.statementType = StringUtils.substringBefore(sql, BLANK);

	this.parameterValues = parameterValues;
    }

    /**
     * Says if a statement contains semicolons (';') that are not trailing. Use
     * this to prevent attacks when a statement is multi-statements.
     * 
     * @return true if the SQL statement contains semicolons that are not
     *         trailing.
     */
    public boolean isWithSemicolons() {
	String localSql = sql;

	localSql = removeTrailingSemicolons(localSql);

	return localSql.contains(";");
    }

    /**
     * Remove all trailing ";" from SQL command
     * 
     * @param sql
     *            the sql command
     * @return the sql command without the trailing ";"
     */
    private String removeTrailingSemicolons(String sql) {
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
     * @return true if the SQL statement contains SQL comments
     */
    public boolean isWithComments() {
	return ((sql.contains("/*") && sql.contains("*/")
		|| (sql.contains("({") && sql.contains("})"))
		|| sql.contains(" --")));
    }

    /**
     * Extract the statement type from a SQL order.
     * 
     * @return the statement type: <code>DELETE, INSERT, SELECT, UPDATE,</code>
     *         etc...
     */
    public String getStatementType() {
	return statementType;
    }

    /**
     * Says a statement is a statement of certain type.
     * 
     * @param statementTypeToMatch
     *            the statement type to match: DELETE / ...
     * @return true if the statement type is matched.
     */
    private boolean isStatementType(String statementTypeToMatch) {
	if (statementTypeToMatch == null) {
	    throw new IllegalArgumentException(
		    "statementTypeToMatch can not be null!");
	}

	if (statementType == null) {
	    return false;
	}

	if (statementType.equalsIgnoreCase(statementTypeToMatch)) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Says if the statement is a <code>DELETE</code>.
     * 
     * @return true if the statement is a <code>DELETE</code>
     */
    public boolean isDelete() {
	return isStatementType(DELETE);
    }

    /**
     * Says if the statement is an <code>INSERT</code>.
     * 
     * @return true if the statement is an <code>INSERT</code>
     */
    public boolean isInsert() {
	return isStatementType(INSERT);
    }

    /**
     * Says if the statement is a <code>SELECT</code>.
     * 
     * @return true if the statement is a <code>SELECT</code>
     */
    public boolean isSelect() {
	return isStatementType(SELECT);
    }

    /**
     * Says if the statement is an <code>UPDATE</code>.
     * 
     * @return true if the statement is an <code>UPDATE</code>
     */
    public boolean isUpdate() {
	return isStatementType(UPDATE);
    }

    /**
     * Returns the number of parameters in the statement
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
     * @throws IndexOutOfBoundsException
     *             if there is no parameter
     */
    public Object getLastParameter() {

	int size = parameterValues.size();
	if (size == 0) {
	    throw new IndexOutOfBoundsException("There is no parameter.");
	}
	return parameterValues.get(size - 1);
    }

    /**
     * Returns the value in string of the first parameter of the parameters
     * list.
     * 
     * @return the value in string of the first parameter of the parameters
     *         list.
     * @throws IndexOutOfBoundsException
     *             if there is no parameter
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
     * @param index
     *            index of parameter as in a list: starts at 0.
     * @return the value as object of the parameter index.
     * @throws IndexOutOfBoundsException
     *             if the index is out of range (
     *             <tt>index &lt; 0 || index &gt;= size()</tt>)
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
		    "Parameter index is out of bounds: " + index
			    + ". Number of parameters: " + size);
	}
    }

    /**
     * Says if the statement is a DML (Data Manipulation Language) statement (
     * <code>DELETE/INSERT/SELECT/UPDATE</code>).
     * 
     * @return true if the statement is DML statement
     */
    public boolean isDml() {
	return (isDelete() || isInsert() || isSelect() || isUpdate());
    }

    /**
     * Says if the statement is a DCL (Data Control Language) statement (
     * <code>GRANT / REVOKE</code>).
     * 
     * @return true if the statement is DCL statement
     */
    public boolean isDcl() {
	if (statementType == null) {
	    return false;
	}

	if (statementType.equalsIgnoreCase(GRANT)
		|| statementType.equalsIgnoreCase(REVOKE)) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Says if the statement is a DDL (Data Definition Language) statement (
     * <code>CREATE / ALTER / DROP/ TRUNCATE / COMMENT / RENAME</code>)
     * 
     * @return true if the statement is DDL statement
     */
    public boolean isDdl() {
	if (statementType == null) {
	    return false;
	}

	if (statementType.equalsIgnoreCase(CREATE)
		|| statementType.equalsIgnoreCase(ALTER)
		|| statementType.equalsIgnoreCase(DROP)
		|| statementType.equalsIgnoreCase(TRUNCATE)
		|| statementType.equalsIgnoreCase(COMMENT)
		|| statementType.equalsIgnoreCase(RENAME)

	) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Returns the table name in use type from a DML SQL order.
     * 
     * @return the table name in use (the first one in a <code>SELECT</code>
     *         statement) for a DML statement. Returns null if statement is not
     *         DML.
     */
    public String getTableNameFromDmlStatement()
	    throws IllegalArgumentException {
	// Extract the first order
	String statementTypeUpper = statementType.toUpperCase();

	String sqlUpper = sql.toUpperCase();

	// Extract the table depending on the ordOer
	sqlUpper = StringUtils.substringAfter(sqlUpper, statementTypeUpper);
	sqlUpper = sqlUpper.trim();

	String table = null;

	if (statementTypeUpper.equals(INSERT)) {
	    sqlUpper = StringUtils.substringAfter(sqlUpper, "INTO ");
	    sqlUpper = sqlUpper.trim();
	    table = StringUtils.substringBefore(sqlUpper, " ");
	} else if (statementTypeUpper.equals(SELECT)
		|| statementTypeUpper.equals(DELETE)) {
	    sqlUpper = StringUtils.substringAfter(sqlUpper, "FROM ");
	    sqlUpper = sqlUpper.trim();
	    // Remove commas in the statement and replace with blanks in case we
	    // have
	    // a join: "TABLE," ==> "TABLE "
	    sqlUpper = sqlUpper.replaceAll(",", " ");
	    table = StringUtils.substringBefore(sqlUpper, BLANK);
	} else if (statementTypeUpper.equals(UPDATE)) {
	    debug("sqlLocal :" + sqlUpper + ":");
	    table = StringUtils.substringBefore(sqlUpper, BLANK);
	} else {
	    return null; // No table
	}

	debug("table: " + table);

	if (table != null) {
	    table = table.trim();
	}

	// Return the part after last dot
	if (table.contains(".")) {
	    table = StringUtils.substringAfterLast(table, ".");
	}

	table = table.replace("\'", "");
	table = table.replace("\"", "");

	debug("table before return: " + table);

	return table;
    }

    /**
     * Returns the string content of the SQL statement.
     * 
     * @return the string content of the SQL statement
     */
    public String getSql() {
	return this.sql;
    }

    // * <li>Says if the statement contain basic aggregate functions:
    // * <code>MAX(), MIN(), COUNT() or AVG()</code>.</li>

    // /**
    // * Says if the statement has at least a basic aggregate function: &nbsp;
    // * <code>MAX(), MIN(), COUNT(), AVG()</code>.
    // *
    // * @return true if the statement has at least a basic aggregate function
    // */
    // public boolean isWithBasicAggregate() {
    // String sqlOrderUpper = sql.toUpperCase();
    //
    // // Aggregate format is either " MAX(" or " MAX "
    // String arrayAgg[] = { " MAX(", " MIN(", " COUNT(", " AVG(" };
    //
    // for (int i = 0; i < arrayAgg.length; i++) {
    // String element = arrayAgg[i];
    //
    // if (sqlOrderUpper.contains(element)) {
    // return true;
    // }
    //
    // // Presentation may be different: "MAX (" instead of "MAX("
    // element = element.replace("(", " ");
    //
    // if (sqlOrderUpper.contains(element)) {
    // return true;
    // }
    //
    // }
    //
    // return false;
    // }

    /**
     * Debug tool
     * 
     * @param s
     */

    // @SuppressWarnings("unused")
    private void debug(String s) {
	if (DEBUG) {
	    // System.out.println(new Date() + " " + s);
	    System.out.println(new Date() + " " + s);
	}
    }

}
