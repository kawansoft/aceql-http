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

public class ServletMetadataQuery {

    public static final String METADATA_QUERY = "METADATA_QUERY";

    private String requestUri = null;

    public ServletMetadataQuery(String requestUri) {
	if (requestUri == null) {
	    throw new NullPointerException("urlContent is null");
	}

	this.requestUri = requestUri;
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

    public static boolean isMetadataQueryAction(String action) throws SQLException {
	if (action == null) {
	    throw new NullPointerException("action is null");
	}
	return (action.equals(HttpParameter.METADATA_QUERY_DB_SCHEMA_DOWNLOAD)
		|| action.equals(HttpParameter.METADATA_QUERY_GET_TABLE_DETAILS)
		|| action.equals(HttpParameter.METADATA_QUERY_GET_DB_METADATA)
		|| action.equals(HttpParameter.METADATA_QUERY_GET_TABLE_NAMES));
    }

}
