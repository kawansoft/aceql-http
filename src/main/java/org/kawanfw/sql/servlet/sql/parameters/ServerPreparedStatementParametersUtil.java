package org.kawanfw.sql.servlet.sql.parameters;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.sql.AceQLParameter;
import org.kawanfw.sql.servlet.sql.ParameterDirection;
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
     * Debug 
     * @param s
     */

    protected static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
