/*
 * This file is part of AceQL Client SDK.
 * AceQL Client SDK: Remote JDBC access over HTTP with AceQL HTTP.
 * Copyright (C) 2021,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.
 *
 * Licensed under the Apache License, DefaultVersion 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kawanfw.sql.jdbc.metadata;

import java.util.List;
import java.util.Vector;

import org.kawanfw.sql.util.HtmlConverter;


/**
 * @author Nicolas de Pomereu A holder for a ResultSetMetaData that we want to
 *         transport.
 *
 */
public class ResultSetMetaDataHolder {


    private int columnCount;
    private List<Boolean> autoIncrement;
    private List<Boolean> caseSensitive;
    private List<Boolean> searchable;
    private List<Boolean> currency;
    private List<Integer> nullable;
    private List<Boolean> signed;
    private List<Integer> columnDisplaySize;
    private List<String> columnLabel;
    private List<String> columnName;
    private List<String> schemaName;
    private List<Integer> precision;
    private List<Integer> scale;
    private List<String> tableName;
    private List<String> catalogName;
    private List<Integer> columnType;
    private List<String> columnTypeName;
    private List<Boolean> readOnly;
    private List<Boolean> writable;
    private List<Boolean> definitelyWritable;
    private List<String> columnClassName;

    /**
     * @return the autoIncrement
     */
    public List<Boolean> getAutoIncrement() {
	return this.autoIncrement;
    }

    /**
     * @param autoIncrement
     *            the autoIncrement to set
     */
    public void setAutoIncrement(List<Boolean> autoIncrement) {
	this.autoIncrement = autoIncrement;
    }

    /**
     * @return the caseSensitive
     */
    public List<Boolean> getCaseSensitive() {
	return this.caseSensitive;
    }

    /**
     * @param caseSensitive
     *            the caseSensitive to set
     */
    public void setCaseSensitive(List<Boolean> caseSensitive) {
	this.caseSensitive = caseSensitive;
    }

    /**
     * @return the searchable
     */
    public List<Boolean> getSearchable() {
	return this.searchable;
    }

    /**
     * @param searchable
     *            the searchable to set
     */
    public void setSearchable(List<Boolean> searchable) {
	this.searchable = searchable;
    }

    /**
     * @return the currency
     */
    public List<Boolean> getCurrency() {
	return this.currency;
    }

    /**
     * @param currency
     *            the currency to set
     */
    public void setCurrency(List<Boolean> currency) {
	this.currency = currency;
    }

    /**
     * @return the nullable
     */
    public List<Integer> getNullable() {
	return this.nullable;
    }

    /**
     * @param nullable
     *            the nullable to set
     */
    public void setNullable(List<Integer> nullable) {
	this.nullable = nullable;
    }

    /**
     * @return the signed
     */
    public List<Boolean> getSigned() {
	return this.signed;
    }

    /**
     * @param signed
     *            the signed to set
     */
    public void setSigned(List<Boolean> signed) {
	this.signed = signed;
    }

    /**
     * @return the columnDisplaySize
     */
    public List<Integer> getColumnDisplaySize() {
	return this.columnDisplaySize;
    }

    /**
     * @param columnDisplaySize
     *            the columnDisplaySize to set
     */
    public void setColumnDisplaySize(List<Integer> columnDisplaySize) {
	this.columnDisplaySize = columnDisplaySize;
    }

    /**
     * @return the columnLabel
     */
    public List<String> getColumnLabel() {
	// return this.columnLabel;

	List<String> columnLabelReturn = new Vector<String>();
	for (String label : this.columnLabel) {
	    columnLabelReturn.add(HtmlConverter.fromHtml(label));
	}
	return columnLabelReturn;
    }

    /**
     * @param columnLabel
     *            the columnLabel to set
     */
    public void setColumnLabel(List<String> columnLabel) {

	this.columnLabel = new Vector<String>();
	for (String label : columnLabel) {
	    this.columnLabel.add(HtmlConverter.toHtml(label));
	}
    }

    /**
     * @return the columnName
     */
    public List<String> getColumnName() {
	List<String> columnNameReturn = new Vector<String>();
	for (String name : this.columnName) {
	    columnNameReturn.add(HtmlConverter.fromHtml(name));
	}
	return columnNameReturn;
    }

    /**
     * @param columnName
     *            the columnName to set
     */
    public void setColumnName(List<String> columnName) {
	this.columnName = new Vector<String>();
	for (String name : columnName) {
	    this.columnName.add(HtmlConverter.toHtml(name));
	}
    }

    /**
     * @return the schemaName
     */
    public List<String> getSchemaName() {
	return this.schemaName;
    }

    /**
     * @param schemaName
     *            the schemaName to set
     */
    public void setSchemaName(List<String> schemaName) {
	this.schemaName = schemaName;
    }

    /**
     * @return the precision
     */
    public List<Integer> getPrecision() {
	return this.precision;
    }

    /**
     * @param precision
     *            the precision to set
     */
    public void setPrecision(List<Integer> precision) {
	this.precision = precision;
    }

    /**
     * @return the scale
     */
    public List<Integer> getScale() {
	return this.scale;
    }

    /**
     * @param scale
     *            the scale to set
     */
    public void setScale(List<Integer> scale) {
	this.scale = scale;
    }

    /**
     * @return the tableName
     */
    public List<String> getTableName() {
	return this.tableName;
    }

    /**
     * @param tableName
     *            the tableName to set
     */
    public void setTableName(List<String> tableName) {
	this.tableName = tableName;
    }

    /**
     * @return the catalogName
     */
    public List<String> getCatalogName() {
	return this.catalogName;
    }

    /**
     * @param catalogName
     *            the catalogName to set
     */
    public void setCatalogName(List<String> catalogName) {
	this.catalogName = catalogName;
    }

    /**
     * @return the columnType
     */
    public List<Integer> getColumnType() {
	return this.columnType;
    }

    /**
     * @param columnType
     *            the columnType to set
     */
    public void setColumnType(List<Integer> columnType) {
	this.columnType = columnType;
    }

    /**
     * @return the columnTypeName
     */
    public List<String> getColumnTypeName() {
	return this.columnTypeName;
    }

    /**
     * @param columnTypeName
     *            the columnTypeName to set
     */
    public void setColumnTypeName(List<String> columnTypeName) {
	this.columnTypeName = columnTypeName;
    }

    /**
     * @return the readOnly
     */
    public List<Boolean> getReadOnly() {
	return this.readOnly;
    }

    /**
     * @param readOnly
     *            the readOnly to set
     */
    public void setReadOnly(List<Boolean> readOnly) {
	this.readOnly = readOnly;
    }

    /**
     * @return the writable
     */
    public List<Boolean> getWritable() {
	return this.writable;
    }

    /**
     * @param writable
     *            the writable to set
     */
    public void setWritable(List<Boolean> writable) {
	this.writable = writable;
    }

    /**
     * @return the definitelyWritable
     */
    public List<Boolean> getDefinitelyWritable() {
	return this.definitelyWritable;
    }

    /**
     * @param definitelyWritable
     *            the definitelyWritable to set
     */
    public void setDefinitelyWritable(List<Boolean> definitelyWritable) {
	this.definitelyWritable = definitelyWritable;
    }

    /**
     * @return the columnClassName
     */
    public List<String> getColumnClassName() {
	return this.columnClassName;
    }

    /**
     * @param columnClassName
     *            the columnClassName to set
     */
    public void setColumnClassName(List<String> columnClassName) {
	this.columnClassName = columnClassName;
    }

    /**
     * @return the columnCount
     */
    public int getColumnCount() {
	return this.columnCount;
    }

    /**
     * @param columnCount
     *            the columnCount to set
     */
    public void setColumnCount(int columnCount) {
	this.columnCount = columnCount;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "ResultSetMetaDataHolder [columnCount=" + this.columnCount
		+ ", autoIncrement=" + this.autoIncrement + ", caseSensitive="
		+ this.caseSensitive + ", searchable=" + this.searchable
		+ ", currency=" + this.currency + ", nullable=" + this.nullable
		+ ", signed=" + this.signed + ", columnDisplaySize="
		+ this.columnDisplaySize + ", columnLabel=" + this.columnLabel
		+ ", columnName=" + this.columnName + ", schemaName="
		+ this.schemaName + ", precision=" + this.precision
		+ ", scale=" + this.scale + ", tableName=" + this.tableName
		+ ", catalogName=" + this.catalogName + ", columnType="
		+ this.columnType + ", columnTypeName=" + this.columnTypeName
		+ ", readOnly=" + this.readOnly + ", writable=" + this.writable
		+ ", definitelyWritable=" + this.definitelyWritable
		+ ", columnClassName=" + this.columnClassName + "]";
    }

}
