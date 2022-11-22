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
package org.kawanfw.sql.servlet.sql.parameters;

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
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.servlet.injection.classes.InjectedClassesStore;
import org.kawanfw.sql.servlet.sql.AceQLParameter;
import org.kawanfw.sql.servlet.sql.AceQLTypes;
import org.kawanfw.sql.servlet.sql.JavaSqlConversion;
import org.kawanfw.sql.servlet.sql.ParameterDirection;
import org.kawanfw.sql.servlet.sql.PostgreSqlUtil;
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

    private static boolean DEBUG = FrameworkDebug.isSet(ServerPreparedStatementParameters.class);

    /** Universal and clean line separator */
    private static String CR_LF = System.getProperty("line.separator");

    private static final String HTML_DECODED = ".html-decoded.txt";

    /** The parameter values as objects that can be casted */
    private Map<Integer, Object> parameterValues = new TreeMap<>();

    /** The parameter types as objects that can be casted */
    private Map<Integer, String> parameterTypes = new TreeMap<>();

    /** The parameter types as objects that can be casted */
    private Map<Integer, String> parameterStringValues = new TreeMap<>();

    /** The InputStream corresponding to a Blob */
    private List<InputStream> inList = new Vector<InputStream>();

    /** The Reader corresponding to a Clob */
    private List<Reader> readerList = new Vector<Reader>();

    /** The blob/clob files list */
    private List<File> blobsOrClobs = new Vector<File>();

    private String username;
    private String database;
    private String sql;
    private PreparedStatement preparedStatement = null;
    private Map<Integer, AceQLParameter> inOutStatementParameters;
    private String htlmEncoding; // Can be null
	
   
    /**
     * 
     * @param username
     * @param database
     * @param sql
     * @param preparedStatement
     * @param inOutStatementParameters
     * @param htlmEncoding
     */
    public ServerPreparedStatementParameters(String username, String database, String sql,
	    PreparedStatement preparedStatement, Map<Integer, AceQLParameter> inOutStatementParameters,
	    String htlmEncoding) {

	Objects.requireNonNull(username, "username cannot be null!");
	Objects.requireNonNull(database, "database cannot be null!");
	Objects.requireNonNull(sql, "sql cannot be null!");
	Objects.requireNonNull(preparedStatement, "preparedStatement cannot be null!");
	Objects.requireNonNull(inOutStatementParameters, "inOutStatementParameters cannot be null!");
	
	this.username = username;
	this.database = database;
	this.sql = sql;
	this.preparedStatement = preparedStatement;
	this.inOutStatementParameters = inOutStatementParameters;
	this.htlmEncoding = htlmEncoding;
	
    }

    public int getParametersNumber() {
	Set<Integer> set = inOutStatementParameters.keySet();
	return set.size();
    }


    /**
     * Sets the parameters from JSon String
     *
     * @throws SQLException
     * @throws IOException
     * @throws IllegalArgumentException if use passes bad parameters
     */
    public void setParameters() throws SQLException, IllegalArgumentException, IOException {

	if (inOutStatementParameters.isEmpty()) {
	    return;
	}

	for (Map.Entry<Integer, AceQLParameter> entry : inOutStatementParameters.entrySet()) {

	    int paramIndex = entry.getKey();
	    AceQLParameter aceQLParameter = entry.getValue();

	    String paramType = aceQLParameter.getParameterType();
	    String paramValue = aceQLParameter.getParameterValue();
	    String paramDirection = aceQLParameter.getParameterDirection();

	    if (isInParameter(paramDirection) && paramValue.equals("NULL")) {
		paramValue = null;
	    }

	    debug(paramIndex + " / " + paramType + " / " + paramValue);

	    parameterTypes.put(paramIndex, paramType);
	    parameterStringValues.put(paramIndex, paramValue);

	    if (paramValue == null) {
		registerNullParameter(paramIndex, paramType, paramDirection);
	    } else if (paramType.equalsIgnoreCase(AceQLTypes.CHAR) || paramType.equalsIgnoreCase(AceQLTypes.CHARACTER)
		    || paramType.equalsIgnoreCase(AceQLTypes.VARCHAR)) {
		registerCharParameter(paramIndex, paramType, paramValue, paramDirection);
	    } else if (paramType.equalsIgnoreCase(AceQLTypes.DECIMAL)
		    || paramType.equalsIgnoreCase(AceQLTypes.NUMERIC)) {
		registerDecimalOrNumericParameter(paramIndex, paramType, paramValue, paramDirection);
	    } else if (paramType.equalsIgnoreCase(AceQLTypes.BIT)) {
		registerBitParameter(paramIndex, paramType, paramValue, paramDirection);
	    } else if (paramType.equalsIgnoreCase(AceQLTypes.TINYINT) || paramType.equalsIgnoreCase(AceQLTypes.SMALLINT)
		    || paramType.equalsIgnoreCase(AceQLTypes.INTEGER)) {
		registerSmallIntParameter(paramIndex, paramType, paramValue, paramDirection);
	    }
	    else if (paramType.equalsIgnoreCase(AceQLTypes.BIGINT)) {
		registerBigIntParameter(paramIndex, paramType, paramValue, paramDirection);
	    } else if (paramType.equalsIgnoreCase(AceQLTypes.REAL)) {
		registerRealParameter(paramIndex, paramType, paramValue, paramDirection);
	    } else if (paramType.equalsIgnoreCase(AceQLTypes.FLOAT)
		    || paramType.equalsIgnoreCase(AceQLTypes.DOUBLE_PRECISION)) {
		registerDoublePrecision(paramIndex, paramType, paramValue, paramDirection);
	    }
	    else if (paramType.equalsIgnoreCase(AceQLTypes.DATE)) {
		registerDateParameter(paramIndex, paramType, paramValue, paramDirection);
	    } else if (paramType.equalsIgnoreCase(AceQLTypes.TIME)) {
		registerTimeParameter(paramIndex, paramType, paramValue, paramDirection);
	    } else if (paramType.equalsIgnoreCase(AceQLTypes.TIMESTAMP)) {
		registerTimestampParameter(paramIndex, paramType, paramValue, paramDirection);
	    }
	    else if (paramType.equalsIgnoreCase(AceQLTypes.LONGVARCHAR)
		    || paramType.equalsIgnoreCase(AceQLTypes.CLOB)) {
		registerLongVarcharOrClobParameter(paramIndex, paramValue, paramDirection);
	    } else if (paramType.equalsIgnoreCase(AceQLTypes.URL)) {
		registerUrlParameter(paramIndex, paramType, paramValue, paramDirection);
	    }
	    else if (paramType.equalsIgnoreCase(AceQLTypes.BINARY) || paramType.equalsIgnoreCase(AceQLTypes.VARBINARY)
		    || paramType.equalsIgnoreCase(AceQLTypes.LONGVARBINARY)
		    || paramType.equalsIgnoreCase(AceQLTypes.BLOB)) {
		registerBinaryParameter(paramIndex, paramValue, paramDirection);
	    } else {
		throw new IllegalArgumentException(
			"Invalid parameter type: " + paramType + " for parameter index " + paramIndex + ".");
	    }
	}

    }

    /**
     * @param paramIndex
     * @param paramValue
     * @param paramDirection
     * @throws IllegalArgumentException
     * @throws SQLException
     * @throws IOException
     */
    private void registerBinaryParameter(int paramIndex, String paramValue, String paramDirection)
	    throws IllegalArgumentException, SQLException, IOException {
	// BINARY byte[]
	// VARBINARY byte[]
	// LONGVARBINARY byte[]
	if (isOutParameter(paramDirection)) {
	    throw new IllegalArgumentException(
		    "Invalid OUT direction. Binary stream and Blob parameters can not be OUT (parameter index "
			    + paramIndex + ").");
	}

	setBinaryStream(preparedStatement, paramIndex, paramValue);
	parameterValues.put(paramIndex, paramValue);
    }

    /**
     * @param paramIndex
     * @param paramType
     * @param paramValue
     * @param paramDirection
     * @throws SQLException
     * @throws IllegalArgumentException
     */
    private void registerUrlParameter(int paramIndex, String paramType, String paramValue, String paramDirection)
	    throws SQLException, IllegalArgumentException {
	if (isInParameter(paramDirection)) {
	    try {
		URL url = new URL(paramValue);
		preparedStatement.setURL(paramIndex, url);
		parameterValues.put(paramIndex, paramValue);
	    } catch (MalformedURLException e) {
		throw new IllegalArgumentException("The following URL is invalid/malformed: " + paramValue);
	    }
	}
	registerOutParameter(paramIndex, paramType, paramDirection);
    }

    /**
     * @param paramIndex
     * @param paramValue
     * @param paramDirection
     * @throws IllegalArgumentException
     * @throws SQLException
     * @throws IOException
     */
    private void registerLongVarcharOrClobParameter(int paramIndex, String paramValue, String paramDirection)
    	// LONGVARCHAR String
	    throws IllegalArgumentException, SQLException, IOException {
	if (isOutParameter(paramDirection)) {
	    throw new IllegalArgumentException(
		    "Invalid OUT direction. Characters stream and Clob parameters can not be OUT. (index "
			    + paramIndex + ").");
	}

	setCharacterStream(preparedStatement, paramIndex, paramValue);
	parameterValues.put(paramIndex, paramValue);
    }

    /**
     * @param paramIndex
     * @param paramType
     * @param paramValue
     * @param paramDirection
     * @throws NumberFormatException
     * @throws SQLException
     */
    private void registerTimestampParameter(int paramIndex, String paramType, String paramValue, String paramDirection)
	    throws NumberFormatException, SQLException {
	if (isInParameter(paramDirection)) {
	    long timemilliseconds = Long.parseLong(paramValue);

	    java.sql.Timestamp theDateTime = new java.sql.Timestamp(timemilliseconds);
	    preparedStatement.setTimestamp(paramIndex, theDateTime);
	    parameterValues.put(paramIndex, theDateTime);
	}
	registerOutParameter(paramIndex, paramType, paramDirection);
    }

    /**
     * @param paramIndex
     * @param paramType
     * @param paramValue
     * @param paramDirection
     * @throws NumberFormatException
     * @throws SQLException
     */
    private void registerTimeParameter(int paramIndex, String paramType, String paramValue, String paramDirection)
	    throws NumberFormatException, SQLException {
	if (isInParameter(paramDirection)) {
	    long timemilliseconds = Long.parseLong(paramValue);

	    java.sql.Time theDateTime = new java.sql.Time(timemilliseconds);
	    preparedStatement.setTime(paramIndex, theDateTime);
	    parameterValues.put(paramIndex, theDateTime);
	}
	registerOutParameter(paramIndex, paramType, paramDirection);
    }

    /**
     * @param paramIndex
     * @param paramType
     * @param paramValue
     * @param paramDirection
     * @throws NumberFormatException
     * @throws SQLException
     */
    private void registerDateParameter(int paramIndex, String paramType, String paramValue, String paramDirection)
	    throws NumberFormatException, SQLException {
	if (isInParameter(paramDirection)) {
	    long timemilliseconds = Long.parseLong(paramValue);

	    java.sql.Date theDateTime = new java.sql.Date(timemilliseconds);
	    preparedStatement.setDate(paramIndex, theDateTime);
	    parameterValues.put(paramIndex, theDateTime);
	}
	registerOutParameter(paramIndex, paramType, paramDirection);
    }

    /**
     * @param paramIndex
     * @param paramType
     * @param paramValue
     * @param paramDirection
     * @throws NumberFormatException
     * @throws SQLException
     */
    private void registerDoublePrecision(int paramIndex, String paramType, String paramValue, String paramDirection)
	    throws NumberFormatException, SQLException {
	if (isInParameter(paramDirection)) {
	    Double theDouble = Double.valueOf(paramValue);

	    preparedStatement.setDouble(paramIndex, theDouble);
	    parameterValues.put(paramIndex, theDouble.doubleValue());
	}
	registerOutParameter(paramIndex, paramType, paramDirection);
    }

    /**
     * @param paramIndex
     * @param paramType
     * @param paramValue
     * @param paramDirection
     * @throws NumberFormatException
     * @throws SQLException
     */
    private void registerRealParameter(int paramIndex, String paramType, String paramValue, String paramDirection)
	    throws NumberFormatException, SQLException {
	if (isInParameter(paramDirection)) {
	    Float theFloat = Float.parseFloat(paramValue);

	    preparedStatement.setFloat(paramIndex, theFloat.floatValue());
	    parameterValues.put(paramIndex, theFloat.floatValue());
	}
	registerOutParameter(paramIndex, paramType, paramDirection);
    }

    /**
     * @param paramIndex
     * @param paramType
     * @param paramValue
     * @param paramDirection
     * @throws NumberFormatException
     * @throws SQLException
     */
    private void registerBigIntParameter(int paramIndex, String paramType, String paramValue, String paramDirection)
	    throws NumberFormatException, SQLException {
	// BIGINT Long
	// REAL Float
	// FLOAT Double
	// DOUBLE PRECISION Double

	if (isInParameter(paramDirection)) {
	    Long theLong = Long.parseLong(paramValue);

	    preparedStatement.setLong(paramIndex, theLong.longValue());
	    parameterValues.put(paramIndex, theLong.longValue());
	}
	registerOutParameter(paramIndex, paramType, paramDirection);
    }

    /**
     * @param paramIndex
     * @param paramType
     * @param paramValue
     * @param paramDirection
     * @throws NumberFormatException
     * @throws SQLException
     */
    private void registerSmallIntParameter(int paramIndex, String paramType, String paramValue, String paramDirection)
	    throws NumberFormatException, SQLException {
	
	debug("Parameter is int!");
	
	if (isInParameter(paramDirection)) {
	    // Integer theInteger = new Integer(paramValue);
	    Integer theInteger = Integer.parseInt(paramValue);

	    debug("");
	    debug("Parameter paramIndex: " + paramIndex);
	    debug("Parameter int value : " + theInteger.intValue());
		
	    preparedStatement.setInt(paramIndex, theInteger.intValue());
	    parameterValues.put(paramIndex, Integer.parseInt(paramValue));
	}
	registerOutParameter(paramIndex, paramType, paramDirection);
    }

    /**
     * @param paramIndex
     * @param paramType
     * @param paramValue
     * @param paramDirection
     * @throws SQLException
     */
    private void registerBitParameter(int paramIndex, String paramType, String paramValue, String paramDirection)
	    throws SQLException {
	if (isInParameter(paramDirection)) {
	    // Boolean theBool = new Boolean(paramValue);
	    Boolean theBool = Boolean.valueOf(paramValue);

	    preparedStatement.setBoolean(paramIndex, theBool.booleanValue());
	    parameterValues.put(paramIndex, Boolean.parseBoolean(paramValue));
	}
	registerOutParameter(paramIndex, paramType, paramDirection);
    }

    /**
     * @param paramIndex
     * @param paramType
     * @param paramValue
     * @param paramDirection
     * @throws SQLException
     */
    private void registerDecimalOrNumericParameter(int paramIndex, String paramType, String paramValue,
	    String paramDirection) throws SQLException {
	if (isInParameter(paramDirection)) {
	    BigDecimal bigDecimal = new BigDecimal(paramValue);
	    preparedStatement.setBigDecimal(paramIndex, bigDecimal);
	    parameterValues.put(paramIndex, new BigDecimal(paramValue));
	}
	registerOutParameter(paramIndex, paramType, paramDirection);
    }

    /**
     * @param paramIndex
     * @param paramType
     * @param paramValue
     * @param paramDirection
     * @throws SQLException
     */
    private void registerCharParameter(int paramIndex, String paramType, String paramValue, String paramDirection)
	    throws SQLException {
	if (isInParameter(paramDirection)) {
	    preparedStatement.setString(paramIndex, paramValue);
	    parameterValues.put(paramIndex, paramValue);
	}
	registerOutParameter(paramIndex, paramType, paramDirection);
    }

    /**
     * @param paramIndex
     * @param paramType
     * @param paramDirection
     * @throws SQLException
     */
    private void registerNullParameter(int paramIndex, String paramType, String paramDirection) throws SQLException {
	debug("BEFORE setNull " + paramIndex + " " + paramType);

	if (isInParameter(paramDirection)) {
	    preparedStatement.setNull(paramIndex, JavaSqlConversion.fromSqlToJava(paramType));
	    parameterValues.put(paramIndex, null);
	}

	registerOutParameter(paramIndex, paramType, paramDirection);

	debug("AFTER setNull");
    }


    private void registerOutParameter(int paramIndex, String paramType, String paramDirection) throws SQLException {
	if (isOutParameter(paramDirection)) {
	    if (preparedStatement instanceof CallableStatement) {
		int sqlType = JavaSqlConversion.fromSqlToJava(paramType);
		((CallableStatement) preparedStatement).registerOutParameter(paramIndex, sqlType);
	    } else {
		throw new IllegalArgumentException(
			"Illegal OUT parameter. PreparedStatement is not a CallableStatement (parameter index: "
				+ paramIndex + ").");
	    }
	}
    }

    public static boolean isInParameter(String parameterDirection) {
	Objects.requireNonNull(parameterDirection, "parameterDirection cannot be null!");
	return parameterDirection.equals(ParameterDirection.IN) || parameterDirection.equals(ParameterDirection.INOUT);
    }

    public static boolean isOutParameter(String parameterDirection) {
	Objects.requireNonNull(parameterDirection, "parameterDirection cannot be null!");
	return parameterDirection.equals(ParameterDirection.OUT) || parameterDirection.equals(ParameterDirection.INOUT);
    }

    /**
     * Sets the character stream using the underlying Clob file uploaded by the
     * client side
     *
     * @param preparedStatement The Prepared Statement to execute
     * @param parameterIndex    the parameter index
     * @param paramValue        the parameter value (the file name)
     * @throws SQLException
     */
    private void setCharacterStream(PreparedStatement preparedStatement, int parameterIndex, String paramValue)
	    throws SQLException, IOException {

	DatabaseConfigurator databaseConfigurator = InjectedClassesStore.get().getDatabaseConfigurators().get(database);

	// Extract the Clob file from the parameter
	String blobId = paramValue;
	File blobsDir = databaseConfigurator.getBlobsDirectory(username);
	File clobFile = new File(blobsDir.toString() + File.separator + blobId);

	if (!clobFile.exists()) {
	    throw new FileNotFoundException("Clob file does not exists for blob_id: " + blobId);
	}

	Reader reader = null;
	this.readerList.add(reader);

	long theLength = -1;

	if (Boolean.parseBoolean(htlmEncoding)) {
	    File clobFileHtmlDecoded = new File(clobFile + HTML_DECODED);
	    blobsOrClobs.add(clobFileHtmlDecoded);

	    try (BufferedReader br = new BufferedReader(new FileReader(clobFile));
		    Writer writer = new BufferedWriter(new FileWriter(clobFileHtmlDecoded));) {

		String line = null;
		while ((line = br.readLine()) != null) {
		    line = HtmlConverter.fromHtml(line);
		    writer.write(line + CR_LF);
		}

	    } finally {
		// IOUtils.closeQuietly(br);
		// IOUtils.closeQuietly(writer);

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
	preparedStatement.setCharacterStream(parameterIndex, reader, (int) theLength);

    }

    /**
     * Set the binary stream using the underlying Blob file uploaded by the client
     * side
     *
     * @param preparedStatement The Prepared Statement to execute
     * @param parameterIndex    the parameter index
     * @param paramValue        the parameter value (the file name)
     * @throws SQLException
     * @throws IOException
     */
    private void setBinaryStream(PreparedStatement preparedStatement, int parameterIndex, String paramValue)
	    throws SQLException, IOException {
	// Extract the Blob file from the parameter

	DatabaseConfigurator databaseConfigurator = InjectedClassesStore.get().getDatabaseConfigurators().get(database);

	// Extract the Blob/Clob file from the parameter
	String blobId = paramValue;
	File blobsDir = databaseConfigurator.getBlobsDirectory(username);
	File blobFile = new File(blobsDir.toString() + File.separator + blobId);

	debug("before getFileFromParameter()");
	blobsOrClobs.add(blobFile);

	debug("before new BufferedInputStream(new FileInputStream(blobFile))");

	if (!blobFile.exists()) {
	    throw new IllegalArgumentException("No Blob/Clob uploaded for blob_id. FIle does not exists: " + blobFile);
	}

	InputStream in = null;

	// Then update the prepared statement binary stream and we are done!
	in = new BufferedInputStream(new FileInputStream(blobFile));
	long theLength = blobFile.length();

	inList.add(in);

	debug("before preparedStatement.setBinaryStream()");

	Connection connection = preparedStatement.getConnection();

	// Test if we are in PostgreSQL with OID column for large file
	if (PostgreSqlUtil.isPostgreSqlStatementWithOID(connection, sql)) {

	    debug("column is OID! " + parameterIndex);
	    PostgreSqlUtil.setPostgreSqlParameterWithLargeObject(preparedStatement, parameterIndex, in, connection);

	} else {
	    // We cast theLength, because the long version may not be
	    // implemented by
	    // the driver
	    debug("column is NOT OID " + parameterIndex);
	    preparedStatement.setBinaryStream(parameterIndex, in, (int) theLength);
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
	    // IOUtils.closeQuietly(in);
	    if (in != null) {
		try {
		    in.close();
		} catch (IOException e) {
		    // e.printStackTrace();
		}
	    }
	}

	for (Reader reader : readerList) {
	    // IOUtils.closeQuietly(reader);
	    if (reader != null) {
		try {
		    reader.close();
		} catch (IOException e) {
		    // e.printStackTrace();
		}
	    }
	}

	if (KeepTempFilePolicyParms.KEEP_TEMP_FILE || DEBUG) {
	    System.err.println("WARNING: Blob file not deleted! KEEP_TEMP_FILE: "
		    + KeepTempFilePolicyParms.KEEP_TEMP_FILE + " DEBUG: " + DEBUG);
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
     * @return the inOutStatementParameters
     */
    public Map<Integer, AceQLParameter> getInOutStatementParameters() {
	return inOutStatementParameters;
    }

    /**
     * @param s
     */

    protected void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
