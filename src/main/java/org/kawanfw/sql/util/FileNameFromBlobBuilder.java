/*
 * Copyright (c)2023 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-02-21
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Nicolas de Pomereu Build the raw filename that will contain the Blob
 *         or Clob contant
 */
public class FileNameFromBlobBuilder {

    // DML
    private final static String DELETE = "DELETE";
    private final static String INSERT = "INSERT";
    private final static String SELECT = "SELECT";
    private final static String UPDATE = "UPDATE";

    private static final String BLANK = " ";

    /** The statement sql string */
    private String sqlOrder = null;

    /** The column to dump */
    private String columnName = null;

    /** The parameter index */
    private int parameterIndex = 0;

    /** true if the Blob is a Clob */
    private boolean isClob = false;

    /**
     * Default constructor
     * 
     * @param sqlOrder
     *            The statement sql string
     * @param columnName
     *            The column to dump
     * @param isClob
     *            true if the Blob is a Clob
     */
    public FileNameFromBlobBuilder(String sqlOrder, String columnName,
	    boolean isClob) {
	if (sqlOrder == null) {
	    throw new IllegalArgumentException(
		    Tag.PRODUCT_PRODUCT_FAIL + "sqlOrder can not be null!");
	}

	if (columnName == null) {
	    throw new IllegalArgumentException(
		    Tag.PRODUCT_PRODUCT_FAIL + "columnName can not be null!");
	}

	this.sqlOrder = sqlOrder;
	this.columnName = columnName;
	this.isClob = isClob;
    }

    /**
     * Default constructor
     * 
     * @param sqlOrder
     *            The statement sql string
     * @param parameterIndex
     *            the index of the prepared statement
     * @param isClob
     *            true if the Blob as a Clob
     */
    public FileNameFromBlobBuilder(String sqlOrder, int parameterIndex,
	    boolean isClob) {
	if (sqlOrder == null) {
	    throw new IllegalArgumentException(
		    Tag.PRODUCT_PRODUCT_FAIL + "sqlOrder can not be null!");
	}

	if (parameterIndex < 1) {
	    throw new IllegalArgumentException(Tag.PRODUCT_PRODUCT_FAIL
		    + "Prepared statement parameterIndex must be >= 1");
	}

	this.sqlOrder = sqlOrder;
	this.parameterIndex = parameterIndex;
	this.isClob = isClob;
    }

    /**
     * @return the raw file name of the file to create from the blob/clob column
     *         with the content
     */
    public String getFileName() {

	String fileName = null;
	String unique = FrameworkFileUtil.getUniqueId();

	sqlOrder = sqlOrder.trim();

	String statementType = StringUtils.substringBefore(sqlOrder, BLANK);
	String tableName = getTableNameFromDmlStatement(statementType,
		sqlOrder);

	if (tableName == null) {
	    tableName = "unknown";
	}

	if (statementType == null) {
	    statementType = "unknown";
	}

	if (columnName != null) {
	    fileName = statementType.toLowerCase() + "-"
		    + tableName.toLowerCase() + "." + columnName + "-" + unique
		    + ".blob";
	} else {
	    fileName = statementType.toLowerCase() + "-"
		    + tableName.toLowerCase() + "-index-" + parameterIndex + "-"
		    + unique + ".blob";
	}

	if (isClob) {
	    fileName += ".clob.txt";
	}

	return fileName;
    }

    /**
     * Returns the table name in use type from a DML SQL order.
     * 
     * @param statementType
     *            the statement type (INSERT, ...)
     * @param sql
     *            the sql order
     * 
     * @return the table name in use (the first one in a <code>SELECT</code>
     *         statement) for a DML statement. Returns null if statement is not
     *         DML.
     */
    private String getTableNameFromDmlStatement(String statementType,
	    String sql) throws IllegalArgumentException {
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
	    // debug("sqlLocal :" + sqlUpper + ":");
	    table = StringUtils.substringBefore(sqlUpper, BLANK);
	} else {
	    return null; // No table
	}

	if (table != null) {
	    table = table.trim();
	}

	// Return the part after last dot
	if (table.contains(".")) {
	    table = StringUtils.substringAfterLast(table, ".");
	}

	table = table.replace("\'", "");
	table = table.replace("\"", "");

	return table;
    }

}
