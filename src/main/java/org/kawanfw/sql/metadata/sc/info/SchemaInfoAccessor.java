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
