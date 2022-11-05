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
package org.kawanfw.test.parms;

import org.kawanfw.sql.api.util.SqlUtil;

public class ConnectionLoaderJdbcInfo {

    public String sqlEngine = null;

    private String driverClassName;
    private String url;

    public ConnectionLoaderJdbcInfo(String sqlEngine) {
	this.sqlEngine = sqlEngine;
	build();
    }

    private void build() {

	if (sqlEngine.equals(SqlUtil.POSTGRESQL)) {
	    driverClassName = JdbcDriverParms.POSTGRES_DRIVER_CLASS_NAME;
	    url = JdbcDriverParms.POSTGRES_URL;
	} else if (sqlEngine.equals(SqlTestParms.MARIADB)) {
	    driverClassName = JdbcDriverParms.MARIADB_DRIVER_CLASS_NAME;
	    url = JdbcDriverParms.MARIADB_URL;
	} else if (sqlEngine.equals(SqlUtil.MYSQL)) {
	    driverClassName = JdbcDriverParms.MYSQL_DRIVER_CLASS_NAME;
	    url = JdbcDriverParms.MYSQL_URL;
	} else if (sqlEngine.equals(SqlUtil.ORACLE)) {
	    driverClassName = JdbcDriverParms.ORACLE_DRIVER_CLASS_NAME;
	    url = JdbcDriverParms.ORACLE_URL;
	} else if (sqlEngine.equals(SqlTestParms.SQLSERVER_JTDS_DRIVER)) {
	    driverClassName = JdbcDriverParms.SQL_SERVER_JTDS_DRIVER_CLASS_NAME;
	    url = JdbcDriverParms.SQL_SERVER_JTDS_URL;
	} else if (sqlEngine.equals(SqlTestParms.SQLSERVER_MS_DRIVER)) {
	    driverClassName = JdbcDriverParms.SQL_SERVER_MS_DRIVER_CLASS_NAME;
	    url = JdbcDriverParms.SQL_SERVER_MS_URL;
	} else if (sqlEngine.equals(SqlUtil.DB2)) {
	    driverClassName = JdbcDriverParms.DB2_DRIVER_CLASS_NAME;
	    url = JdbcDriverParms.DB2_URL;
	} else if (sqlEngine.equals(SqlUtil.ADAPTIVE_SERVER_ENTERPRISE)) {
	    driverClassName = JdbcDriverParms.ASE_DRIVER_CLASS_NAME;
	    url = JdbcDriverParms.ASE_URL;
	} else if (sqlEngine.equals("SQL Anywhere")) {
	    driverClassName = JdbcDriverParms.SQL_ANYWHERE_DRIVER_CLASS_NAME;
	    url = JdbcDriverParms.SQL_ANYWHERE_URL;
	} else if (sqlEngine.equals(SqlUtil.INFORMIX)) {
	    driverClassName = JdbcDriverParms.INFORMIX_CLASS_NAME;
	    url = JdbcDriverParms.INFORMIX_URL;
	} else if (sqlEngine.equals(SqlUtil.HSQLDB)) {
	    driverClassName = JdbcDriverParms.HSQLDB_DRIVER_CLASS_NAME;
	    url = JdbcDriverParms.HSQLDB_URL;
	} else if (sqlEngine.equals(SqlUtil.H2)) {
	    driverClassName = JdbcDriverParms.H2_DRIVER_CLASS_NAME;
	    url = JdbcDriverParms.H2_URL;
	} else if (sqlEngine.equals(SqlUtil.INGRES)) {
	    driverClassName = JdbcDriverParms.INGRES_DRIVER_CLASS_NAME;
	    url = JdbcDriverParms.INGRES_URL;
	} else if (sqlEngine.equals(SqlUtil.TERADATA)) {
	    driverClassName = JdbcDriverParms.TERADATA_DRIVER_CLASS_NAME;
	    url = JdbcDriverParms.TERADATA_URL;
	} else {
	    throw new IllegalArgumentException("Invalid Product: " + sqlEngine);
	}

    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getUrl() {
        return url;
    }




}
