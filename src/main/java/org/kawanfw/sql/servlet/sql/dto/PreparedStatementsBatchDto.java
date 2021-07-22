/**
 * 
 */
package org.kawanfw.sql.servlet.sql.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the Map of prepared statement parameters to upload to server.
 * @author Nicolas de Pomereu
 *
 */
public class PreparedStatementsBatchDto {

    private List<PrepStatementParamsHolder> prepStatementParamsHolderList = new ArrayList<>();

    public PreparedStatementsBatchDto(List<PrepStatementParamsHolder> prepStatementParamsHolderList) {
	this.prepStatementParamsHolderList = prepStatementParamsHolderList;
    }

    /**
     * @return the prepStatementParamsHolderList
     */
    public List<PrepStatementParamsHolder> getPrepStatementParamsHolderList() {
        return prepStatementParamsHolderList;
    }

    @Override
    public String toString() {
	return "PreparedStatementsBatchDto [prepStatementParamsHolderList=" + prepStatementParamsHolderList + "]";
    }
    
}
