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
