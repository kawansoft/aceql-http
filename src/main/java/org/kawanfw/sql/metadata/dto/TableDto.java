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
package org.kawanfw.sql.metadata.dto;

import org.kawanfw.sql.metadata.Table;

/**
 * Contains the list of tables of the database.
 * @author Nicolas de Pomereu
 *
 */
public class TableDto {

    private String status = "OK";
    private Table table = null;

    public TableDto(Table table) {
	super();
	this.table = table;
    }

    public String getStatus() {
        return status;
    }

    public Table getTable() {
        return table;
    }

    @Override
    public String toString() {
	return "DatabaseInfoDto [status=" + status + ", table=" + table + "]";
    }

}
