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

/**
 *
 * Contains all the request parameters for AceQL in Rest mode
 *
 * @author Nicolas de Pomereu
 *
 */

public class HttpParameter {

    /**
     * Protected
     */
    protected HttpParameter() {

    }

    // Login info
    public static final String USERNAME = "username";
    public static final String SESSION_ID = "session_id";
    public static final String DATABASE = "database";
    public static final String CONNECTION_ID = "connection_id";

    public static final String PASSWORD = "password";
    public static final String PRETTY_PRINTING = "pretty_printing";
    public static final String COLUMN_TYPES = "column_types";

    public static final String ACTION = "action";
    public static final String ACTION_VALUE = "action_value";

    public static final String UNKNOWN = "unknown";

    // Login/Logout actions
    // OLD calls in version 1.0
    public static final String CONNECT = "connect";
    public static final String DISCONNECT = "disconnect";

    // Login/Logout actions
    // New calls in version 1.0
    public static final String LOGIN = "login";
    public static final String LOGOUT = "logout";

    // New action to get a new Connection
    public static final String GET_CONNECTION = "get_connection";

    // Version action
    public static final Object GET_VERSION = "get_version";

    // Meta action
    public static final Object GET_CATALOG = "get_catalog";

    // Connections modifiers & Getters Action & values
    public static final String COMMIT = "commit";
    public static final String ROLLBACK = "rollback";

    public static final String SET_AUTO_COMMIT = "set_auto_commit";
    public static final String SET_READ_ONLY = "set_read_only";
    public static final String SET_TRANSACTION_ISOLATION_LEVEL = "set_transaction_isolation_level";
    public static final String SET_HOLDABILITY = "set_holdability";

    public static final String GET_AUTO_COMMIT = "get_auto_commit";
    public static final String IS_READ_ONLY = "is_read_only";
    public static final String GET_HOLDABILITY = "get_holdability";
    public static final String GET_TRANSACTION_ISOLATION_LEVEL = "get_transaction_isolation_level";

    public static final String NONE = "none";
    public static final String READ_UNCOMMITTED = "read_uncommitted";
    public static final String READ_COMMITTED = "read_committed";
    public static final String REPEATABLE_READ = "repeatable_read";
    public static final String SERIALIZABLE = "serializable";

    public static final String HOLD_CURSORS_OVER_COMMIT = "hold_cursors_over_commit";
    public static final String CLOSE_CURSORS_AT_COMMIT = "close_cursors_at_commit";

    //METADATA Actions
    public static final String METADATA_QUERY_DB_SCHEMA_DOWNLOAD = "metadata_query_db_schema_download";
    public static final String METADATA_QUERY_GET_TABLE_NAMES = "metadata_query_get_table_names";
    public static final String METADATA_QUERY_GET_DB_METADATA = "metadata_query_get_db_metadata";
    public static final String METADATA_QUERY_GET_TABLE_DETAILS = "metadata_query_get_table_details";

    // SAVEPOINTS
    public static final String SET_SAVEPOINT = "set_savepoint";
    public static final String SET_SAVEPOINT_NAME = "set_savepoint_name";
    public static final String ROLLBACK_SAVEPOINT = "rollback_savepoint";
    public static final String RELEASE_SAVEPOINT = "release_savepoint";

    public static final String NAME = "NAME";
    public static final String SAVEPOINT = "SAVEPOINT";

    public static final String CLOSE = "close";

    // New DML Action
    public static final String EXECUTE = "execute";
    public static final String EXECUTE_UPDATE = "execute_update";
    public static final String EXECUTE_QUERY = "execute_query";

    // DML parameters
    public static final String SQL = "sql";
    public static final String PREPARED_STATEMENT = "prepared_statement";
    public static final String GZIP_RESULT = "gzip_result";

    public static final String STORED_PROCEDURE = "stored_procedure";

    public static final String PARAM_VALUE_ = "param_value_";
    public static final String PARAM_TYPE_ = "param_type_";
    public static final String PARAM_DIRECTION_ = "param_direction_";

    // Out register parameters
    public static final String OUT_PARAM_NAME_ = "out_param_name_";

    // Blob actions & parameters
    public static final String BLOB_UPLOAD = "blob_upload";
    public static final String BLOB_DOWNLOAD = "blob_download";
    public static final String GET_BLOB_LENGTH = "get_blob_length";
    public static final String BLOB_ID = "blob_id";

    public static final String HTML_ENCODING = "html_encoding";
    public static final String FILENAME = "filename";

    public static final String CLIENT_VERSION = "client_version";

    public static final String FORMAT = "format";
    public static final String TABLE_NAME = "table_name";
    public static final String TABLE_TYPE = "table_type";





}
