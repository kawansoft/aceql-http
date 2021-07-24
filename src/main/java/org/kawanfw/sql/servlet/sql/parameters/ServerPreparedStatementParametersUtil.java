package org.kawanfw.sql.servlet.sql.parameters;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.sql.AceQLParameter;
import org.kawanfw.sql.servlet.sql.ParameterDirection;
import org.kawanfw.sql.servlet.sql.dto.PrepStatementParamsHolder;
import org.kawanfw.sql.util.FrameworkDebug;

public class ServerPreparedStatementParametersUtil {

    private static boolean DEBUG = FrameworkDebug.isSet(ServerPreparedStatementParametersUtil.class);
    
    /**
     * Build a clean Map of PreparedStatement parameters (index, value) from the request filled by client side
     * @param request the servlet reQUEST
     * @return  the
     * @throws SQLException if a parameter is IN and there is no value for it
     */
    public static Map<Integer, AceQLParameter> buildParametersFromRequest(HttpServletRequest request) throws SQLException {
	
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

		// NO: fix to accept "" (empty values) now:  if (isInParameter(parameterDirection) && (requestParamValue == null || requestParamValue.isEmpty())) {
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
    
    public static boolean isInParameter(String parameterDirection) {
	Objects.requireNonNull(parameterDirection, "parameterDirection cannot be null!");
	return parameterDirection.equals(ParameterDirection.IN) || parameterDirection.equals(ParameterDirection.INOUT);
    }
    

    /**
     * PreparedStatement parameters converter.
     * To be used for PreparedStatement with batch mode
     * @param prepStatementParamsHolder	the prepared statement parameters in PrepStatementParamsHolder format
     * @return the prepared statement parameters in Map<Integer, AceQLParameter> format.
     */
    public static Map<Integer, AceQLParameter> buildParametersFromHolder(
	    PrepStatementParamsHolder prepStatementParamsHolder) {
	Objects.requireNonNull(prepStatementParamsHolder, "prepStatementParamsHolder cannot be null!");
	Map<String, String> holderStatementParameters = prepStatementParamsHolder.getStatementParameters();
	
	Set<String> keys = holderStatementParameters.keySet();
	
	debug("PreparedStatement parameters index as set unsorted: ");
	debug(keys);

	// Simple way of sorting
	List<String> list = keys.stream().sorted((e1, e2) -> 
	e1.compareTo(e2)).collect(Collectors.toList());
	
	debug("PreparedStatement parameters index as list sorted: ");
	debug(list);
	
	Map<Integer, AceQLParameter> parameters = new HashMap<>();
	
	int i = 1;
	for (String key : list) {
	    String value = holderStatementParameters.get(key);
	    String parameterDirection = ParameterDirection.IN;
	    AceQLParameter aceQLParameter = new AceQLParameter(i, key, value, parameterDirection, null);
	    parameters.put(i, aceQLParameter);
	    
	    i++;
	}

	return parameters;
    }

    private static void debug(List<String> list) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + list);
	}
    }

    /**
     * @param keys
     */
    public static void debug(Set<String> keys) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + keys);
	}
    }

    /**
     * Debug
     * 
     * @param s
     */

    protected static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }
    
}
