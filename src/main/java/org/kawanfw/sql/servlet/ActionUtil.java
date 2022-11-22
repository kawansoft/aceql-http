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

    public static boolean isHealthCheckInfo(String action) {
        return action.equals(HttpParameter.HEALTH_CHECK_INFO);
    }


}
