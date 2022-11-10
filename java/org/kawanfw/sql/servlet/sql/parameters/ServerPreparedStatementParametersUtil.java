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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.SystemUtils;
import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.sql.AceQLParameter;
import org.kawanfw.sql.servlet.sql.ParameterDirection;
import org.kawanfw.sql.servlet.sql.dto.PrepStatementParamsHolder;
import org.kawanfw.sql.util.FrameworkDebug;

public class ServerPreparedStatementParametersUtil {

    private static boolean DEBUG = FrameworkDebug.isSet(ServerPreparedStatementParametersUtil.class);

    /**
     * Build a clean Map of PreparedStatement parameters (index, value) from the
     * request filled by client side
     * 
     * @param request the servlet reQUEST
     * @return the
     * @throws SQLException if a parameter is IN and there is no value for it
     */
    public static Map<Integer, AceQLParameter> buildParametersFromRequest(HttpServletRequest request)
	    throws SQLException {

	Map<Integer, AceQLParameter> inOutStatementParameters = new TreeMap<Integer, AceQLParameter>();

	int i = 1;

	while (true) {
	    String requestParamType = request.getParameter(HttpParameter.PARAM_TYPE_ + i);

	    if (requestParamType != null && !requestParamType.isEmpty()) {
		String requestParamValue = request.getParameter(HttpParameter.PARAM_VALUE_ + i);

		String parameterDirection = request.getParameter(HttpParameter.PARAM_DIRECTION_ + i);

		if (parameterDirection == null) {
		    parameterDirection = ParameterDirection.IN;
		}
		parameterDirection = parameterDirection.toLowerCase();

		if (!parameterDirection.equals(ParameterDirection.IN)
			&& !parameterDirection.equals(ParameterDirection.OUT)
			&& !parameterDirection.equals(ParameterDirection.INOUT)) {
		    throw new IllegalArgumentException(
			    "Invalid direction for parameter of index " + i + ": " + parameterDirection);
		}

		// Out parameters may have a f...ing name!! We have to handle it.
		String outParameterName = request.getParameter(HttpParameter.OUT_PARAM_NAME_ + i);

		inOutStatementParameters.put(i, new AceQLParameter(i, requestParamType, requestParamValue,
			parameterDirection, outParameterName));

		debug("index: " + i + " / type " + requestParamType + " / direction: " + parameterDirection
			+ " / value: " + requestParamValue);

		// NO: fix to accept "" (empty values) now: if
		// (isInParameter(parameterDirection) && (requestParamValue == null ||
		// requestParamValue.isEmpty())) {
		if (isInParameter(parameterDirection) && requestParamValue == null) {
		    throw new SQLException("No parameter value for IN parameter index " + i);
		}

	    } else {
		break;
	    }

	    i++;
	}

	return inOutStatementParameters;

    }

    /**
     * PreparedStatement parameters converter. To be used for PreparedStatement with
     * batch mode
     * 
     * @param prepStatementParamsHolder the prepared statement parameters in
     *                                  PrepStatementParamsHolder format
     * @return the prepared statement parameters in Map<Integer, AceQLParameter>
     *         format.
     */
    public static Map<Integer, AceQLParameter> buildParametersFromHolder(
	    PrepStatementParamsHolder prepStatementParamsHolder) {
	Objects.requireNonNull(prepStatementParamsHolder, "prepStatementParamsHolder cannot be null!");
	Map<String, String> holderStatementParameters = prepStatementParamsHolder.getStatementParameters();
	
	debug();
	debug("PreparedStatement parameters:");
	debug(holderStatementParameters);

	Map<Integer, AceQLParameter> parameters = new HashMap<>();

	int i = 1;
	while (true) {
	    String parameterType = holderStatementParameters.get(HttpParameter.PARAM_TYPE_ + i);

	    if (parameterType != null) {
		String parameterValue = holderStatementParameters.get(HttpParameter.PARAM_VALUE_ + i);

		String parameterDirection = ParameterDirection.IN;
		AceQLParameter aceQLParameter = new AceQLParameter(i, parameterType, parameterValue, parameterDirection,
			null);
		parameters.put(i, aceQLParameter);
	    } else {
		break;
	    }

	    i++;
	}

	debug();
	debug(parameters);
	
	return parameters;
    }


    public static boolean isInParameter(String parameterDirection) {
	Objects.requireNonNull(parameterDirection, "parameterDirection cannot be null!");
	return parameterDirection.equals(ParameterDirection.IN) || parameterDirection.equals(ParameterDirection.INOUT);
    }

    private static void debug(Map<?, ?> map) {
	if (DEBUG) {
	    dump(new Date() + " " + map);
	}
    }

    private static void debug() {
	if (DEBUG) {
	    dump(new Date() + "");
	}
    }

    /**
     * debug
     * 
     * @param set
     */
    public static void debug(Set<String> set) {
	if (DEBUG) {
	    dump(new Date() + " " + set);
	}
    }

    /**
     * Debug
     * 
     * @param s
     * @throws IOException
     */

    protected static void debug(String s) {
	if (DEBUG) {
	    dump(new Date() + " " + s);
	}
    }

    public static void dump(String string) {

	File file = getDumpFile();

	try (FileWriter fw = new FileWriter(file, true);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter out = new PrintWriter(bw)) {
	    out.println(string);
	} catch (Exception e) {
	    e.printStackTrace(System.out);
	}
    }

    /**
     * @return
     */
    public static File getDumpFile() {
	String filename = SystemUtils.getUserHome() + File.separator + "aceql_dump.txt";
	File file = new File(filename);
	return file;
    }

}
