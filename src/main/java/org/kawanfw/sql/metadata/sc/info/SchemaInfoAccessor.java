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
package org.kawanfw.sql.metadata.sc.info;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang3.SystemUtils;

/**
 * Wrapper to access SchemaCrawler that is not accessible in Java 7.
 * @author Nicolas de Pomereu
 *
 */
public class SchemaInfoAccessor {

    private Connection connection = null;
    private String failureReason = null;

    public SchemaInfoAccessor(Connection connection) {
	this.connection = connection;
    }

    public boolean isAccessible() {
	String javaVersion = SystemUtils.JAVA_VERSION;
	if (SystemUtils.JAVA_VERSION.compareTo("1.8") < 0) {
	    failureReason = "Java version is " + javaVersion + ". Access to db_schema_download API info requires Java 8 or beyond on AceQL server";
	    return false;
	}

	return true;
    }

    public String getFailureReason() {
        return failureReason;
    }

    /**
     * Gets the SchemaInfoSC instance in Java 8 and beyond only.
     * @return SchemaInfoSC instance in Java 8 and beyond only.
     * @throws SQLException
     */
    public SchemaInfoSC getSchemaInfoSC( ) throws SQLException {
	return new SchemaInfoSC(connection);
    }


}
