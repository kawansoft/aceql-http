/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP                                     
 * Copyright (C) 2017,  KawanSoft SAS
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.HtmlConverter;
import org.kawanfw.sql.util.KeepTempFilePolicyParms;

/**
 * Allows to build the Prepared Statement parameters from a JSON string.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class ServerPreparedStatementParameters {

    private static boolean DEBUG = FrameworkDebug
	    .isSet(ServerPreparedStatementParameters.class);

    /** Universal and clean line separator */
    private static String CR_LF = System.getProperty("line.separator");

    private static final String HTML_DECODED = ".html-decoded.txt";

    private PreparedStatement preparedStatement = null;

    /** The parameter values as objects that can be casted */
    private Map<Integer, Object> parameterValues = new TreeMap<>();

    /** The parameter types as objects that can be casted */
    private Map<Integer, String> parameterTypes = new TreeMap<>();

    /** The parameter types as objects that can be casted */
    private Map<Integer, String> parameterStringValues = new TreeMap<>();

    private HttpServletRequest request;

    /** The InputStream corresponding to a Blob */
    private List<InputStream> inList = new Vector<InputStream>();

    /** The Reader corresponding to a Clob */
    private List<Reader> readerList = new Vector<Reader>();

    /** The blob/clob files list */
    private List<File> blobsOrClobs = new Vector<File>();

    /**
     * Constructor
     * 
     * @param preparedStatement
     *            the prepared statement for whiv to set the parameters
     * @param request
     *            the servlet request
     */
    public ServerPreparedStatementParameters(
	    PreparedStatement preparedStatement, HttpServletRequest request) {

	if (preparedStatement == null) {
	    throw new NullPointerException("preparedStatement is nul!");
	}

	this.preparedStatement = preparedStatement;
	this.request = request;
    }

    /**
     * Sets the parameters from JSon String
     * 
     * @throws SQLException
     * @throws IOException
     * @throws IllegalArgumentException
     *             if use passes bad parameters
     */
    public void setParameters() throws SQLException, IllegalArgumentException,
	    IOException {

	/** The map of (param_type_n, param_value_n) */
	Map<Integer, AceQLParameter> statementParameters = new TreeMap<Integer, AceQLParameter>();

	int i = 1;

	while (true) {
	    String requestParamType = request
		    .getParameter(HttpParameter.PARAM_TYPE_ + i);

	    if (requestParamType != null && !requestParamType.isEmpty()) {
		String requestParamValue = request
			.getParameter(HttpParameter.PARAM_VALUE_ + i);
		statementParameters.put(i, new AceQLParameter(requestParamType,
			requestParamValue));

		if (requestParamValue == null || requestParamValue.isEmpty()) {
		    throw new SQLException(
			    "No parameter value for parameter index " + i);
		}
	    } else {
		break;
	    }

	    i++;
	}

	if (statementParameters.isEmpty()) {
	    return;
	}

	for (Map.Entry<Integer, AceQLParameter> entry : statementParameters
		.entrySet()) {

	    int paramIndex = entry.getKey();
	    AceQLParameter aceQLParameter = entry.getValue();

	    String paramType = aceQLParameter.getParameterType();
	    String paramValue = aceQLParameter.getParameterValue();

	    if (paramValue.equals("NULL")) {
		paramValue = null;
	    }

	    debug(paramIndex + " / " + paramType + " / " + paramValue);
	    /*
	     * CHARACTER String VARCHAR String NUMERIC java.math.BigDecimal
	     * DECIMAL java.math.BigDecimal BIT Boolean TINYINT Integer SMALLINT
	     * Integer INTEGER Integer BIGINT Long REAL Float FLOAT Double
	     * DOUBLE PRECISION Double
	     * 
	     * DATE java.sql.Date TIME java.sql.Time TIMESTAMP
	     * java.sql.Timestamp
	     */

	    parameterTypes.put(paramIndex, paramType);
	    parameterStringValues.put(paramIndex, paramValue);

	    if (paramValue == null) {
		debug("BEFORE setNull " + paramIndex + " " + paramType);
		preparedStatement.setNull(paramIndex,
			JavaSqlConversion.fromSqlToJava(paramType));
		parameterValues.put(paramIndex, null);
		debug("AFTER setNull");
	    } else if (paramType.equalsIgnoreCase(AceQLTypes.CHAR)
		    || paramType.equalsIgnoreCase(AceQLTypes.CHARACTER)
		    || paramType.equalsIgnoreCase(AceQLTypes.VARCHAR)) {
		preparedStatement.setString(paramIndex, paramValue);
		parameterValues.put(paramIndex, paramValue);
	    } else if (paramType.equalsIgnoreCase(AceQLTypes.DECIMAL)
		    || paramType.equalsIgnoreCase(AceQLTypes.NUMERIC)) {
		BigDecimal bigDecimal = new BigDecimal(paramValue);
		preparedStatement.setBigDecimal(paramIndex, bigDecimal);
		parameterValues.put(paramIndex, new BigDecimal(paramValue));
	    } else if (paramType.equalsIgnoreCase(AceQLTypes.BIT)) {
		Boolean theBool = new Boolean(paramValue);
		preparedStatement
			.setBoolean(paramIndex, theBool.booleanValue());
		parameterValues.put(paramIndex,
			Boolean.parseBoolean(paramValue));
	    } else if (paramType.equalsIgnoreCase(AceQLTypes.TINYINT)
		    || paramType.equalsIgnoreCase(AceQLTypes.SMALLINT)
		    || paramType.equalsIgnoreCase(AceQLTypes.INTEGER)) {
		Integer theInteger = new Integer(paramValue);
		preparedStatement.setInt(paramIndex, theInteger.intValue());
		parameterValues.put(paramIndex, Integer.parseInt(paramValue));
	    }
	    // BIGINT Long
	    // REAL Float
	    // FLOAT Double
	    // DOUBLE PRECISION Double
	    else if (paramType.equalsIgnoreCase(AceQLTypes.BIGINT)) {
		Long theLong = new Long(paramValue);
		preparedStatement.setLong(paramIndex, theLong.longValue());
		parameterValues.put(paramIndex, theLong.longValue());
	    } else if (paramType.equalsIgnoreCase(AceQLTypes.REAL)) {
		Float theFloat = new Float(paramValue);
		preparedStatement.setFloat(paramIndex, theFloat.longValue());
		parameterValues.put(paramIndex, theFloat.longValue());
	    } else if (paramType.equalsIgnoreCase(AceQLTypes.FLOAT)
		    || paramType.equalsIgnoreCase(AceQLTypes.DOUBLE_PRECISION)) {
		Double theDouble = new Double(paramValue);
		preparedStatement.setDouble(paramIndex, theDouble);
		parameterValues.put(paramIndex, theDouble.doubleValue());
	    }
	    // DATE java.sql.Date
	    // TIME java.sql.Time
	    // TIMESTAMP java.sql.Timestamp
	    else if (paramType.equalsIgnoreCase(AceQLTypes.DATE)) {
		long timemilliseconds = new Long(paramValue).longValue();
		java.sql.Date theDateTime = new java.sql.Date(timemilliseconds);
		preparedStatement.setDate(paramIndex, theDateTime);
		parameterValues.put(paramIndex, theDateTime);
	    } else if (paramType.equalsIgnoreCase(AceQLTypes.TIME)) {
		long timemilliseconds = new Long(paramValue).longValue();
		java.sql.Time theDateTime = new java.sql.Time(timemilliseconds);
		preparedStatement.setTime(paramIndex, theDateTime);
		parameterValues.put(paramIndex, theDateTime);
	    } else if (paramType.equalsIgnoreCase(AceQLTypes.TIMESTAMP)) {
		long timemilliseconds = new Long(paramValue).longValue();
		java.sql.Timestamp theDateTime = new java.sql.Timestamp(
			timemilliseconds);
		preparedStatement.setTimestamp(paramIndex, theDateTime);
		parameterValues.put(paramIndex, theDateTime);
	    }
	    // LONGVARCHAR String
	    else if (paramType.equalsIgnoreCase(AceQLTypes.LONGVARCHAR)
		    || paramType.equalsIgnoreCase(AceQLTypes.CLOB)) {
		setCharacterStream(preparedStatement, paramIndex, paramValue);
		parameterValues.put(paramIndex, paramValue);
	    } else if (paramType.equalsIgnoreCase(AceQLTypes.URL)) {
		try {
		    URL url = new URL(paramValue);
		    preparedStatement.setURL(paramIndex, url);
		    parameterValues.put(paramIndex, paramValue);
		} catch (MalformedURLException e) {
		    throw new IllegalArgumentException(
			    "The following URL is invalid/malformed: "
				    + paramValue);
		}
	    }
	    // BINARY byte[]
	    // VARBINARY byte[]
	    // LONGVARBINARY byte[]
	    else if (paramType.equalsIgnoreCase(AceQLTypes.BINARY)
		    || paramType.equalsIgnoreCase(AceQLTypes.VARBINARY)
		    || paramType.equalsIgnoreCase(AceQLTypes.LONGVARBINARY)
		    || paramType.equalsIgnoreCase(AceQLTypes.BLOB)) {

		setBinaryStream(preparedStatement, paramIndex, paramValue);
		parameterValues.put(paramIndex, paramValue);

	    } else {
		throw new IllegalArgumentException("Invalid param_type: "
			+ paramType + " for param_index " + paramIndex + ".");
	    }
	}

    }

    /**
     * Sets the character stream using the underlying Clob file uploaded by the
     * client side
     * 
     * @param preparedStatement
     *            The Prepared Statement to execute
     * @param parameterIndex
     *            the parameter index
     * @param paramValue
     *            the parameter value (the file name)
     * @throws SQLException
     */
    private void setCharacterStream(PreparedStatement preparedStatement,
	    int parameterIndex, String paramValue) throws SQLException,
	    IOException {

	String username = request.getParameter(HttpParameter.USERNAME);
	String database = request.getParameter(HttpParameter.DATABASE);
	DatabaseConfigurator databaseConfigurator = ServerSqlManager
		.getDatabaseConfigurator(database);

	// Extract the Clob file from the parameter
	String blobId = paramValue;
	File blobsDir = databaseConfigurator.getBlobsDirectory(username);
	File clobFile = new File(blobsDir.toString() + File.separator + blobId);

	if (!clobFile.exists()) {
	    throw new FileNotFoundException(
		    "Clob file does not exists for blob_id: " + blobId);
	}

	Reader reader = null;
	this.readerList.add(reader);
	
	long theLength = -1;

	String htlmEncoding = request.getParameter(HttpParameter.HTML_ENCODING);
	if (Boolean.parseBoolean(htlmEncoding)) {
	    File clobFileHtmlDecoded = new File(clobFile + HTML_DECODED);
	    blobsOrClobs.add(clobFileHtmlDecoded);

	    BufferedReader br = null;
	    Writer writer = null;

	    try {
		br = new BufferedReader(new FileReader(clobFile));
		writer = new BufferedWriter(new FileWriter(clobFileHtmlDecoded));
		String line = null;
		while ((line = br.readLine()) != null) {
		    line = HtmlConverter.fromHtml(line);
		    writer.write(line + CR_LF);
		}

	    } finally {
		IOUtils.closeQuietly(br);
		IOUtils.closeQuietly(writer);

		if (!KeepTempFilePolicyParms.KEEP_TEMP_FILE && !DEBUG) {
		    clobFile.delete();
		}
	    }

	    reader = new BufferedReader(new FileReader(clobFileHtmlDecoded));
	    theLength = clobFileHtmlDecoded.length();

	} else {
	    blobsOrClobs.add(clobFile);
	    reader = new BufferedReader(new FileReader(clobFile));
	    theLength = clobFile.length();
	}

	// We cast theLength, because the long version may not be implemented by
	// the driver
	preparedStatement.setCharacterStream(parameterIndex, reader,
		(int) theLength);

    }

    /**
     * Set the binary stream using the underlying Blob file uploaded by the
     * client side
     * 
     * @param preparedStatement
     *            The Prepared Statement to execute
     * @param parameterIndex
     *            the parameter index
     * @param paramValue
     *            the parameter value (the file name)
     * @throws SQLException
     * @throws IOException
     */
    private void setBinaryStream(PreparedStatement preparedStatement,
	    int parameterIndex, String paramValue) throws SQLException,
	    IOException {
	// Extract the Blob file from the parameter

	String username = request.getParameter(HttpParameter.USERNAME);
	String database = request.getParameter(HttpParameter.DATABASE);
	DatabaseConfigurator databaseConfigurator = ServerSqlManager
		.getDatabaseConfigurator(database);

	// Extract the Blob/Clob file from the parameter
	String blobId = paramValue;
	File blobsDir = databaseConfigurator.getBlobsDirectory(username);
	File blobFile = new File(blobsDir.toString() + File.separator + blobId);

	debug("before getFileFromParameter()");
	blobsOrClobs.add(blobFile);

	debug("before new BufferedInputStream(new FileInputStream(blobFile))");

	if (!blobFile.exists()) {
	    throw new IllegalArgumentException(
		    "No Blob/Clob uploaded for blob_id: " + blobId);
	}

	InputStream in = null;
	
	// Then update the prepared statement binary stream and we are done!
	in = new BufferedInputStream(new FileInputStream(blobFile));
	long theLength = blobFile.length();

	inList.add(in);
	
	debug("before preparedStatement.setBinaryStream()");

	Connection connection = preparedStatement.getConnection();
	String sql = request.getParameter(HttpParameter.SQL);

	// Test if we are in PostgreSQL with OID column for large file
	if (PostgreSqlUtil.isPostgreSqlStatementWithOID(connection, sql)) {

	    debug("column is OID! " + parameterIndex);
	    PostgreSqlUtil.setPostgreSqlParameterWithLargeObject(
		    preparedStatement, parameterIndex, in, connection);

	} else {
	    // We cast theLength, because the long version may not be
	    // implemented by
	    // the driver
	    debug("column is NOT OID " + parameterIndex);
	    preparedStatement.setBinaryStream(parameterIndex, in,
		    (int) theLength);
	}

	debug("after preparedStatement.setBinaryStream()");

    }

    /**
     * Returns the parameter values as Objects that can be casted
     * 
     * @return the parameter Values as Objects
     */
    public List<Object> getParameterValues() {
	Collection<Object> collection = parameterValues.values();

	List<Object> values = new Vector<Object>();

	for (Object object : collection) {
	    values.add(object);
	}

	return values;
    }

    /**
     * Close the underlying Blob/Clob parameters
     */
    public void close() {
	for (InputStream in : inList) {
	    IOUtils.closeQuietly(in);
	}

	for (Reader reader : readerList) {
	    IOUtils.closeQuietly(reader);
	}

	if (KeepTempFilePolicyParms.KEEP_TEMP_FILE || DEBUG) {
	    return;
	}

	for (File blobOrClob : blobsOrClobs) {
	    blobOrClob.delete();
	}
    }

    public Map<Integer, String> getParameterTypes() {
	return parameterTypes;
    }

    public Map<Integer, String> getParameterStringValues() {
	return parameterStringValues;
    }

    /**
     * @param s
     */

    protected void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

    }

}
