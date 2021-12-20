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

package org.kawanfw.sql.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.servlet.http.HttpServletRequest;

import org.kawanfw.sql.api.server.executor.ClientEvent;
import org.kawanfw.sql.api.server.executor.ClientEventWrapper;
import org.kawanfw.sql.api.server.executor.ServerQueryExecutor;
import org.kawanfw.sql.metadata.dto.ServerQueryExecutorDto;
import org.kawanfw.sql.metadata.util.GsonWsUtil;
import org.kawanfw.sql.servlet.sql.ResultSetWriter;
import org.kawanfw.sql.servlet.sql.json_return.JsonUtil;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.SqlTag;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ServerQueryExecutorUtil {

    private static boolean DEBUG = FrameworkDebug.isSet(ServerQueryExecutorUtil.class);
    
    /**
     * Static class.
     */
    protected ServerQueryExecutorUtil() {

    }

    public static boolean isExecuteServerQuery(HttpServletRequest request, OutputStream out, String action,
	    Connection connection) throws SQLException, IOException {

	if (action.equals(HttpParameter.EXECUTE_SERVER_QUERY)) {
	    // Get username / database / ServerQueryExecutorDto
	    // Execute the classname with reflection (no aceql-server.properties preloading
	    // in first version)
	    String username = request.getParameter(HttpParameter.USERNAME);
	    String database = request.getParameter(HttpParameter.DATABASE);

	    String jsonString = request.getParameter(HttpParameter.SERVER_QUERY_EXECUTOR_DTO);
	    ServerQueryExecutorDto serverQueryExecutorDto = GsonWsUtil.fromJson(jsonString,
		    ServerQueryExecutorDto.class);

	    debug("serverQueryExecutorDto: " + serverQueryExecutorDto.toString());
	    
	    Class<?> c = null;
	    String className = null;
	    try {
		className = serverQueryExecutorDto.getServerQueryExecutorClassName();
		c = Class.forName(className);
	    } catch (ClassNotFoundException e) {
		throw new SQLException(SqlTag.USER_CONFIGURATION + ". Cannot load ServerQueryExecutor class: "
			+ className + ". " + e.toString());
	    }

	    Constructor<?> constructor;
	    try {
		constructor = c.getConstructor();
	    } catch (Exception e) {
		throw new SQLException(SqlTag.USER_CONFIGURATION
			+ ". Cannot create constructor for ServerQueryExecutor class: " + className + ". "
			+ e.toString());
	    }

	    ServerQueryExecutor serverQueryExecutor = null;
	    try {
		serverQueryExecutor = (ServerQueryExecutor) constructor.newInstance();
	    } catch (Exception e) {
		throw new SQLException(SqlTag.USER_CONFIGURATION
			+ ". Cannot create new instance for ServerQueryExecutor class: " + className + ". "
			+ e.toString());
	    }

	    List<String> paramTypes = serverQueryExecutorDto.getParameterTypes();
	    List<String> paramValues = serverQueryExecutorDto.getParameterValues();

	    List<Object> params = new ArrayList<>();
	    try {
		params = buildParametersValuesFromTypes(paramTypes, paramValues);
	    } catch (Exception e) {
		throw new SQLException(SqlTag.USER_CONFIGURATION
			+ ". Cannot load parameters for ServerQueryExecutor class: " + className + ". " + e.toString());
	    }

	    String ipAddress = request.getRemoteAddr();
	    ClientEvent clientEvent = ClientEventWrapper.builderClientEvent(username, database, ipAddress, params);
	    ResultSet rs = serverQueryExecutor.executeQuery(clientEvent, connection);
	    
	    if (rs == null) {
		throw new SQLException(SqlTag.USER_CONFIGURATION
			+ ". ResultSet cannot be null! The ServerQueryExecutor class returns a null ResultSet: " + className + ". ");	
	    }
	    
	    dumpResultSetOnServletOutStream(request, rs, out);

	    return true;
	} else {
	    return false;
	}
    }

    private static List<Object> buildParametersValuesFromTypes(List<String> paramTypes, List<String> paramValues)
	    throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException,
	    InstantiationException, IllegalAccessException, InvocationTargetException {

	if (paramTypes == null || paramTypes.isEmpty()) {
	    return null;
	}

	List<Object> values = new ArrayList<>();

	for (int i = 0; i < paramTypes.size(); i++) {

	    String value = paramValues.get(i);
	    String javaType = paramTypes.get(i);

	    debug("javaType / value: " + javaType + " / " + value);
	    
	    JavaValueBuilder javaValueBuilder = new JavaValueBuilder(javaType, value);
	    values.add(javaValueBuilder.getValue());
	}

	return values;
    }

    private static void dumpResultSetOnServletOutStream(HttpServletRequest request, ResultSet rs, OutputStream out)
	    throws SQLException, IOException {

	boolean doGzip = Boolean.parseBoolean(request.getParameter(HttpParameter.GZIP_RESULT));
	OutputStream outFinal =  getFinalOutputStream(out, doGzip);
	
	boolean doPrettyPrinting = true;
	JsonGeneratorFactory jf = JsonUtil.getJsonGeneratorFactory(doPrettyPrinting);

	JsonGenerator gen = jf.createGenerator(outFinal);
	gen.writeStartObject().write("status", "OK");

	String fillResultSetMetaDataStr = request.getParameter(HttpParameter.FILL_RESULT_SET_META_DATA);
	boolean fillResultSetMetaData = Boolean.parseBoolean(fillResultSetMetaDataStr);

	String sql = "select * from table"; // Will not be used
	ResultSetWriter resultSetWriter = new ResultSetWriter(request, sql, gen, fillResultSetMetaData);
	resultSetWriter.write(rs);

	ServerSqlManager.writeLine(outFinal);

	gen.writeEnd(); // .write("status", "OK")
	gen.flush();
	gen.close();
    }
    
    /**
     * Get the OutputStream to use. A regular one or a GZIP_RESULT one
     * @param out
     * @param doGzip
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static OutputStream getFinalOutputStream(OutputStream out, boolean doGzip) throws FileNotFoundException, IOException {

	if (doGzip) {
	    GZIPOutputStream gZipOut = new GZIPOutputStream(out);
	    return gZipOut;
	} else {
	    OutputStream outFinal = out;
	    return outFinal;
	}
    }
    
    public static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
