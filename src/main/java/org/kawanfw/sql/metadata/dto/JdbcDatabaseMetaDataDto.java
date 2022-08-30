/*
 * Copyright (c)2022 KawanSoft S.A.S.
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-30
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.metadata.dto;

import org.kawanfw.sql.metadata.JdbcDatabaseMetaData;

public class JdbcDatabaseMetaDataDto {

    private String status = "OK";
    private JdbcDatabaseMetaData jdbcDatabaseMetaData = null;

    public JdbcDatabaseMetaDataDto(JdbcDatabaseMetaData jdbcDatabaseMetaData) {
	super();
	this.jdbcDatabaseMetaData = jdbcDatabaseMetaData;
    }

    public String getStatus() {
        return status;
    }

    public JdbcDatabaseMetaData getJdbcDatabaseMetaData() {
        return jdbcDatabaseMetaData;
    }

    @Override
    public String toString() {
	return "JdbcDatabaseMetaDataDto [status=" + status + ", jdbcDatabaseMetaData=" + jdbcDatabaseMetaData + "]";
    }


}
