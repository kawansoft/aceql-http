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
package org.kawanfw.sql.metadata.dto;

import java.util.List;

/**
 * Holder for class name to execute and params to use
 * @author Nicolas de Pomereu
 *
 */
public class ServerQueryExecutorDto {

    private String serverQueryExecutorClassName;
    private List<String> parameterTypes;
    private List<String> parameterValues;
    
    /**
     * Constructor.
     * 
     * @param serverQueryExecutorClassName
     * @param parameterTypes
     * @param parameterValues
     */
    public ServerQueryExecutorDto(String serverQueryExecutorClassName, List<String> parameterTypes,
	    List<String> parameterValues) {
	this.serverQueryExecutorClassName = serverQueryExecutorClassName;
	this.parameterTypes = parameterTypes;
	this.parameterValues = parameterValues;
    }

    /**
     * @return the serverQueryExecutorClassName
     */
    public String getServerQueryExecutorClassName() {
        return serverQueryExecutorClassName;
    }

    /**
     * @return the parameterTypes
     */
    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    /**
     * @return the parameterValues
     */
    public List<String> getParameterValues() {
        return parameterValues;
    }

    @Override
    public String toString() {
	return "ServerQueryExecutorDto [serverQueryExecutorClassName=" + serverQueryExecutorClassName
		+ ", parameterTypes=" + parameterTypes + ", parameterValues=" + parameterValues + "]";
    }
    
    
}
