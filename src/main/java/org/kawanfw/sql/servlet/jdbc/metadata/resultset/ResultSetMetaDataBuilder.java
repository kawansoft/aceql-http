/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.jdbc.metadata.resultset;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.kawanfw.sql.jdbc.metadata.ResultSetMetaDataHolder;

/**
 * Builds the ResultSetMetaDataHolder.
 * @author Nicolas de Pomereu
 *
 */
public class ResultSetMetaDataBuilder {

    private ResultSet resultSet = null;

    private int columnCount;

    private List<Boolean> autoIncrement = new Vector<Boolean>();
    private List<Boolean> caseSensitive = new Vector<Boolean>();
    private List<Boolean> searchable = new Vector<Boolean>();
    private List<Boolean> currency = new Vector<Boolean>();
    private List<Integer> nullable = new Vector<Integer>();
    private List<Boolean> signed = new Vector<Boolean>();
    private List<Integer> columnDisplaySize = new Vector<Integer>();
    private List<String> columnLabel = new Vector<String>();
    private List<String> columnName = new Vector<String>();
    private List<String> schemaName = new Vector<String>();
    private List<Integer> precision = new Vector<Integer>();
    private List<Integer> scale = new Vector<Integer>();
    private List<String> tableName = new Vector<String>();
    private List<String> catalogName = new Vector<String>();
    private List<Integer> columnType = new Vector<Integer>();
    private List<String> columnTypeName = new Vector<String>();
    private List<Boolean> readOnly = new Vector<Boolean>();
    private List<Boolean> writable = new Vector<Boolean>();
    private List<Boolean> definitelyWritable = new Vector<Boolean>();
    private List<String> columnClassName = new Vector<String>();

    private ResultSetMetaDataHolder resultSetMetaDataHolder;

    public ResultSetMetaDataBuilder(ResultSet resultSet) throws SQLException {
	this.resultSet = resultSet;
	build();
    }

    /**
     * Fills the values to pass to ResultSetMetaDataHolder and builds the ResultSetMetaDataHolder
     * @throws SQLException
     */
    private void build() throws SQLException {
	ResultSetMetaData meta = resultSet.getMetaData();

	fillLists(meta);

	resultSetMetaDataHolder = new ResultSetMetaDataHolder();
	resultSetMetaDataHolder.setColumnCount(columnCount);
	resultSetMetaDataHolder.setAutoIncrement(autoIncrement);
	resultSetMetaDataHolder.setCaseSensitive(caseSensitive);
	resultSetMetaDataHolder.setSearchable(searchable);
	resultSetMetaDataHolder.setCurrency(currency);
	resultSetMetaDataHolder.setNullable(nullable);
	resultSetMetaDataHolder.setSigned(signed);
	resultSetMetaDataHolder.setColumnDisplaySize(columnDisplaySize);
	resultSetMetaDataHolder.setColumnLabel(columnLabel);
	resultSetMetaDataHolder.setColumnName(columnName);
	resultSetMetaDataHolder.setSchemaName(schemaName);
	resultSetMetaDataHolder.setPrecision(precision);
	resultSetMetaDataHolder.setScale(scale);
	resultSetMetaDataHolder.setTableName(tableName);
	resultSetMetaDataHolder.setCatalogName(catalogName);
	resultSetMetaDataHolder.setColumnType(columnType);
	resultSetMetaDataHolder.setColumnTypeName(columnTypeName);
	resultSetMetaDataHolder.setReadOnly(readOnly);
	resultSetMetaDataHolder.setWritable(writable);
	resultSetMetaDataHolder.setDefinitelyWritable(definitelyWritable);
	resultSetMetaDataHolder.setColumnClassName(columnClassName);
    }

    /**
     * Fill all the list values of the ResultSetDataMeta
     * @param meta
     * @throws SQLException
     */
    private void fillLists(ResultSetMetaData meta) throws SQLException {
	columnCount = meta.getColumnCount();

	// Loop on Columns
	for (int i = 1; i <= columnCount; i++) {
	    autoIncrement.add(meta.isAutoIncrement(i));
	    caseSensitive.add(meta.isCaseSensitive(i));
	    searchable.add(meta.isSearchable(i));
	    currency.add(meta.isCurrency(i));
	    nullable.add(meta.isNullable(i));
	    signed.add(meta.isSigned(i));
	    columnDisplaySize.add(meta.getColumnDisplaySize(i));
	    columnLabel.add(meta.getColumnLabel(i));
	    columnName.add(meta.getColumnName(i));
	    schemaName.add(meta.getSchemaName(i));
	    precision.add(meta.getPrecision(i));
	    scale.add(meta.getScale(i));
	    tableName.add(meta.getTableName(i));
	    catalogName.add(meta.getCatalogName(i));
	    columnType.add(meta.getColumnType(i));
	    columnTypeName.add(meta.getColumnTypeName(i));
	    readOnly.add(meta.isReadOnly(i));
	    writable.add(meta.isWritable(i));
	    definitelyWritable.add(meta.isDefinitelyWritable(i));
	    columnClassName.add(meta.getColumnClassName(i));
	}
    }

    public ResultSetMetaDataHolder getResultSetMetaDataHolder() {
        return resultSetMetaDataHolder;
    }


}
