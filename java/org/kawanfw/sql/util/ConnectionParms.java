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
 */

public class ConnectionParms {

    public static final String CONNECTION_ID = "CONNECTION_ID";

    public static final String AUTOCOMMIT = "AUTOCOMMIT";
    public static final String READONLY = "READONLY";
    public static final String HOLDABILITY = "HOLDABILITY";
    public static final String TRANSACTION_ISOLATION = "TRANSACTION_ISOLATION";

    public static final String NO_PARM = "NO_PARM";
    // Connection Info
    public static final String TIMEOUT = "timeout";
    public static final String VALUE = "value";
    public static final String PROPERTIES = "properties";

    public static final String SCHEMA = "schema";

    public static final String ELEMENTS = "elements";
    public static final String TYPENAME = "typename";

    /**
     * Protected Constructor
     */
    protected ConnectionParms() {
    }

}
