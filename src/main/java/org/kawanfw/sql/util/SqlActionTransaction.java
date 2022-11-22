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
package org.kawanfw.sql.util;
/**
 * @author Nicolas de Pomereu
 * 
 *         Parameters to be used in programs
 */

public class SqlActionTransaction {

    public static final String ACTION_SQL_COMMIT = "sql_commit";
    public static final String ACTION_SQL_ROLLBACK = "sql_rollback";
    public static final String ACTION_SQL_CON_CLOSE = "sql_con_close";

    public static final String ACTION_SQL_SET_AUTOCOMMIT = "sql_set_autocommit";
    public static final String ACTION_SQL_SET_READ_ONLY = "sql_set_read_only ";
    public static final String ACTION_SQL_SET_HOLDABILITY = "sql_set_holdability";
    public static final String ACTION_SQL_SET_TRANSACTION_ISOLATION = "sql_set_transaction_isolation=";

    public static final String ACTION_SQL_GET_AUTOCOMMIT = "sql_get_autocommit";
    public static final String ACTION_SQL_IS_READ_ONLY = "sql_is_read_only ";
    public static final String ACTION_SQL_GET_HOLDABILITY = "sql_get_holdability";
    public static final String ACTION_SQL_GET_TRANSACTION_ISOLATION = "sql_get_transaction_isolation=";

    /**
     * Protected Constructor
     */
    protected SqlActionTransaction() {

    }

}
