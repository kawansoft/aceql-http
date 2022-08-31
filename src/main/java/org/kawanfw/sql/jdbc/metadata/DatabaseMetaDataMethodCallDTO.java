/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.jdbc.metadata;

import java.util.List;
import java.util.Objects;

/**
 * DTO to transport a DatabaseMetaData function call with parameters from client to server.
 * @author Nicolas de Pomereu
 *
 */
public class DatabaseMetaDataMethodCallDTO {

    private String methodName;
    private List<String> paramTypes;
    private List<String> paramValues;

    /**
     * Constructor of the DTO.
     * @param methodName
     * @param paramTypes
     * @param paramValues
     */
    public DatabaseMetaDataMethodCallDTO(String methodName, List<String> paramTypes, List<String> paramValues) {
	this.methodName =  Objects.requireNonNull(methodName, "methodName cannot ne null!");
	this.paramTypes = Objects.requireNonNull(paramTypes, "paramTypes cannot ne null!");
	this.paramValues = Objects.requireNonNull(paramValues, "paramValues cannot ne null!");
    }

    public String getMethodName() {
        return methodName;
    }

    public List<String> getParamTypes() {
        return paramTypes;
    }

    public List<String> getParamValues() {
        return paramValues;
    }

    @Override
    public String toString() {
	return "DatabaseMetaDataMethodCallDTO [methodName=" + methodName + ", paramTypes=" + paramTypes
		+ ", paramValues=" + paramValues + "]";
    }

}
