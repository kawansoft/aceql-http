/**
 * 
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
