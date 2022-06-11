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
package org.kawanfw.sql.servlet.sql;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.json.stream.JsonGenerator;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.jdbc.metadata.ResultSetMetaDataHolder;
import org.kawanfw.sql.metadata.util.GsonWsUtil;
import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.jdbc.metadata.resultset.ResultSetMetaDataBuilder;
import org.kawanfw.sql.tomcat.StaticParms;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ResultSetWriter {

    static final String NULL = "NULL";

    private static boolean DEBUG = FrameworkDebug.isSet(ResultSetWriter.class);

    public static String CR_LF = System.getProperty("line.separator");

    /** the sql order */
    private String sqlOrder = null;


    /**
     * Says if ResultSet Meta Data must be downloaded from server along with
     * ResultSet
     */
    private boolean fillResultSetMetaData = false;
    private HttpServletRequest request;
    private Boolean doColumnTypes = false;
    private JsonGenerator gen = null;


    /**
     * @param request  the http request
     * @param sqlOrder the sql order
     * @param gen      The JSon Generator
     * @param fillResultSetMetaData TODO
     */
    public ResultSetWriter(HttpServletRequest request, String sqlOrder, JsonGenerator gen, boolean fillResultSetMetaData) {

	this.sqlOrder = sqlOrder;

	this.request = request;
	this.gen = gen;

	String columnTypes = request.getParameter(HttpParameter.COLUMN_TYPES);
	doColumnTypes = Boolean.parseBoolean(columnTypes);

	this.fillResultSetMetaData = fillResultSetMetaData;
	debug("fillResultSetMetaData: " + fillResultSetMetaData);

    }

    /**
     * Constructor for tests
     *
     * @param out
     * @param sqlOrder
     */
    public ResultSetWriter(String sqlOrder) {
	super();
	this.sqlOrder = sqlOrder;
    }

    /**
     * Code extracted and modified for OReilly jdbc book in french.
     *
     * Process the ResultSet and print it on the outPutStream <br>
     * - Each row is a line of a List of column values <br>
     *
     * @param resultSet the Result Set to process and print on the output stream
     * @param br        the writer where to redirect the result set content, one
     *                  Json line per rs.next();
     *
     */
    public void write(ResultSet resultSet) throws SQLException, IOException {
	try {
	    if (resultSet == null) {
		throw new SQLException("resultSet is null!");
	    }

	    String productName = ResultSetWriterUtil.getDatabaseProductName(resultSet);

	    ColumnInfoCreator columnInfoCreator = new ColumnInfoCreator(resultSet, productName);
	    List<Integer> columnTypeList = columnInfoCreator.getColumnTypeList();
	    List<String> columnTypeNameList = columnInfoCreator.getColumnTypeNameList();
	    List<String> columnNameList = columnInfoCreator.getColumnNameList();
	    List<String> columnTableList = columnInfoCreator.getColumnTableList();

	    writeResultSetMetaData(resultSet);
	    writeColumnTypes(columnTypeList);

	    gen.writeStartArray("query_rows").writeStartObject();

	    int row_count = 0;
	    // Loop result Set
	    while (resultSet.next()) {

		row_count++;
		gen.writeStartArray("row_" + row_count);

		for (int i = 0; i < columnTypeList.size(); i++) {
		    int columnIndex = i + 1;
		    int columnType = columnTypeList.get(i);
		    String columnTypeName = columnTypeNameList.get(i);
		    String columnName = columnNameList.get(i);
		    String columnTable = columnTableList.get(i);

		    debugColumnInfo(columnIndex, columnType, columnTypeName, columnName, columnTable);

		    Object columnValue = null;
		    String columnValueStr = null;

		    BinaryColumnFormater binaryColumnFormater = new BinaryColumnFormater(request, resultSet, productName, columnType, columnIndex, columnName);
		    if (binaryColumnFormater.isBinaryColumn()) {
			debug("isBinaryColumn: true");
			columnValueStr = binaryColumnFormater.formatAndReturnId();
			debug("isBinaryColumn:columnValueStr: " + columnValueStr);
		    } else if (ResultSetWriterUtil.isNStringColumn(columnType)) {
			columnValue = resultSet.getNString(columnIndex);
			columnValueStr = ResultSetWriterUtil.treatNullValue(resultSet, columnValue);
		    } else if (isClobColumn(columnType)) {
			ClobColumnFormater clobColumnFormater = new ClobColumnFormater(request, resultSet, columnIndex);
			columnValueStr = clobColumnFormater.formatAndReturnId();
		    } else if (columnType == Types.ARRAY) {
			columnValueStr = ResultSetWriterUtil.formatArrayColumn(resultSet, columnIndex);
		    } else if (ResultSetWriterUtil.isDateTime(columnType)) {
			columnValueStr = ResultSetWriterUtil.formatDateTimeColumn(resultSet, columnType, columnIndex);
		    } else if (columnType == Types.ROWID) {
			columnValueStr = ResultSetWriterUtil.formatRowIdColumn(request, resultSet, columnIndex);
		    } else {
			try {
			    columnValue = resultSet.getObject(columnIndex);
			    debug("columnValue: " + columnValue);

			} catch (Exception e) {
			    throw new SQLException(columnType + "Type/TypeName/ColName " + columnTypeNameList.get(i)
				    + " " + columnName, e);
			}
			columnValueStr = ResultSetWriterUtil.treatNullValue(resultSet, columnValue);
		    }

		    debug("columnValueStr : " + columnValueStr);
		    gen.writeStartObject();
		    writeColumn(columnName, columnValue, columnValueStr);
		    gen.writeEnd();
		}

		gen.writeEnd(); // line_i
		
		// Allow to flush on each row... If required by environment...
		if (StaticParms.FLUSH_EACH_RESULT_SET_ROW) {
		    gen.flush(); 
		}
	    }

	    gen.writeEnd(); // .writeStartObject();
	    gen.writeEnd(); // .writeStartArray("query_rows")

	    gen.write("row_count", row_count);
	    
	    gen.flush(); // Final flush only

	} finally {
	    try {
		if (resultSet != null) {
		    resultSet.close();
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    // NO! IOUtils.closeQuietly(out);
	}
    }

    /**
     * Stores in Json the ResultSetMetaData
     * @throws SQLException
     *
     */
    private void writeResultSetMetaData(ResultSet resultSet) throws SQLException {
	if (fillResultSetMetaData) {
	    ResultSetMetaDataBuilder resultSetMetaDataBuilder = new ResultSetMetaDataBuilder(resultSet);
	    ResultSetMetaDataHolder resultSetMetaDataHolder = resultSetMetaDataBuilder.getResultSetMetaDataHolder();

	    String jsonString = GsonWsUtil.getJSonString(resultSetMetaDataHolder);
	    gen.write("ResultSetMetaData", jsonString);
	}
    }

    /**
     * @param columnName
     * @param columnValue
     * @param columnValueStr
     * @throws NumberFormatException
     */
    private void writeColumn(String columnName, Object columnValue, String columnValueStr)
	    throws NumberFormatException {
	if (StringUtils.isNumeric(columnValueStr)) {

	if (columnValue instanceof Integer) {
	    gen.write(columnName, Integer.parseInt(columnValueStr));
	} else if (columnValue instanceof Double) {
	    gen.write(columnName, Double.parseDouble(columnValueStr));
	} else if (columnValue instanceof Float) {
	    gen.write(columnName, Float.parseFloat(columnValueStr));
	} else if (columnValue instanceof Long) {
	    gen.write(columnName, Long.parseLong(columnValueStr));
	} else if (columnValue instanceof BigDecimal) {
	    gen.write(columnName, new BigDecimal(columnValueStr));
	} else {
	    gen.write(columnName, columnValueStr);
	}

	} else {
	gen.write(columnName, columnValueStr);
	}
    }

    /**
     * @param columnIndex
     * @param columnType
     * @param columnTypeName
     * @param columnName
     * @param columnTable
     */
    public void debugColumnInfo(int columnIndex, int columnType, String columnTypeName, String columnName,
	    String columnTable) {
	debug("");
	debug("columnIndex    : " + columnIndex);
	debug("columnType     : " + columnType);
	debug("columnTypeName : " + columnTypeName);
	debug("columnName     : " + columnName);
	debug("columnTable    : " + columnTable);
    }


    /**
     * Write the column types. Maybe required by client SDKs.
     *
     * @param columnTypeList
     */
    private void writeColumnTypes(List<Integer> columnTypeList) {
	if (doColumnTypes) {
	    gen.writeStartArray("column_types");
	    for (int i = 0; i < columnTypeList.size(); i++) {
		int columnType = columnTypeList.get(i);
		gen.write(JavaSqlConversion.fromJavaToSql(columnType));
	    }
	    gen.writeEnd();
	}
    }

    /**
     * return true if the column is a Types.CLOB || Types.NCLOB
     *
     * @param columnType the sql column type
     * @return true if it's (N)CLOB
     */
    private boolean isClobColumn(int columnType) {

	// 18/11/11 23:20 NDP : ResultSetWriter: no file dump for
	// ResultSetMetaData queries
	if (sqlOrder.equals("ResultSetMetaData")) {
	    return false;
	}

	return columnType == Types.CLOB || columnType == Types.LONGVARCHAR || columnType == Types.NCLOB;
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
