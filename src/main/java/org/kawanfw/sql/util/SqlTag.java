/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.util;

import org.kawanfw.sql.version.VersionWrapper;

/**
 * @author Nicolas de Pomereu
 *
 */
public class SqlTag {
    
    public static final String SQL_PRODUCT_INIT = "["
	    + VersionWrapper.getName().toUpperCase() + " INIT]";
    
    public static final String SQL_PRODUCT_START = "["
	    + VersionWrapper.getName().toUpperCase() + " START]";
    public static final String SQL_PRODUCT_START_FAILURE = "["
	    + VersionWrapper.getName().toUpperCase() + " START FAILURE]";

    public static final String USER_CONFIGURATION = "[USER CONFIGURATION]";
    public static final String SQL_PRODUCT_LICENSE_FAILURE = "["
	    + VersionWrapper.getName().toUpperCase() + " - LICENSE FAILURE]";

    public static final String PLEASE_CORRECT = "Please correct and retry.";

    protected SqlTag() {

    }


}
