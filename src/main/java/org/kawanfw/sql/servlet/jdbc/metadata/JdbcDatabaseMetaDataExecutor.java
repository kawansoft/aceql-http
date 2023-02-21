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
package org.kawanfw.sql.servlet.jdbc.metadata;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.servlet.http.HttpServletRequest;

import org.kawanfw.sql.jdbc.metadata.BooleanResponseDTO;
import org.kawanfw.sql.jdbc.metadata.DatabaseMetaDataMethodCallDTO;
import org.kawanfw.sql.metadata.util.GsonWsUtil;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.servlet.sql.ResultSetWriter;
import org.kawanfw.sql.servlet.sql.json_return.JsonUtil;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.Tag;

/**
 * @author Nicolas de Pomereu
 *
 */
public class JdbcDatabaseMetaDataExecutor {

    /** Set to true to display/log debug info */
    private static boolean DEBUG = FrameworkDebug.isSet(JdbcDatabaseMetaDataExecutor.class);

    private HttpServletRequest request;
    private DatabaseMetaDataMethodCallDTO databaseMetaDataMethodCallDTO;
    private OutputStream out;
    private Connection connection;


    /**
     * Constructor.
     * @param request
     * @param databaseMetaDataMethodCallDTO
     * @param out
     * @param connection
     */
    public JdbcDatabaseMetaDataExecutor(HttpServletRequest request, DatabaseMetaDataMethodCallDTO databaseMetaDataMethodCallDTO,
	    OutputStream out, Connection connection) {
	this.request = request;
	this.databaseMetaDataMethodCallDTO = databaseMetaDataMethodCallDTO;
	this.out = out;
	this.connection = connection;
    }

    /**
     * Executes the call of the DatabaseMetaData method asked by the PC side.
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws IOException
     */
    public void callDatabaseMetaDataMethod() throws SQLException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {

	String methodName = databaseMetaDataMethodCallDTO.getMethodName();
	List<String> paramTypes = databaseMetaDataMethodCallDTO.getParamTypes();
	List<String> paramsValues = databaseMetaDataMethodCallDTO.getParamValues();

	DatabaseMetaData databaseMetaData = connection.getMetaData();

	// Trap DatabaseMetaData.getTables() & DatabaseMetaData.getUDTs()
	// that have special array String[] or int[] parameters

	if (methodName.equals("getTables") || methodName.equals("getUDTs")
		|| methodName.equals("getPrimaryKeys")) {
	    DatabaseMetaDataSpecialExecutor databaseMetaDataSpecial = new DatabaseMetaDataSpecialExecutor(
		    databaseMetaData, methodName, paramsValues);
	    ResultSet rs = databaseMetaDataSpecial.execute();
	    dumpResultSetOnServletOutStream(rs);
	    return;
	}

	MethodParametersBuilder methodParametersBuilder = new MethodParametersBuilder(paramTypes, paramsValues);
	Class<?>[] methodParameterTypes = methodParametersBuilder.getMethodParamTypes();
	Object[] methodParameterValues = methodParametersBuilder.getMethodParamValues();

	Object resultObj = callMethodWithReflection(methodName, databaseMetaData, methodParameterTypes, methodParameterValues);

	if (resultObj instanceof ResultSet) {
	    ResultSet rs = (ResultSet) resultObj;
	    dumpResultSetOnServletOutStream(rs);

	} else {
	    // All other formats are handled in String
	    String result = null;
	    if (resultObj != null) {
		result = resultObj.toString();
	    }

	    debug("callMethodWithReflection: " + result);
	    Boolean booleanResult = Boolean.parseBoolean(result);
	    BooleanResponseDTO booleanResponseDTO = new BooleanResponseDTO(booleanResult);
	    String jsonString = GsonWsUtil.getJSonString(booleanResponseDTO);
	    ServerSqlManager.writeLine(out, jsonString);
	}

    }

    private void dumpResultSetOnServletOutStream(ResultSet rs) throws SQLException, IOException {
	boolean doPrettyPrinting = true;
	JsonGeneratorFactory jf = JsonUtil.getJsonGeneratorFactory(doPrettyPrinting);

	JsonGenerator gen = jf.createGenerator(out);
	gen.writeStartObject().write("status", "OK");

	boolean fillResultSetMetaData = true;
	ResultSetWriter resultSetWriter = new ResultSetWriter(request, "ResultSetMetaData", gen, fillResultSetMetaData);
	resultSetWriter.write(rs);

	ServerSqlManager.writeLine(out);

	gen.writeEnd(); // .write("status", "OK")
	gen.flush();
	gen.close();

    }


    /**
     * Calls the
     * @param methodName
     * @param databaseMetaData
     * @param methodParameterTypes
     * @param methodParameterValues
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private Object callMethodWithReflection(String methodName, DatabaseMetaData databaseMetaData, Class<?>[] methodParameterTypes,
	    Object[] methodParameterValues) throws ClassNotFoundException, SQLException, SecurityException, NoSuchMethodException,
	    IllegalArgumentException, IllegalAccessException, InvocationTargetException {
	Class<?> c = Class.forName("java.sql.DatabaseMetaData");
	Object theObject = databaseMetaData;

	// Invoke the method
	Method main = null;
	Object resultObj = null;

	// Get the Driver Info
	String database = "";
	String productVersion = "";
	String DriverName = "";
	String DriverVersion = "";
	String driverInfo = Tag.PRODUCT;

	database = databaseMetaData.getDatabaseProductName();
	productVersion = databaseMetaData.getDatabaseProductVersion();
	DriverName = databaseMetaData.getDriverName();
	DriverVersion = databaseMetaData.getDriverVersion();
	driverInfo +=  " " + database + " " + productVersion + " " + DriverName + " " + DriverVersion;

	String methodParams = getMethodParams(methodParameterValues);

	try {
	    main = c.getDeclaredMethod(methodName, methodParameterTypes);
	} catch (SecurityException e) {
	    throw new SecurityException(driverInfo + " - Security - Impossible to get declared DatabaseMetaData."
		    + methodName + "(" + methodParams + ")");
	} catch (NoSuchMethodException e) {
	    throw new NoSuchMethodException(driverInfo + " - No Such Method - Impossible get declared DatabaseMetaData."
		    + methodName + "(" + methodParams + ")");
	}

	try {
	    resultObj = main.invoke(theObject, methodParameterValues);
	} catch (IllegalArgumentException e) {
	    throw new IllegalArgumentException(
		    driverInfo + " - Impossible to call DatabaseMetaData." + methodName + "(" + methodParams + ")");
	} catch (IllegalAccessException e) {
	    throw new IllegalAccessException(driverInfo + " - Impossible to access DatabaseMetaData method."
		    + methodName + "(" + methodParams + ")");
	} catch (InvocationTargetException e) {
	    throw new InvocationTargetException(e, driverInfo + " - Impossible to invoke DatabaseMetaData method."
		    + methodName +  methodParams);
	}
	return resultObj;
    }

    /**
     * Returns the method parameters as (value1, value2, ...)
     *
     * @param values the value array
     * @return the method parameters as (value1, value2, ...)
     */
    private static String getMethodParams(Object[] values) {

	if (values.length == 0) {
	    return "";
	}

	String returnValue = "(";

	for (int i = 0; i < values.length; i++) {
	    returnValue += values[i];
	    if (i < values.length - 1) {
		returnValue += ", ";
	    }
	}

	returnValue += ")";

	return returnValue;
    }

    /**
     * Debug tool
     *
     * @param s
     */
    private void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }
}
