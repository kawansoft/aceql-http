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
package org.kawanfw.sql.servlet;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Utility methods for actions.
 * @author Nicolas de Pomereu
 *
 */
public class ActionUtil {

    public static boolean isMetadataQueryAction(String action) throws SQLException {
        Objects.requireNonNull(action, "action cannot be null!");

        return (action.equals(HttpParameter.METADATA_QUERY_DB_SCHEMA_DOWNLOAD)
        	|| action.equals(HttpParameter.METADATA_QUERY_GET_TABLE_DETAILS)
        	|| action.equals(HttpParameter.METADATA_QUERY_GET_DB_METADATA)
        	|| action.equals(HttpParameter.METADATA_QUERY_GET_TABLE_NAMES));
    }

    public static boolean isJdbcDatabaseMetaDataQuery(String action) {
        return action.equals(HttpParameter.JDBC_DATABASE_META_DATA);
    }


}
