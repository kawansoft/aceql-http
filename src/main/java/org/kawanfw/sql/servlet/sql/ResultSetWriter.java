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
import java.net.URL;
import java.sql.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

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

    private static final String NULL_STREAM = "NULL_STREAM";

    private static boolean DEBUG = FrameworkDebug.isSet(ResultSetWriter.class);

    public static String CR_LF = System.getProperty("line.separator");

    /** Writer for the result set */
    private OutputStream out = null;

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
     * @param out      the buffered Writer for the result set
     * @param username the client username
     * @param sqlOrder the sql order
     * @param gen      The JSon Generator
     */
    public ResultSetWriter(HttpServletRequest request, OutputStream out, String username, String sqlOrder,
	    JsonGenerator gen) {
	this.out = out;

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
    public ResultSetWriter(OutputStream out, String sqlOrder) {
	super();
	this.out = out;
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

	    String productName = getDatabaseProductName(resultSet);
	    isTerradata = productName.equals(SqlUtil.TERADATA) ? true : false;
	    isPostgreSQL = productName.equals(SqlUtil.POSTGRESQL) ? true : false;

	    ResultSetMetaData meta = resultSet.getMetaData();
	    int cols = meta.getColumnCount();

	    int row_count = 0;

	    List<Integer> columnTypeList = new Vector<Integer>();
	    List<String> columnTypeNameList = new Vector<String>();
	    List<String> columnNameList = new Vector<String>();
	    List<String> columnTableList = new Vector<String>();

	    // Loop on Columns
	    for (int i = 1; i <= cols; i++) {
		columnTypeList.add(meta.getColumnType(i));
		columnNameList.add(meta.getColumnName(i).toLowerCase());
		columnTypeNameList.add(meta.getColumnTypeName(i));

		if (isPostgreSQL) {
		    columnTableList.add(PostgreSqlUtil.getTableName(resultSet, i));
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

	    if (doColumnTypes) {
		gen.writeStartArray("column_types");
		for (int i = 0; i < columnTypeList.size(); i++) {
		    int columnType = columnTypeList.get(i);
		    gen.write(JavaSqlConversion.fromJavaToSql(columnType));
		}
		gen.writeEnd();
	    }

	    gen.writeStartArray("query_rows").writeStartObject();

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

		    debug("");
		    debug("columnIndex    : " + columnIndex);
		    debug("columnType     : " + columnType);
		    debug("columnTypeName : " + columnTypeName);
		    debug("columnName     : " + columnName);
		    debug("columnTable    : " + columnTable);

		    Object columnValue = null;
		    String columnValueStr = null;

		    if (isBinaryColumn(resultSet, columnType, columnName, columnTable)) {
			debug("isBinaryColumn: true");
			columnValueStr = formatBinaryColumn(resultSet, columnIndex, columnType, columnName,
				columnTable);
			debug("isBinaryColumn:columnValueStr: " + columnValueStr);
		    } else if (isNStringColumn(columnType)) {
			columnValueStr = resultSet.getNString(columnIndex);
			// columnValueStr =
			// HtmlConverter.toHtml(columnValueStr);
		    } else if (isClobColumn(columnType)) {
			columnValueStr = formatClobColumn(resultSet, columnIndex);
		    } else if (columnType == Types.ARRAY) {
			columnValueStr = formatArrayColumn(resultSet, columnIndex);
		    } else if (isDateTime(columnType)) {
			columnValueStr = formatDateTimeColumn(resultSet, columnType, columnIndex);
		    } else if (columnType == Types.ROWID) {
			columnValueStr = formatRowIdColumn(resultSet, columnIndex);
		    } else {
			try {
			    columnValue = resultSet.getObject(columnIndex);
			    debug("columnValue: " + columnValue);

			} catch (Exception e) {
			    // int intValue = resultSet.getInt(columnName);
			    debug("Exception     : " + e.toString());
			    debug("columnType    : " + columnType);
			    debug("columnTypeName: " + columnTypeNameList.get(i));
			    debug("columnName    : " + columnName);
			    throw new SQLException(columnType + "Type/TypeName/ColName " + columnTypeNameList.get(i)
				    + " " + columnName, e);
			}

			if (resultSet.wasNull()) {
			    columnValueStr = "NULL";
			} else if (columnValue == null) {
			    columnValueStr = null;
			} else {
			    columnValueStr = columnValue.toString();
			}

		    }

		    debug("columnValueStr : " + columnValueStr);

		    // Case we - maybe - have an URL:
		    columnValueStr = urlFormater(resultSet, columnIndex, columnValueStr);

		    // if (isCharacterType(columnType)) {
		    // debugStringType(columnValueStr);
		    // columnValueStr = HtmlConverter
		    // .toHtml(columnValueStr);
		    // debug("columnValueStr HTML encoded: " + columnValueStr);
		    // }

		    gen.writeStartObject();

		    if (StringUtils.isNumeric(columnValueStr)) {

			// if (columnValue instanceof Integer) {
			// gen.write(columnName, new
			// Integer(columnValueStr).intValue());
			// } else if (columnValue instanceof Double) {
			// gen.write(columnName, new
			// Double(columnValueStr).doubleValue());
			// } else if (columnValue instanceof Float) {
			// gen.write(columnName, new
			// Float(columnValueStr).floatValue());
			// } else if (columnValue instanceof Long) {
			// gen.write(columnName, new
			// Long(columnValueStr).longValue());
			// } else if (columnValue instanceof BigDecimal) {
			// gen.write(columnName, new
			// BigDecimal(columnValueStr));
			// } else {
			// gen.write(columnName, columnValueStr);
			// }

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
	    }

	    gen.writeEnd(); // .writeStartObject();
	    gen.writeEnd(); // .writeStartArray("query_rows")

	    gen.write("row_count", row_count);

	    ServerSqlManager.writeLine(out);

	} finally {
	    resultSet.close();
	    // NO! IOUtils.closeQuietly(out);
	}
    }

    private boolean isDateTime(int columnType) {
	if (columnType == Types.DATE || columnType == Types.TIME || columnType == Types.TIMESTAMP) {
	    return true;
	} else {
	    return false;
	}
    }

    private String formatDateTimeColumn(ResultSet rs, int columnType, int columnIndex) throws SQLException {
	if (columnType == Types.DATE) {
	    Date date = rs.getDate(columnIndex);
	    long milliseconds = date.getTime();

	    // return new Long(milliseconds).toString();
	    return Long.toString(milliseconds);

	} else if (columnType == Types.TIME) {
	    Time time = rs.getTime(columnIndex);
	    long milliseconds = time.getTime();

	    // return new Long(milliseconds).toString();
	    return Long.toString(milliseconds);
	}
	if (columnType == Types.TIMESTAMP) {
	    Timestamp time = rs.getTimestamp(columnIndex);
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

	if (columnType == Types.SMALLINT || columnType == Types.INTEGER || columnType == Types.NUMERIC
		|| columnType == Types.DECIMAL || columnType == Types.BIGINT || columnType == Types.REAL
		|| columnType == Types.FLOAT || columnType == Types.DOUBLE) {
	    return true;
	} else {
	    return false;
	}

    }

    // /**
    // * @param columnValueStr
    // * @throws UnsupportedEncodingException
    // */
    // public void debugStringType(String columnValueStr)
    // throws UnsupportedEncodingException {
    // if (DEBUG) {
    // byte[] utf8 = columnValueStr.getBytes("UTF-8");
    // String hexString = CodecHex.encodeHexString(utf8);
    // String sBase64 = Base64.byteArrayToBase64(utf8);
    //
    // System.out.println();
    // System.out.println("columnValueStr : " + columnValueStr);
    // System.out.println("columnValueStrHtml: " + columnValueStr);
    // System.out.println("hexString : " + hexString);
    // System.out.println("sBase64 : " + sBase64);
    // }
    // }

    /**
     * Says if a column is N Type
     * 
     * @param columnType the SQL Column Type
     * @return true if a column is N Type
     */
    private boolean isNStringColumn(int columnType) {
	if (columnType == Types.NCHAR || columnType == Types.NVARCHAR || columnType == Types.LONGNVARCHAR) {
	    return true;
	} else {
	    return false;
	}
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
    private String formatArrayColumn(ResultSet resultSet, int columnIndex) throws SQLException, IOException {
	Array array = resultSet.getArray(columnIndex);

	Object[] objects = (Object[]) array.getArray();
	String arrayStr = "{";
	for (int i = 0; i < objects.length; i++) {
	    arrayStr += objects[i] + ",";
	}

	if (arrayStr.contains(",")) {
	    arrayStr = StringUtils.substringBeforeLast(arrayStr, ",");
	}

	arrayStr += "}";
	return arrayStr;

	/*
	 * ArrayHttp arrayHttp = new ArrayHttp(array); ArrayTransporter arrayTransporter
	 * = new ArrayTransporter(); String base64 =
	 * arrayTransporter.toBase64(arrayHttp); return base64;
	 */
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
     * Format - if detected - an URL
     * 
     * @param resultSet
     * @param columnIndex
     * @param columnValueStr
     * @return
     */
    public String urlFormater(ResultSet resultSet, int columnIndex, String columnValueStr) {

	try {
	    URL url = resultSet.getURL(columnIndex);
	    if (url != null) {
		// Its an URL!
		// UrlTransporter urlTransporter = new UrlTransporter();
		// columnValueStr = urlTransporter.toBase64(url);
		columnValueStr = url.toString();
	    }

	} catch (Exception e) {
	    // Do nothing. It's not an URL
	}

	return columnValueStr;
    }

    /**
     * Returns true if engine is terradata
     * 
     * @param resultSet the result set in use
     * @returns true if engine is terradata
     * @throws SQLException
     */
    private String getDatabaseProductName(ResultSet resultSet) throws SQLException {

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
    @SuppressWarnings("unused")
    private boolean isCharacterType(int columnType) {
	if (columnType == Types.CHAR || columnType == Types.NCHAR || columnType == Types.VARCHAR
		|| columnType == Types.NVARCHAR || columnType == Types.LONGVARCHAR
		|| columnType == Types.LONGNVARCHAR) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * the binary content is dumped in a server file that will be available for the
     * client the name of the file will be stored in the output stream ;
     * 
     * @param resultSet   the result set in progress to send back to the client side
     * @param columnIndex the column index
     * @param columnType  the column type
     * @param columnName  the column name
     * @param columnTable the table name of the column
     * @return the formated binary column
     * 
     * @throws SQLException
     */
    private String formatBinaryColumn(ResultSet resultSet, int columnIndex, int columnType, String columnName,
	    String columnTable) throws SQLException, IOException {
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
     * @param columnTable the table name of the column
     * @return true if it's a binary type
     */
    private boolean isBinaryColumn(ResultSet resultSet, int columnType, String columnName, String columnTable)
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

	if (columnType == Types.CLOB || columnType == Types.LONGVARCHAR || columnType == Types.NCLOB) {
	    return true;
	} else {
	    return false;
	}
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
