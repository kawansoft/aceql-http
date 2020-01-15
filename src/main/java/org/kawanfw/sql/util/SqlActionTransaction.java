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

    // public static final String ACTION_SQL_INIT_REMOTE_CONNECTION =
    // "sql_init_remote_connection";

    /*
     * setSavepoint setSavepoint(name) rollback(Savepoint savepoint)
     * releaseSavepoint(Savepoint savepoint)
     * 
     * SAVEPOINT
     */

    public static final String ACTION_SQL_SET_SAVEPOINT = "sql_set_savepoint";
    public static final String ACTION_SQL_SET_SAVEPOINT_NAME = "sql_set_savepoint_name";
    public static final String ACTION_SQL_SET_ROLLBACK_SAVEPOINT = "sql_set_rollback_savepoint";
    public static final String ACTION_SQL_SET_RELEASE_SAVEPOINT = "sql_set_release_savepoint";

    /**
     * Protected Constructor
     */
    protected SqlActionTransaction() {

    }

}
