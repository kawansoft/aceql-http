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

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ServletPathAnalyzer {

    private static boolean DEBUG = FrameworkDebug.isSet(ServletPathAnalyzer.class);

    /**
     *
     */
    public ServletPathAnalyzer() {
    }

    private String connectionModifierOrReader = null;
    private String sqlStatement = null;
    private String blobAction = null;

    private String actionValue = null;
    private String session = null;
    private String connection;

    public boolean isConnectionModifierOrReader(String requestUri) {

        if (requestUri == null) {
            throw new NullPointerException("urlContent is null");
        }

        if (requestUri.endsWith("/get_connection")) {
            connectionModifierOrReader = "get_connection";
            return true;
        }

        if (requestUri.endsWith("/close")) {
            connectionModifierOrReader = "close";
            return true;
        }

        if (requestUri.endsWith("/logout") || requestUri.endsWith("/disconnect")) {
            connectionModifierOrReader = "logout";
            return true;
        }

        if (requestUri.endsWith("/commit")) {
            connectionModifierOrReader = "commit";
            return true;
        }

        if (requestUri.endsWith("/get_catalog")) {
            connectionModifierOrReader = "get_catalog";
            return true;
        }

        if (requestUri.endsWith("/rollback")) {
            connectionModifierOrReader = "rollback";
            return true;
        }

        if (requestUri.endsWith("/set_auto_commit/true") || requestUri.endsWith("/set_auto_commit/false")) {
            connectionModifierOrReader = "set_auto_commit";
            actionValue = StringUtils.substringAfterLast(requestUri, "/");
            return true;
        }

        if (requestUri.endsWith("/set_read_only/true") || requestUri.endsWith("/set_read_only/false")) {
            connectionModifierOrReader = "set_read_only";
            actionValue = StringUtils.substringAfterLast(requestUri, "/");
            return true;
        }

        if (requestUri.contains("/set_transaction_isolation_level/")) {
            connectionModifierOrReader = "set_transaction_isolation_level";
            actionValue = StringUtils.substringAfterLast(requestUri, "/");
            return true;
        }

        if (requestUri.contains("/set_holdability/")) {
            connectionModifierOrReader = "set_holdability";
            actionValue = StringUtils.substringAfterLast(requestUri, "/");
            return true;
        }

        if (requestUri.endsWith("/get_auto_commit")) {
            connectionModifierOrReader = "get_auto_commit";
            return true;
        }

        if (requestUri.endsWith("/is_read_only")) {
            connectionModifierOrReader = "is_read_only";
            return true;
        }

        if (requestUri.endsWith("/get_holdability")) {
            connectionModifierOrReader = "get_holdability";
            return true;
        }

        if (requestUri.endsWith("/get_transaction_isolation_level")) {
            connectionModifierOrReader = "get_transaction_isolation_level";
            return true;
        }

        return false;

    }

    public boolean isVersionAction(String urlContent) {
        if (urlContent == null) {
            throw new NullPointerException("urlContent is null");
        }

        if (urlContent.endsWith("/get_version")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isBlobAction(String urlContent) {
        if (urlContent == null) {
            throw new NullPointerException("urlContent is null");
        }

        if (urlContent.endsWith("/blob_upload")) {
            blobAction = "blob_upload";
            return true;
        }

        if (urlContent.endsWith("/blob_download")) {
            blobAction = "blob_download";
            return true;
        }

        if (urlContent.endsWith("/get_blob_length")) {
            blobAction = "get_blob_length";
            return true;
        }

        return false;

    }

    public String getBlobAction() {
        if (blobAction == null) {
            throw new NullPointerException("blobAction is null. Call isBlobAction() before");
        }
        return blobAction;
    }

    public String getConnectionModifierOrReader() {

        if (connectionModifierOrReader == null) {
            throw new NullPointerException("connectionModifierOrReader is null. Call isConnectionModifier() before");
        }
        return connectionModifierOrReader;
    }

    public boolean isExecuteUpdateOrQueryStatement(String urlContent) {
        if (urlContent == null) {
            throw new NullPointerException("urlContent is null");
        }

        if (urlContent.endsWith("/execute_update")) {
            sqlStatement = "execute_update";
            return true;
        }

        if (urlContent.endsWith("/execute_query")) {
            sqlStatement = "execute_query";
            return true;
        }

        if (urlContent.endsWith("/execute")) {
            sqlStatement = "execute";
            return true;
        }

        return false;

    }

    public boolean isMetadataQuery(final String urlContent) {
        if (urlContent == null) {
            throw new NullPointerException("urlContent is null");
        }

        if (!urlContent.contains("/metadata_query/")) {
            return false;
        }

        if (urlContent.contains("/metadata_query/db_schema_download")) {
            return true;
        } else if (urlContent.contains("/metadata_query/get_table")) {
            return true;
        } else if (urlContent.endsWith("/metadata_query/get_db_metadata")) {
            return true;
        } else if (urlContent.endsWith("/metadata_query/get_table_names")) {
            return true;
        }

        return false;
    }

    public void buildElements(String servletName, String urlContent) {

        if (urlContent == null) {
            throw new NullPointerException("urlContent is null");
        }

        if (!urlContent.contains("/session/")) {
            throw new IllegalArgumentException("Request does not contain /session/ subpath in path");
        }

        session = StringUtils.substringBetween(urlContent, "/session/", "/");

        if (session == null) {
            throw new IllegalArgumentException("Request does not contain session id");
        }

        // can be null
        connection = StringUtils.substringBetween(urlContent, "/connection/", "/");

    }

    public String getSession() {
        return session;
    }

    public String getConnection() {
        return connection;
    }

    public String getSqlStatement() {
        return sqlStatement;
    }

    public String getActionValue() {
        return actionValue;
    }

    /**
     * Debug
     */
    public static void debug(String s) {
        if (DEBUG) {
            System.out.println(new Date() + " " + s);
        }
    }

}
