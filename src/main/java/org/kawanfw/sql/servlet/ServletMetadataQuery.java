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
package org.kawanfw.sql.servlet;

import java.util.Objects;

public class ServletMetadataQuery {

    public static final String METADATA_QUERY = "METADATA_QUERY";

    private String requestUri = null;

    public ServletMetadataQuery(String requestUri) {
	this.requestUri =Objects.requireNonNull(requestUri, "requestUri cannot be null!");
    }

    public String getAction() {

	if (requestUri.contains("/metadata_query/db_schema_download")) {
	    return HttpParameter.METADATA_QUERY_DB_SCHEMA_DOWNLOAD;
	}
	else if (requestUri.endsWith("/metadata_query/get_db_metadata")) {
	    return HttpParameter.METADATA_QUERY_GET_DB_METADATA;
	}
	else if (requestUri.endsWith("/metadata_query/get_table_names")) {
	    return HttpParameter.METADATA_QUERY_GET_TABLE_NAMES;
	}
	else if (requestUri.contains("/metadata_query/get_table")) {
	    return HttpParameter.METADATA_QUERY_GET_TABLE_DETAILS;
	}
	else {
	    throw new IllegalArgumentException("Unknown metadata_query action: " + requestUri);
	}
    }

}
