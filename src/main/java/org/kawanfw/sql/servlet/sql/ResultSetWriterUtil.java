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
package org.kawanfw.sql.servlet.sql;

import java.io.IOException;
import java.net.URL;
import java.sql.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import javax.servlet.http.HttpServletRequest;

import org.kawanfw.sql.api.util.SqlUtil;
import org.kawanfw.sql.jdbc.metadata.AceQLArray;
import org.kawanfw.sql.metadata.util.GsonWsUtil;
import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.connection.ConnectionStore;
import org.kawanfw.sql.util.SqlReturnCode;

public class ResultSetWriterUtil {

    private static final String NULL = "NULL";

    /**
     * If wa have a ResultSet.wasNull() ==> value is "NULL" for transport to client
     * side.
     *
     * @param resultSet
     * @param columnValue
     * @return
     * @throws SQLException
     */
    public static String treatNullValue(ResultSet resultSet, Object columnValue) throws SQLException {
	String columnValueStr;
	if (resultSet.wasNull()) {
	    columnValueStr = NULL;
	} else if (columnValue == null) {
	    columnValueStr = null;
	} else {
	    columnValueStr = columnValue.toString();
	}
	return columnValueStr;
    }

    public static boolean isDateTime(int columnType) {
	return columnType == Types.DATE || columnType == Types.TIME || columnType == Types.TIMESTAMP;
    }

    public static String formatDateTimeColumn(ResultSet rs, int columnType, int columnIndex) throws SQLException {
	if (columnType == Types.DATE) {
	    Date date = rs.getDate(columnIndex);

	    if (date == null) {
		return NULL;
	    }

	    long milliseconds = date.getTime();

	    // return new Long(milliseconds).toString();
	    return Long.toString(milliseconds);
	} else if (columnType == Types.TIME) {
	    Time time = rs.getTime(columnIndex);

	    if (time == null) {
		return NULL;
	    }

	    long milliseconds = time.getTime();

	    // return new Long(milliseconds).toString();
	    return Long.toString(milliseconds);
	}
	if (columnType == Types.TIMESTAMP) {
	    Timestamp time = rs.getTimestamp(columnIndex);

	    if (time == null) {
		return NULL;
	    }

	    long milliseconds = time.getTime();

	    // return new Long(milliseconds).toString();
	    return Long.toString(milliseconds);
	} else {
	    throw new IllegalArgumentException("columnType is not a Time/Timestamp: " + columnType);
	}
    }

    // NUMERIC java.math.BigDecimal
    // DECIMAL java.math.BigDecimal
    // BIGINT long Long
    // REAL float Float
    // FLOAT double Double
    // DOUBLE PRECISION double Double
    public static boolean isNumericType(int columnType) {
	return columnType == Types.SMALLINT || columnType == Types.INTEGER || columnType == Types.NUMERIC
		|| columnType == Types.DECIMAL || columnType == Types.BIGINT || columnType == Types.REAL
		|| columnType == Types.FLOAT || columnType == Types.DOUBLE;

    }

    /**
     * Says if a column is N Type
     *
     * @param columnType the SQL Column Type
     * @return true if a column is N Type
     */
    public static boolean isNStringColumn(int columnType) {
	return columnType == Types.NCHAR || columnType == Types.NVARCHAR || columnType == Types.LONGNVARCHAR;
    }


    /**
     * Format - if detected - an URL
     *
     * @param resultSet
     * @param columnIndex
     * @param columnValueStr
     * @return
     */
    public static String urlFormater(ResultSet resultSet, int columnIndex, final String columnValueStr) {

	String columnValueStrNew =  columnValueStr;

	try {
	    URL url = resultSet.getURL(columnIndex);
	    if (url != null) {
		// Its an URL!
		// UrlTransporter urlTransporter = new UrlTransporter();
		// columnValueStr = urlTransporter.toBase64(url);
		columnValueStrNew = url.toString();
	    }

	} catch (Exception e) {
	    // Do nothing. It's not an URL
	}

	return columnValueStrNew;
    }

    /**
     * Returns true if engine is terradata
     *
     * @param resultSet the result set in use
     * @returns true if engine is terradata
     * @throws SQLException
     */
    public static String getDatabaseProductName(ResultSet resultSet) throws SQLException {

	Statement statement = resultSet.getStatement();

	// happens on Metadata requests, we don' care about the result:
	if (statement == null) {
	    return "unknown";
	} else {
	    Connection connection = statement.getConnection();
	    return new SqlUtil(connection).getDatabaseProductName();
	}
    }

    /**
     * Says if a columns is char/string type
     *
     * @param columnType
     * @return
     */
    public static boolean isCharacterType(int columnType) {
	return columnType == Types.CHAR || columnType == Types.NCHAR || columnType == Types.VARCHAR
		|| columnType == Types.NVARCHAR || columnType == Types.LONGVARCHAR
		|| columnType == Types.LONGNVARCHAR;
    }

    /**
     * Format the column as an java.sqlArray
     *
     * @param resultSet
     * @param columnIndex
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public static String formatArrayColumn(ResultSet resultSet, int columnIndex) throws SQLException, IOException {
	Array array = resultSet.getArray(columnIndex);
	AceQLArray aceQLArray = new AceQLArray(array);
	String jsonString = GsonWsUtil.getJSonString(aceQLArray);
	return jsonString;
    }

    /**
     * Format the column as a RowId
     * @param request
     * @param resultSet
     * @param columnIndex
     *
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public static String formatRowIdColumn(HttpServletRequest request, ResultSet resultSet, int columnIndex) throws SQLException, IOException {
        RowId rowId = resultSet.getRowId(columnIndex);

        if (rowId == null) {
            return ResultSetWriter.NULL;
        }

        String username = request.getParameter(HttpParameter.USERNAME);
        String sessionId = request.getParameter(HttpParameter.SESSION_ID);
        String connectionId = request.getParameter(HttpParameter.CONNECTION_ID);

        ConnectionStore connectionStore = new ConnectionStore(username, sessionId, connectionId);
        Connection connection = connectionStore.get();

        if (connection == null) {
            throw new SQLException(SqlReturnCode.SESSION_INVALIDATED);
        }

        connectionStore.put(rowId);

        return rowId.toString();
        // Old code:
        // RowIdHttp rowIdHttp = new RowIdHttp(rowId.hashCode(),
        // rowId.getBytes());
        //
        // RowIdTransporter rowIdTransporter = new RowIdTransporter();
        // String base64 = rowIdTransporter.toBase64(rowIdHttp);
        // return base64;
    }


}
