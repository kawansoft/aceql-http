/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.sql;

import org.kawanfw.sql.servlet.sql.parameters.ServerPreparedStatementParameters;

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

    @Override
    public String toString() {
	return "AceQLParameter [parameterIndex=" + parameterIndex + ", parameterType=" + parameterType
		+ ", parameterValue=" + parameterValue + ", parameterDirection=" + parameterDirection
		+ ", outParameterName=" + outParameterName + "]";
    }
    
}
