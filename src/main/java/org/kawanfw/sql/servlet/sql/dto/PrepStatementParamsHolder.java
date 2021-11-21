/*
 * This file is part of AceQL JDBC Driver.
 * AceQL JDBC Driver: Remote JDBC access over HTTP with AceQL HTTP.
 * Copyright (C) 2021,  KawanSoft SAS
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
package org.kawanfw.sql.servlet.sql.dto;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Nicolas de Pomereu
 *
 */
public class PrepStatementParamsHolder {

    /** All the PreparedStatement parameters and ther values */
    private Map<String, String> statementParameters = new LinkedHashMap<>();

    public PrepStatementParamsHolder(Map<String, String> statementParameters) {
	this.statementParameters = statementParameters;
    }

    /**
     * @return the statementParameters
     */
    public Map<String, String> getStatementParameters() {
        return statementParameters;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((statementParameters == null) ? 0 : statementParameters.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	PrepStatementParamsHolder other = (PrepStatementParamsHolder) obj;
	if (statementParameters == null) {
	    if (other.statementParameters != null)
		return false;
	} else if (!statementParameters.equals(other.statementParameters))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "PrepStatementParamsHolder [statementParameters=" + statementParameters + "]";
    }
    
 
}
