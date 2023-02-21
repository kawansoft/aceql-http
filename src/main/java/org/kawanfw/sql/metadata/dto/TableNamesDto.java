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

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the list of tables of the database.
 * @author Nicolas de Pomereu
 *
 */
public class TableNamesDto {

    private String status = "OK";
    private List<String> tableNames = new ArrayList<>();

    public TableNamesDto(List<String> tableNames) {
	this.tableNames = tableNames;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getTableNames() {
        return tableNames;
    }

    @Override
    public String toString() {
	return "TableNamesDto [status=" + status + ", tableNames=" + tableNames + "]";
    }

}
