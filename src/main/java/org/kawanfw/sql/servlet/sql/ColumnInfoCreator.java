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
package org.kawanfw.sql.servlet.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.kawanfw.sql.api.util.SqlUtil;
import org.kawanfw.sql.util.FrameworkDebug;
import org.postgresql.PGResultSetMetaData;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ColumnInfoCreator {

    private static boolean DEBUG = FrameworkDebug.isSet(ColumnInfoCreator.class);

    private ResultSetMetaData meta;
    private boolean isPostgreSQL;

    private List<Integer> columnTypeList = new Vector<Integer>();
    private List<String> columnTypeNameList = new Vector<String>();
    private List<String> columnNameList = new Vector<String>();
    private List<String> columnTableList = new Vector<String>();

    private Map<String, Integer> mapColumnNames = new LinkedHashMap<String, Integer>();

    private PGResultSetMetaData pgResultSetMetaData;

    /**
     * Constructor.
     * @param resultSet
     * @param productName the database vendo product name
     * @throws SQLException
     */
    public ColumnInfoCreator(ResultSet resultSet, String productName) throws SQLException {
	this.meta = resultSet.getMetaData();

	this.isPostgreSQL = productName.equals(SqlUtil.POSTGRESQL) ? true : false;
	if (isPostgreSQL) {
	    pgResultSetMetaData = (PGResultSetMetaData) resultSet.getMetaData();	    
	}
	
	create();
    }

    /**
     * Create all the necessary column info.
     * @throws SQLException
     */
    public void create() throws SQLException {
	int cols = meta.getColumnCount();
	// Loop on Columns
	for (int i = 1; i <= cols; i++) {
	    columnTypeList.add(meta.getColumnType(i));
	    //NO! columnNameList.add(meta.getColumnName(i).toLowerCase());
	    columnNameList.add(meta.getColumnName(i));
	    columnTypeNameList.add(meta.getColumnTypeName(i));

	    if (isPostgreSQL) {
		columnTableList.add(PostgreSqlUtil.getTableName(pgResultSetMetaData, i));
	    } else {
		columnTableList.add(meta.getTableName(i));
	    }

	    debug("");
	    debug("meta.getColumnType(" + i + ")    : " + meta.getColumnType(i));
	    debug("meta.getColumnTypeName(" + i + "): " + meta.getColumnTypeName(i));
	    debug("meta.getColumnName(" + i + ")    : " + meta.getColumnName(i));
	    debug("meta.getTableName(" + i + ")     : " + meta.getTableName(i));
	}

	// Ok, dump the column Map<String, Integer> == (Column name, column
	// pos starting 9)
	Map<String, Integer> mapColumnNames = new LinkedHashMap<String, Integer>();

	for (int i = 0; i < columnNameList.size(); i++) {
	    mapColumnNames.put(columnNameList.get(i), i);
	}

    }

    public List<Integer> getColumnTypeList() {
        return columnTypeList;
    }

    public List<String> getColumnTypeNameList() {
        return columnTypeNameList;
    }

    public List<String> getColumnNameList() {
        return columnNameList;
    }

    public List<String> getColumnTableList() {
        return columnTableList;
    }

    public Map<String, Integer> getMapColumnNames() {
        return mapColumnNames;
    }

    /**
     * @param s
     */

    protected void debug(String s) {
	if (DEBUG) {
	    // System.out.println(new Date() + " " + s);
	    System.out.println(s);
	}
    }

}
