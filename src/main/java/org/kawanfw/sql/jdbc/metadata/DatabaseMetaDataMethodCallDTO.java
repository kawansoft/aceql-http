/*
 * This file is part of AceQL Client SDK.
 * AceQL Client SDK: Remote JDBC access over HTTP with AceQL HTTP.
 * Copyright (C) 2020,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
