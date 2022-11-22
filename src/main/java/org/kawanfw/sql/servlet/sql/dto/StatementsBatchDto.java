/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.sql.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the list of SQL statements to upload to server
 * @author Nicolas de Pomereu
 *
 */
public class StatementsBatchDto {

    private List<String> batchList = new ArrayList<>();

    /**
     * 
     * @param batchList the list of SQL statements created with Statement.addBatch()
     */
    public StatementsBatchDto(List<String> batchList) {
	super();
	this.batchList = batchList;
    }

    /**
     * @return the batchList
     */
    public List<String> getBatchList() {
        return batchList;
    }

    @Override
    public String toString() {
	return "StatementsBatchDto [batchList=" + batchList + "]";
    }

    
 
}
