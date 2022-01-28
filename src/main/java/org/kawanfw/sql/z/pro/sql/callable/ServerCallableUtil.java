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
package org.kawanfw.sql.z.pro.sql.callable;

import java.sql.CallableStatement;
import java.sql.SQLException;

import org.kawanfw.sql.servlet.sql.AceQLTypes;

/**
 * Utility methods for stores procedures.
 * @author Nicolas de Pomereu
 *
 */
public class ServerCallableUtil {

    /**
     * Returns the value of the OUT parameter for a CallableStatement using  parameter index.
     * @param callableStatement
     * @param outParamIndex
     * @param paramType
     * @return
     * @throws SQLException
     */
    public static String callableStatementGetStringValue(CallableStatement callableStatement, int outParamIndex,
            String paramType) throws SQLException {
    
        String outParamValue = null;
    
        if (paramType.equalsIgnoreCase(AceQLTypes.CHAR) || paramType.equalsIgnoreCase(AceQLTypes.CHARACTER)
        	|| paramType.equalsIgnoreCase(AceQLTypes.VARCHAR)) {
            outParamValue = callableStatement.getString(outParamIndex);
        } else if (paramType.equalsIgnoreCase(AceQLTypes.DECIMAL) || paramType.equalsIgnoreCase(AceQLTypes.NUMERIC)) {
    
            outParamValue = "" + callableStatement.getBigDecimal(outParamIndex);
    
        } else if (paramType.equalsIgnoreCase(AceQLTypes.BIT)) {
    
            outParamValue = "" + callableStatement.getBoolean(outParamIndex);
    
        } else if (paramType.equalsIgnoreCase(AceQLTypes.TINYINT) || paramType.equalsIgnoreCase(AceQLTypes.SMALLINT)
        	|| paramType.equalsIgnoreCase(AceQLTypes.INTEGER)) {
    
            outParamValue = "" + callableStatement.getInt(outParamIndex);
    
        }
        // BIGINT Long
        // REAL Float
        // FLOAT Double
        // DOUBLE PRECISION Double
        else if (paramType.equalsIgnoreCase(AceQLTypes.BIGINT)) {
    
            outParamValue = "" + callableStatement.getLong(outParamIndex);
    
        } else if (paramType.equalsIgnoreCase(AceQLTypes.REAL)) {
    
            outParamValue = "" + callableStatement.getFloat(outParamIndex);
    
        } else if (paramType.equalsIgnoreCase(AceQLTypes.FLOAT)
        	|| paramType.equalsIgnoreCase(AceQLTypes.DOUBLE_PRECISION)) {
    
            outParamValue = "" + callableStatement.getDouble(outParamIndex);
    
        }
        // DATE java.sql.Date
        // TIME java.sql.Time
        // TIMESTAMP java.sql.Timestamp
        // HACK Version 3.2.2: add .getTime() to transport in numeric long format instead of
        // Date formated.
        else if (paramType.equalsIgnoreCase(AceQLTypes.DATE)) {
            if (callableStatement.getDate(outParamIndex) != null) {
                outParamValue = "" + callableStatement.getDate(outParamIndex).getTime(); 
            }
            else {
        	outParamValue = null;
            }
    
        } else if (paramType.equalsIgnoreCase(AceQLTypes.TIME)) { 
            
            if (callableStatement.getTime(outParamIndex) != null) {
                outParamValue = "" + callableStatement.getTime(outParamIndex).getTime(); 
            }
            else {
        	outParamValue = null;
            }
    
        } else if (paramType.equalsIgnoreCase(AceQLTypes.TIMESTAMP)) {
            
            if (callableStatement.getTimestamp(outParamIndex) != null) {
                outParamValue = "" + callableStatement.getTimestamp(outParamIndex).getTime(); 
            }
            else {
        	outParamValue = null;
            }
            
            
        } else if (paramType.equalsIgnoreCase(AceQLTypes.URL)) {
    
            outParamValue = "" + callableStatement.getURL(outParamIndex);
    
        } else {
            throw new IllegalArgumentException(
        	    "Invalid OUT parameter type: " + paramType + " for parameter index " + outParamIndex + ".");
        }
        return outParamValue;
    }

    /**
     * Returns the value of the OUT parameter for a CallableStatement using parameter name.
     * @param callableStatement
     * @param outParameterName
     * @param paramType
     * @return
     * @throws SQLException
     */
    public static String callableStatementGetStringValue(CallableStatement callableStatement, String outParameterName,
            String paramType) throws SQLException {
    
        String outParamValue = null;
    
        if (paramType.equalsIgnoreCase(AceQLTypes.CHAR) || paramType.equalsIgnoreCase(AceQLTypes.CHARACTER)
        	|| paramType.equalsIgnoreCase(AceQLTypes.VARCHAR)) {
            outParamValue = callableStatement.getString(outParameterName);
        } else if (paramType.equalsIgnoreCase(AceQLTypes.DECIMAL) || paramType.equalsIgnoreCase(AceQLTypes.NUMERIC)) {
    
            outParamValue = "" + callableStatement.getBigDecimal(outParameterName);
    
        } else if (paramType.equalsIgnoreCase(AceQLTypes.BIT)) {
    
            outParamValue = "" + callableStatement.getBoolean(outParameterName);
    
        } else if (paramType.equalsIgnoreCase(AceQLTypes.TINYINT) || paramType.equalsIgnoreCase(AceQLTypes.SMALLINT)
        	|| paramType.equalsIgnoreCase(AceQLTypes.INTEGER)) {
    
            outParamValue = "" + callableStatement.getInt(outParameterName);
    
        }
        // BIGINT Long
        // REAL Float
        // FLOAT Double
        // DOUBLE PRECISION Double
        else if (paramType.equalsIgnoreCase(AceQLTypes.BIGINT)) {
    
            outParamValue = "" + callableStatement.getLong(outParameterName);
    
        } else if (paramType.equalsIgnoreCase(AceQLTypes.REAL)) {
    
            outParamValue = "" + callableStatement.getFloat(outParameterName);
    
        } else if (paramType.equalsIgnoreCase(AceQLTypes.FLOAT)
        	|| paramType.equalsIgnoreCase(AceQLTypes.DOUBLE_PRECISION)) {
    
            outParamValue = "" + callableStatement.getDouble(outParameterName);
    
        }
        // DATE java.sql.Date
        // TIME java.sql.Time
        // TIMESTAMP java.sql.Timestamp
        else if (paramType.equalsIgnoreCase(AceQLTypes.DATE)) {
            outParamValue = "" + callableStatement.getDate(outParameterName);
    
        } else if (paramType.equalsIgnoreCase(AceQLTypes.TIME)) {
            outParamValue = "" + callableStatement.getTime(outParameterName);
    
        } else if (paramType.equalsIgnoreCase(AceQLTypes.TIMESTAMP)) {
            outParamValue = "" + callableStatement.getTimestamp(outParameterName);
        } else if (paramType.equalsIgnoreCase(AceQLTypes.URL)) {
    
            outParamValue = "" + callableStatement.getURL(outParameterName);
    
        } else {
            throw new IllegalArgumentException(
        	    "Invalid OUT parameter type: " + paramType + " for parameter index " + outParameterName + ".");
        }
        return outParamValue;
    }

}
