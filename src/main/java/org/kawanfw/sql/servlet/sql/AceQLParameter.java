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

/**
 * Immutable Holder for a PreparedStatement Parameter.
 * 
 * @author Nicolas de Pomereu
 *
 */

public class AceQLParameter {

    private int parameterIndex = -1;
    private String parameterType = null;
    private String parameterValue = null;

    // OUT parms info
    private String parameterDirection = null;
    private String outParameterName = null;

    /**
     * Constructor
     * 
     * @param parameterIndex
     * @param parameterType
     * @param parameterValue
     * @param parameterDirection
     * @param outParameterName
     */
    public AceQLParameter(int parameterIndex, String parameterType, String parameterValue, String parameterDirection,
	    String outParameterName) {
	this.parameterIndex = parameterIndex;
	this.parameterType = parameterType;
	this.parameterValue = parameterValue;
	this.parameterDirection = parameterDirection;
	this.outParameterName = outParameterName;

    }

    /**
     * @return the parameterIndex
     */
    public int getParameterIndex() {
	return parameterIndex;
    }

    /**
     * Returns the parameter type
     * 
     * @return the parameter type
     */
    public String getParameterType() {
	return parameterType;
    }

    /**
     * Returns the parameter value
     * 
     * @return the parameter value
     */
    public String getParameterValue() {
	return parameterValue;
    }

    /**
     * @return the parameterDirection
     */
    public String getParameterDirection() {
	return parameterDirection;
    }

    /**
     * @return the outParameterName
     */
    public String getOutParameterName() {
	return outParameterName;
    }

    /**
     * Returns true if the parameter is OUT or INOUT type (stored procedure
     * parameter).
     * 
     * @return true if the parameter is OUT or INOUT type.
     */
    public boolean isOutParameter() {

	boolean outParameter = ServerPreparedStatementParameters.isOutParameter(parameterDirection);
	return outParameter;

    }

}
