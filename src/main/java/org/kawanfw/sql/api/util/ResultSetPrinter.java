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
package org.kawanfw.sql.api.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kawanfw.sql.transport.TransportConverter;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * 
 * Tool to display the content of any JDBC <code>ResultSet</code> on a print
 * stream. <br>
 * (Code extracted/adapted from
 * <a href="http://shop.oreilly.com/product/9781565926165.do">Oreilly JDBC
 * book</a>). <br>
 * <br>
 * Limitations:
 * <ul>
 * <li>Text/CLOB columns display is limited to the first line of text.</li>
 * <li>Binary/BLOB columns are not displayed.</li>
 * </ul>
 * <br>
 * Example on our test database
 * <a href="http://www.aceql.com/soft/4.0/src/kawansoft_example.txt"
 * >kawansoft_example</a>: <blockquote>
 * 
 * <pre>
 * String sql = &quot;select * from customer limit 5&quot;;
 * PreparedStatement preparedStatement = connection.prepareStatement(sql);
 * ResultSet rs = preparedStatement.executeQuery();
 * 
 * // Print the result set on System.out:
 * ResultSetPrinter resultSetPrinter = new ResultSetPrinter(rs, System.out);
 * resultSetPrinter.print();
 * 
 * rs.first();
 * System.out.println();
 * 
 * // Print the result set on System.out with CSV format:
 * resultSetPrinter = new ResultSetPrinter(rs, System.out, true);
 * resultSetPrinter.print();
 * 
 * preparedStatement.close();
 * rs.close();
 * </pre>
 * 
 * </blockquote> Will produce the following output: <blockquote>
 * 
 * <pre>
 * customer_id | customer_title | fname | lname    | addressline      | town        | zipcode | phone       
 * ----------------------------------------------------------------------------------------------------------
 *           1 | Sir            | John  | Smith_1  | 1, César Avenue  | JavaLand_1  | 145     | 1-12345678  
 *           2 | Sir            | John  | Smith_2  | 2, César Avenue  | JavaLand_2  | 245     | 2-12345678  
 *           3 | Sir            | John  | Smith_3  | 3, César Avenue  | JavaLand_3  | 345     | 3-12345678  
 *           4 | Sir            | John  | Smith_4  | 4, César Avenue  | JavaLand_4  | 445     | 4-12345678  
 *           5 | Sir            | John  | Smith_5  | 5, César Avenue  | JavaLand_5  | 545     | 5-12345678  
 * (5 rows)
 * &nbsp;
 * customer_id;customer_title;fname;lname;addressline;town;zipcode;phone
 * 1;Sir;John;Smith_1;1, César Avenue;JavaLand_1;145;1-12345678
 * 2;Sir;John;Smith_2;2, César Avenue;JavaLand_2;245;2-12345678
 * 3;Sir;John;Smith_3;3, César Avenue;JavaLand_3;345;3-12345678
 * 4;Sir;John;Smith_4;4, César Avenue;JavaLand_4;445;4-12345678
 * 5;Sir;John;Smith_5;5, César Avenue;JavaLand_5;545;5-12345678
 * 
 * </pre>
 * 
 * </blockquote>
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */
public class ResultSetPrinter {
    /** Debug Value */
    private static boolean DEBUG = FrameworkDebug.isSet(ResultSetPrinter.class);

    /** Universal and clean line separator */
    private static String CR_LF = System.getProperty("line.separator");

    /**
     * The map than contains per column number (starts at 1) the maximum
     * required length
     */
    private Map<Integer, Integer> columnWidth = new ConcurrentHashMap<Integer, Integer>();

    /** The Result Set to process */
    private ResultSet resultSet = null;

    /** The print stream to use */
    private PrintStream printStream = null;

    /**
     * if true, result will be formatted for CSV format with ";" separator
     * instead of "|"
     */
    private boolean formatForCSV = false;

    /**
     * Constructor to use to display a human readable or a CSV formatted
     * <code>ResultSet</code>
     * 
     * @param resultSet
     *            the result set to display
     * @param printStream
     *            the print stream to use for display
     * @param formatForCSV
     *            if true, result will be formatted for CSV format with ";"
     *            separator instead of "|"
     */
    public ResultSetPrinter(ResultSet resultSet, PrintStream printStream,
	    boolean formatForCSV) {

	if (resultSet == null) {
	    throw new IllegalArgumentException("resultSet can not be null!");
	}

	if (printStream == null) {
	    throw new IllegalArgumentException("printStream can not be null!");
	}

	this.resultSet = resultSet;
	this.printStream = printStream;
	this.formatForCSV = formatForCSV;
    }

    /**
     * Constructor to use to display a human readable <code>ResultSet</code>.
     * 
     * @param resultSet
     *            the result set to display
     * @param printStream
     *            the print stream to use for display
     */
    public ResultSetPrinter(ResultSet resultSet, PrintStream printStream) {
	this(resultSet, printStream, false);
    }

    /**
     * Computes the column max width.
     */
    private void computeColumnMaxWidth() throws SQLException {
	ResultSetMetaData meta = resultSet.getMetaData();
	int cols = meta.getColumnCount();

	for (int i = 1; i <= cols; i++) {
	    String label = meta.getColumnLabel(i);
	    label = label.trim();
	    // int size = meta.getColumnDisplaySize(i);
	    int length = label.length();

	    columnWidth.put(i, length + 2);
	}

	// Formatter chaque ligne de l�ensemble r�sultat et l�ajouter.
	while (resultSet.next()) {

	    // Formatter chaque colonne de la ligne.
	    for (int i = 1; i <= cols; i++) {
		Object value = resultSet.getObject(i);

		String strNoTrim = null;

		if (resultSet.wasNull()) {
		    strNoTrim = "NULL";
		} else {
		    if (value == null) {
			strNoTrim = "";
		    } else {
			strNoTrim = value.toString();
		    }
		}

		// Trim, because of some SQL (Postgres,...) behavior
		String str = getFirstLineOfText(strNoTrim);
		str = str.trim();

		debug("str:" + str);

		int colType = meta.getColumnType(i);
		if (str.startsWith(
			TransportConverter.KAWANFW_BYTES_STREAM_FILE)) {
		    if (isBinaryColumn(colType) || colType == Types.BIGINT) {
			str = getBinaryDataMessage();
		    }
		}

		int length = str.length();

		/*
		 * if (columnWidth.get(i) == null) { columnWidth.put(i, length +
		 * 2); } else { int storedLength = columnWidth.get(i); if
		 * (length > storedLength) { columnWidth.put(i, length + 2); } }
		 */

		if (length + 2 > columnWidth.get(i)) {
		    columnWidth.put(i, length + 2);
		}
	    }
	}
    }

    /**
     * @return the binary data message depending on android
     */
    public String getBinaryDataMessage() {
	return "(binary data)";
    }

    /**
     * Prints the first line of a text
     * 
     * @param str
     *            the text
     * @return the first line of the text
     * @throws SQLException
     */
    private String getFirstLineOfText(String str) throws SQLException {
	BufferedReader bufferedReader = new BufferedReader(
		new StringReader(str));

	String line = null;

	try {
	    line = bufferedReader.readLine();
	} catch (IOException e) {
	    throw new SQLException(e);
	}

	return line;
    }

    /**
     * Prints the Result Set.
     */
    public void print() throws SQLException {
	resultSet.beforeFirst();
	computeColumnMaxWidth(); // Compute the column max width
	resultSet.beforeFirst(); // reset the read Result Set

	ResultSetMetaData meta = resultSet.getMetaData();
	StringBuffer bar = new StringBuffer();
	StringBuffer buffer = new StringBuffer();
	int cols = meta.getColumnCount();

	int row_count = 0;
	int i, width = 0;

	// Prepare header of each column
	// Display will look like:
	// --------------------------------------
	// | Column one | Column two |
	// --------------------------------------
	// | Value line 1 | Value line 1 |
	// --------------------------------------

	// Create bar who length is total of all columns
	for (i = 1; i <= cols; i++) {
	    // width += meta.getColumnDisplaySize(i);
	    width += columnWidth.get(i);
	}
	width += 1 + cols;
	for (i = 0; i < width - 2; i++) {
	    bar.append('-');
	}

	// bar.append(CR_LF);

	// buffer.append(bar.toString() + CR_LF + "|");
	// buffer.append(bar.toString() + CR_LF);

	// After firt libe bare: display column titles
	for (i = 1; i <= cols; i++) {

	    String label = meta.getColumnLabel(i);
	    String colTypeName = meta.getColumnTypeName(i);
	    int colType = meta.getColumnType(i);

	    debug("");

	    // int size = meta.getColumnDisplaySize(i);
	    int size = columnWidth.get(i);

	    debug("Column Label     : " + label);
	    debug("Column size      : " + size);
	    debug("Column type      : " + colType);
	    debug("Column type name : " + colTypeName);

	    if (!formatForCSV) {
		label = fillHeaderWithBlanks(label, size);

		// Add column headers to buffer
		buffer.append(label);
		if (i < cols) {
		    buffer.append("|");
		}
	    } else {
		buffer.append(label);
		if (i < cols) {
		    buffer.append(";");
		}
	    }

	}

	// Display underbar
	if (!formatForCSV) {
	    buffer.append(CR_LF + bar.toString());
	}

	String bufferheader = buffer.toString();

	printStream.println(bufferheader);

	// Formatter chaque ligne de l�ensemble r�sultat et l�ajouter.
	while (resultSet.next()) {
	    buffer = new StringBuffer();

	    row_count++;

	    // buffer.append('|');

	    // Formatter chaque colonne de la ligne.
	    for (i = 1; i <= cols; i++) {

		Object value = resultSet.getObject(i);
		int colType = meta.getColumnType(i);

		// int size = meta.getColumnDisplaySize(i);
		int size = columnWidth.get(i);

		String strNoTrim = null;

		if (resultSet.wasNull()) {
		    strNoTrim = "NULL";
		} else {
		    if (value == null) {
			strNoTrim = "";
		    } else {
			strNoTrim = value.toString();
		    }
		}

		// Trim, because of some SQL (Postgres,...) behavior
		// String strCSV = strNoTrim.trim();

		String str = getFirstLineOfText(strNoTrim);
		str = str.trim();

		if (str.startsWith(
			TransportConverter.KAWANFW_BYTES_STREAM_FILE)) {
		    if (isBinaryColumn(colType) || colType == Types.BIGINT) {
			str = getBinaryDataMessage();
		    }
		}

		// NDP: Commented, don't know why it remained?
		// if (meta.getColumnType(i) == Types.TIMESTAMP)
		// {
		// // Fix for timestamp display, because size is limited to
		// first 14:
		// str = str.substring(2);
		// }

		if (!formatForCSV) {
		    str = fillValueWithBlanks(size, str, colType);

		    // buffer.append(str + "|");
		    buffer.append(str);

		    if (i < cols) {
			buffer.append("|");
		    }
		} else {
		    str = str.replace(';', ',');
		    buffer.append(str);

		    if (i < cols) {
			buffer.append(";");
		    }
		}

	    }

	    String bufferLine = buffer.toString();
	    printStream.println(bufferLine);

	}

	if (!formatForCSV) {
	    String query_trailer = new String();
	    query_trailer = "(" + row_count + " rows)";
	    printStream.println(query_trailer);
	}

    }

    /**
     * Returns true if the column is a binary type
     * 
     * @param columnType
     *            the sql column type
     * @param columnName
     *            the sql column name
     * @param columnTable
     *            the table name of the column
     * @return true if it's a binary type
     */
    private static boolean isBinaryColumn(int columnType) {
	if (columnType == Types.BINARY || columnType == Types.VARBINARY
		|| columnType == Types.LONGVARBINARY
		|| columnType == Types.BLOB) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Returns true if column is numeric
     * 
     * @param colType
     *            the meta column type
     * @return true if column is numeric
     */
    private static boolean isColumnNumeric(int colType) {

	if (colType == Types.TINYINT || colType == Types.SMALLINT
		|| colType == Types.INTEGER || colType == Types.BIGINT
		|| colType == Types.FLOAT || colType == Types.REAL
		|| colType == Types.NUMERIC || colType == Types.DECIMAL) {
	    return true;
	} else {
	    return false;
	}

    }

    /**
     * Fills the value of the result set column with surrounding blanks.
     * 
     * @param size
     *            the size to fill with blanks
     * @param str
     *            the column value
     * @param colType
     *            TODO
     * @return the string filled with blank
     */
    private String fillValueWithBlanks(int size, String str, int colType) {
	StringBuffer filler = new StringBuffer();
	if (str.length() > size) {
	    str = str.substring(0, size);
	}

	if (str.length() < size) {
	    int j, x;

	    // x = (size-str.length())/2;
	    x = 1;

	    for (j = 0; j < x; j++) {
		filler.append(" ");
	    }

	    str = filler + str + filler;

	    if (str.length() > size) {
		str = str.substring(0, size);
	    } else {
		while (str.length() < size) {

		    if (isColumnNumeric(colType)) {
			str = " " + str; // blanks appended left because of
					 // numbers
		    } else {
			str = str + " ";
		    }
		}
	    }
	}
	return str;
    }

    /**
     * Fills the header with surrounding blanks.
     * 
     * @param label
     *            the header label
     * @param size
     *            the size to fill with blanks
     * @return the header filled with surrounding blanks
     */
    private String fillHeaderWithBlanks(String label, int size) {
	StringBuffer filler = new StringBuffer();
	int x;

	// Si le titre est plus long que la largeur de la colonne,
	// tronquer son contenu.

	if (label.length() > size) {
	    label = label.substring(0, size);
	}

	// Si le titre est moins long que la largeur de la colonne,
	// ajouter des espaces.
	if (label.length() < size) {
	    int j;

	    // x = (size-label.length())/2;
	    x = 1;

	    for (j = 0; j < x; j++) {
		filler.append(' ');
	    }

	    label = filler + label + filler;

	    if (label.length() > size) {
		label = label.substring(0, size);
	    } else {
		while (label.length() < size) {
		    label += " ";
		}
	    }
	}
	return label;
    }

    private static void debug(String s) {
	if (DEBUG) {
	    System.out.println(s);
	}
    }
}
