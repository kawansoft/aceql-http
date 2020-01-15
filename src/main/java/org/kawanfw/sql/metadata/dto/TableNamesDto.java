/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2020,  KawanSoft SAS
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
