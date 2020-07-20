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
package org.kawanfw.sql.servlet.sql;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Set;

import javax.json.stream.JsonGenerator;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.util.SqlUtil;
import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.servlet.connection.ConnectionStore;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.FrameworkFileUtil;
import org.kawanfw.sql.util.HtmlConverter;
import org.kawanfw.sql.util.SqlReturnCode;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ResultSetWriter {

    private static final String NULL = "NULL";

    private static final String NULL_STREAM = "NULL_STREAM";

    private static boolean DEBUG = FrameworkDebug.isSet(ResultSetWriter.class);

    public static String CR_LF = System.getProperty("line.separator");

    /** The username in use */
    private String username = null;

    /** the sql order */
    private String sqlOrder = null;

    /** The html encoding to use for Clob downloads */
    private boolean htmlEncondingOn = true;

    /** Special treatment required for PostgreSQL for blobs */
    private boolean isPostgreSQL;

    /** Special treatment required for Terradata for blobs */
    private boolean isTerradata = false;

    /**
     * Says if ResultSet Meta Data must be downloaded from server along with
     * ResultSet
     */
    private boolean JoinResultSetMetaData = false;

    private HttpServletRequest request;

    /** The set of columns that are OID (Types.BIGINT) */
    private Set<String> typeBigIntColumnNames = null;

    private Boolean doColumnTypes = false;

    private JsonGenerator gen = null;

    /**
     * @param request  the http request
     * @param username the client username
     * @param sqlOrder the sql order
     * @param gen      The JSon Generator
     */
    public ResultSetWriter(HttpServletRequest request, String username, String sqlOrder, JsonGenerator gen) {

	this.username = username;
	this.sqlOrder = sqlOrder;

	this.request = request;
	this.gen = gen;

	String columnTypes = request.getParameter(HttpParameter.COLUMN_TYPES);
	// doColumnTypes= new Boolean(columnTypes);
	doColumnTypes = Boolean.parseBoolean(columnTypes);

	debug("JoinResultSetMetaData: " + JoinResultSetMetaData);

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
	    isTerradata = productName.equals(SqlUtil.TERADATA) ? true : false;
	    isPostgreSQL = productName.equals(SqlUtil.POSTGRESQL) ? true : false;

	    ColumnInfoCreator columnInfoCreator = new ColumnInfoCreator(resultSet, isPostgreSQL);
	    List<Integer> columnTypeList = columnInfoCreator.getColumnTypeList();
	    List<String> columnTypeNameList = columnInfoCreator.getColumnTypeNameList();
	    List<String> columnNameList = columnInfoCreator.getColumnNameList();
	    List<String> columnTableList = columnInfoCreator.getColumnTableList();

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

		    if (isBinaryColumn(resultSet, columnType, columnName)) {
			debug("isBinaryColumn: true");
			columnValueStr = formatBinaryColumn(resultSet, columnIndex, columnType);
			debug("isBinaryColumn:columnValueStr: " + columnValueStr);
		    } else if (ResultSetWriterUtil.isNStringColumn(columnType)) {
			columnValue = resultSet.getNString(columnIndex);
			columnValueStr = ResultSetWriterUtil.treatNullValue(resultSet, columnValue);

		    } else if (isClobColumn(columnType)) {
			columnValueStr = formatClobColumn(resultSet, columnIndex);
		    } else if (columnType == Types.ARRAY) {
			columnValueStr = ResultSetWriterUtil.formatArrayColumn(resultSet, columnIndex);
		    } else if (ResultSetWriterUtil.isDateTime(columnType)) {
			columnValueStr = ResultSetWriterUtil.formatDateTimeColumn(resultSet, columnType, columnIndex);
		    } else if (columnType == Types.ROWID) {
			columnValueStr = formatRowIdColumn(resultSet, columnIndex);
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

		    gen.writeEnd();
		}

		gen.writeEnd(); // line_i
		gen.flush();
	    }

	    gen.writeEnd(); // .writeStartObject();
	    gen.writeEnd(); // .writeStartArray("query_rows")

	    gen.write("row_count", row_count);

	    // ServerSqlManager.writeLine(out);

	} finally {
	    resultSet.close();
	    // NO! IOUtils.closeQuietly(out);
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
     * Format the column as a RowId
     *
     * @param resultSet
     * @param columnIndex
     * @return
     * @throws SQLException
     * @throws IOException
     */
    private String formatRowIdColumn(ResultSet resultSet, int columnIndex) throws SQLException, IOException {
	RowId rowId = resultSet.getRowId(columnIndex);

	if (rowId == null) {
	    return NULL;
	}

	String sessionId = request.getParameter(HttpParameter.SESSION_ID);
	String connectionId = request.getParameter(HttpParameter.CONNECTION_ID);

	ConnectionStore connectionStore = new ConnectionStore(username, sessionId, connectionId);
	Connection connection = connectionStore.get();

	if (connection == null) {
	    throw new SQLException(SqlReturnCode.SESSION_INVALIDATED);
	}

	connectionStore.put(rowId);

	return rowId.toString();
	// RowIdHttp rowIdHttp = new RowIdHttp(rowId.hashCode(),
	// rowId.getBytes());
	//
	// RowIdTransporter rowIdTransporter = new RowIdTransporter();
	// String base64 = rowIdTransporter.toBase64(rowIdHttp);
	// return base64;
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
     * the binary content is dumped in a server file that will be available for the
     * client the name of the file will be stored in the output stream ;
     *
     * @param resultSet   the result set in progress to send back to the client side
     * @param columnIndex the column index
     * @param columnType  the column type
     * @return the formated binary column
     *
     * @throws SQLException
     */
    private String formatBinaryColumn(ResultSet resultSet, int columnIndex, int columnType) throws SQLException, IOException {
	String columnValueStr;

	String fileName = FrameworkFileUtil.getUniqueId() + ".blob";

	// Maybe null, we want to keep the info
	InputStream in = null;
	if (isTerradata) {
	    in = resultSet.getBlob(columnIndex).getBinaryStream();
	}
	// For PostgreSQL columns OID columns have the BIGINT type
	else if (isPostgreSQL && columnType == Types.BIGINT) {
	    in = PostgreSqlUtil.getPostgreSqlnputStream(resultSet, columnIndex);
	} else {
	    in = resultSet.getBinaryStream(columnIndex);
	}

	String hostFileName = null;

	String database = request.getParameter(HttpParameter.DATABASE);
	DatabaseConfigurator databaseConfigurator = ServerSqlManager.getDatabaseConfigurator(database);
	hostFileName = databaseConfigurator.getBlobsDirectory(username) + File.separator + fileName;
	debug("formatBinaryColumn:outStream: " + hostFileName);

	try (OutputStream outStream = new BufferedOutputStream(new FileOutputStream(hostFileName));) {
	    if (in == null) {
		debug("formatBinaryColumn: in == null");

		// DO NOTHING: just closing will create an empty file
		outStream.write(NULL_STREAM.getBytes());

	    } else {
		IOUtils.copy(in, outStream);
	    }
	} catch (IOException e) {
	    throw new SQLException(e);
	} finally {
	    if (in != null) {
		try {
		    in.close();
		} catch (Exception e) {
		    // e.printStackTrace();
		}
	    }

	}

	// The column value is a file name with a tag for identification
	columnValueStr = fileName;

	return columnValueStr;
    }


    /**
     * return true if the column is a binary type
     *
     * @param resultSet   used to get back the Connection for PostgreSQL meta query
     * @param columnType  the sql column type
     * @param columnName  the sql column name
     * @return true if it's a binary type
     */
    private boolean isBinaryColumn(ResultSet resultSet, int columnType, String columnName)
	    throws SQLException, IOException {
	if (columnType == Types.BINARY || columnType == Types.VARBINARY || columnType == Types.LONGVARBINARY
		|| columnType == Types.BLOB) {
	    return true;
	} else {

	    // Special treatment for PostgreSQL OID which Java long/BIGINT type
	    if (isPostgreSQL && columnType == Types.BIGINT) {
		if (typeBigIntColumnNames == null) {
		    Connection connection = resultSet.getStatement().getConnection();
		    typeBigIntColumnNames = PostgreSqlUtil.getTypeBigIntColumnNames(connection);
		}

		if (typeBigIntColumnNames.contains(columnName.trim().toLowerCase())) {
		    return true;
		}
	    }

	    return false;
	}
    }

    /**
     * the CLOB content is dumped in a server file that will be available for the
     * client the name of the file will be stored in the output stream ;
     *
     * @param resultSet   the result set in progress to send back to the client side
     * @param columnIndex the column index
     *
     * @return the formated binary column
     *
     * @throws SQLException
     */
    private String formatClobColumn(ResultSet resultSet, int columnIndex) throws SQLException, IOException {
	String columnValueStr;

	String fileName = FrameworkFileUtil.getUniqueId() + ".clob.txt";

	// Maybe null, we want to keep the info
	Reader reader = resultSet.getCharacterStream(columnIndex);
	BufferedReader br = new BufferedReader(reader);

	String hostFileName = null;

	// hostFileName = HttpConfigurationUtil.addRootPath(fileConfigurator,
	// username, fileName);

	String database = request.getParameter(HttpParameter.DATABASE);
	DatabaseConfigurator databaseConfigurator = ServerSqlManager.getDatabaseConfigurator(database);

	hostFileName = databaseConfigurator.getBlobsDirectory(username) + File.separator + fileName;

	debug("formatClobColumn:writer: " + hostFileName);

	if (reader == null) {

	    try (Writer writer = new BufferedWriter(new FileWriter(hostFileName));) {
		debug("formatClobColumn.reader == null");
		writer.write(NULL_STREAM + CR_LF);
	    }
	} else {

	    writeClobFile(br, hostFileName);

	}

	// The column value is a file name with a tag for identification
	columnValueStr = fileName;
	return columnValueStr;
    }

    /**
     * Write a Clob file, html encoded or not
     *
     * @param br           the buffered reader on the clob
     * @param hostFileName the host file name to create in base 64
     */
    private void writeClobFile(BufferedReader br, String hostFileName) throws IOException {

	try (Writer writer = new BufferedWriter(new FileWriter(hostFileName));) {

	    String line = null;
	    while ((line = br.readLine()) != null) {

		if (this.htmlEncondingOn) {
		    line = HtmlConverter.fromHtml(line);
		}

		writer.write(line + CR_LF);
	    }
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
