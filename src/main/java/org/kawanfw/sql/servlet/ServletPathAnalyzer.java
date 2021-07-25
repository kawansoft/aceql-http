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
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ServletPathAnalyzer {

    private static boolean DEBUG = FrameworkDebug.isSet(ServletPathAnalyzer.class);

    private String connectionModifierOrReader = null;
    private String sqlStatement = null;
    private String blobAction = null;

    private String actionValue = null;
    private String session = null;
    private String connection;

    private String database;
    private String username;

    private String requestUri;
    private String servletName;
    private String action;

    public ServletPathAnalyzer(String requestUri, String servletName) {
	this.requestUri = requestUri;
	this.servletName = servletName;
	treat();
    }

    private void treat() {
	if (isLoginAction(requestUri, servletName)) {
	    action = "login";
	} else if (isVersionAction(requestUri)) {
	    action = "get_version";
	    buildElements(servletName, requestUri);
	} else if (isConnectionModifierOrReader(requestUri)) {
	    action = getConnectionModifierOrReader();
	    buildElements(servletName, requestUri);
	} else if (isBlobAction(requestUri)) {
	    action = getBlobAction();
	    buildElements(servletName, requestUri);
	} else if (isExecuteFamily(requestUri)) {
	    action = getSqlStatement();
	    buildElements(servletName, requestUri);
	}
	else if (isJdbcDatabaseMetaData(requestUri)) {
	    action = HttpParameter.JDBC_DATABASE_META_DATA;
	    buildElements(servletName, requestUri);
	}
	else if (isMetadataQuery(requestUri)) {
	    ServletMetadataQuery servletMetadataQuery = new ServletMetadataQuery(requestUri);
	    action = servletMetadataQuery.getAction();
	    buildElements(servletName, requestUri);
	}

	else {
	    throw new IllegalArgumentException("Unknown action: " + StringUtils.substringAfterLast(requestUri, "/"));
	}
    }



    public boolean isConnectionModifierOrReader(String requestUri) {

	Objects.requireNonNull(requestUri, "requestUri cannot be null!");

        if (requestUri.endsWith("/get_connection")) {
            connectionModifierOrReader = "get_connection";
            return true;
        }

        if (requestUri.endsWith("/get_catalog")) {
            connectionModifierOrReader = "get_catalog";
            return true;
        }

        if (requestUri.endsWith("/get_schema")) {
            connectionModifierOrReader = "get_catalog";
            return true;
        }

        if (checkCloseCommands(requestUri)) {
            return true;
        }

        if (checkCommitCommands(requestUri)) {
            return true;
        }

        if (checkSavepointCommands(requestUri)) {
            return true;
        }
        
        if (checkHoldabilityAndIsolationCommands(requestUri)) {
            return true;
        }

        return checkReadOnlyCommands(requestUri);

    }

    /**
     * @param requestUri
     */
    private boolean checkReadOnlyCommands(String requestUri) {
	if (requestUri.endsWith("/set_read_only/true") || requestUri.endsWith("/set_read_only/false")) {
            connectionModifierOrReader = "set_read_only";
            actionValue = StringUtils.substringAfterLast(requestUri, "/");
            return true;
        }

        if (requestUri.endsWith("/is_read_only")) {
            connectionModifierOrReader = "is_read_only";
            return true;
        }

        return false;
    }

    /**
     * @param requestUri
     */
    private boolean checkHoldabilityAndIsolationCommands(String requestUri) {
	if (requestUri.contains("/set_holdability/")) {
            connectionModifierOrReader = "set_holdability";
            actionValue = StringUtils.substringAfterLast(requestUri, "/");
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

        if (requestUri.contains("/set_transaction_isolation_level/")) {
            connectionModifierOrReader = "set_transaction_isolation_level";
            actionValue = StringUtils.substringAfterLast(requestUri, "/");
            return true;
        }

        return false;
    }

    /**
     * @param requestUri
     */
    private boolean checkCloseCommands(String requestUri) {
	if (requestUri.endsWith("/close")) {
            connectionModifierOrReader = "close";
            return true;
        }

        if (requestUri.endsWith("/logout") || requestUri.endsWith("/disconnect")) {
            connectionModifierOrReader = "logout";
            return true;
        }

        return false;
    }

    /**
     * @param requestUri
     */
    private boolean checkCommitCommands(String requestUri) {
	if (requestUri.endsWith("/commit")) {
            connectionModifierOrReader = "commit";
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

        if (requestUri.endsWith("/get_auto_commit")) {
            connectionModifierOrReader = "get_auto_commit";
            return true;
        }
        
        return false;
    }
    
    /**
     * @param requestUri
     */
    private boolean checkSavepointCommands(String requestUri) {

	if (requestUri.contains("/set_named_savepoint")) {
            connectionModifierOrReader = "set_named_savepoint";
            return true;
        }	
	
	if (requestUri.contains("/set_savepoint")) {
            connectionModifierOrReader = "set_savepoint";
            return true;
        }
	
	if (requestUri.contains("/rollback_savepoint")) {
            connectionModifierOrReader = "rollback_savepoint";
            return true;
        }
	
	if (requestUri.contains("/release_savepoint")) {
            connectionModifierOrReader = "release_savepoint";
            return true;
        }
        
        return false;
    }

    public boolean isLoginAction(final String requestUri, String servletName) {

	String requestUriNew = requestUri;

	if (isLoginAction(requestUriNew)) {

	    if (!requestUriNew.contains("/" + servletName + "/database/")) {
		throw new IllegalArgumentException("Request does not contain /database/ subpath in path");
	    }

	    if (!requestUriNew.contains("/username/")) {
		throw new IllegalArgumentException("Request does not contain /username/ subpath in path");
	    }

	    database = StringUtils.substringBetween(requestUriNew, "/database/", "/username");

	    // Accept /connect pattern
	    if (requestUriNew.endsWith("/connect")) {
		requestUriNew = StringUtils.substringBeforeLast(requestUriNew, "/connect") + "/login";
	    } else if (requestUriNew.contains("/connect?")) {
		requestUriNew = StringUtils.substringBeforeLast(requestUriNew, "/connect?") + "/login?";
	    }

	    username = StringUtils.substringBetween(requestUriNew, "/username/", "/login");
	    return true;
	} else {
	    return false;
	}

    }

    private boolean isLoginAction(String requestUri) {
	return requestUri.endsWith("/login") || requestUri.endsWith("/connect");
    }

    public boolean isVersionAction(String urlContent) {
	Objects.requireNonNull(urlContent, "urlContent cannot be null!");
	return urlContent.endsWith("/get_version");
    }

    public boolean isBlobAction(String urlContent) {
	Objects.requireNonNull(urlContent, "urlContent cannot be null!");

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
        Objects.requireNonNull(blobAction, "blobAction cannot be null. Call isBlobAction() before");
        return blobAction;
    }

    public String getConnectionModifierOrReader() {
        Objects.requireNonNull(connectionModifierOrReader, "connectionModifierOrReader cannot be null. Call isConnectionModifier() before");
        return connectionModifierOrReader;
    }

    public boolean isExecuteFamily(String urlContent) {
	Objects.requireNonNull(urlContent, "urlContent cannot be null!");

        if (urlContent.endsWith("/execute_update")) {
            sqlStatement = "execute_update";
            return true;
        }

        if (urlContent.endsWith("/execute_query")) {
            sqlStatement = "execute_query";
            return true;
        }

        if (urlContent.endsWith("/prepared_statement_execute_batch")) {
            sqlStatement = "prepared_statement_execute_batch";
            return true;
        }
        
        if (urlContent.endsWith("/statement_execute_batch")) {
            sqlStatement = "statement_execute_batch";
            return true;
        }
        
        if (urlContent.endsWith("/execute")) {
            sqlStatement = "execute";
            return true;
        }

        return false;

    }

    private boolean isJdbcDatabaseMetaData(String urlContent) {
	Objects.requireNonNull(urlContent, "urlContent cannot be null!");
	return urlContent.contains("/jdbc/database_meta_data");
    }

    public boolean isMetadataQuery(final String urlContent) {
	Objects.requireNonNull(urlContent, "urlContent cannot be null!");

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

	Objects.requireNonNull(urlContent, "urlContent cannot be null!");

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



    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
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

    public String getAction() {
        return action;
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
