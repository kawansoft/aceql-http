/*
 * Copyright (c)2023 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-02-21
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.util;
/**
 * 
 * @author Nicolas de Pomereu Defines the leading Exception tags
 */

public class Tag {

    // Common for all producst, as we don't know the product name at run time,
    // we use
    // generic KAWANSOFT FRAMEWORK
    public static final String RUNNING_PRODUCT = "ACEQL HTTP";
    public static String PRODUCT = "[" + RUNNING_PRODUCT + "]";
    public static String PRODUCT_WARNING = "[" + RUNNING_PRODUCT + " WARNING]";
    public static String WARNING = "[WARNING]";
    public static String PRODUCT_USER_CONFIG_FAIL = "[" + RUNNING_PRODUCT
	    + " - USER CONFIGURATION FAILURE]";
    public static String PRODUCT_PRODUCT_FAIL = "[" + RUNNING_PRODUCT
	    + " FAILURE]";
    public static String PRODUCT_SECURITY = "[" + RUNNING_PRODUCT
	    + " SECURITY]";
    public static String PRODUCT_EXCEPTION_RAISED = "[" + RUNNING_PRODUCT
	    + " - EXCEPTION RAISED]";

    public static final String REQUIRES_ACEQL_ENTERPRISE_EDITION = "requires on server AceQL HTTP Enterprise Edition. See www.aceql.com.";
    
    public static final String ClassNotFoundException = "ClassNotFoundException";
    public static final String InstantiationException = "InstantiationException";
    public static final String NoSuchMethodException = "NoSuchMethodException";
    public static final String InvocationTargetException = "InvocationTargetException";
    public static final String SecurityException = "SecurityException";
    public static final String SQLException = "SQLException";
    public static final String BatchUpdateException = "BatchUpdateException";
    public static final String NullPointerException = "NullPointerException";
    public static final String IllegalArgumentException = "IllegalArgumentException";
    public static final String FileNotFoundException = "FileNotFoundException";
    public static final String IOException = "IOException";

    // NIO case the uploaded .class file java version is incompatible with
    // server java version
    public static final String UnsupportedClassVersionError = "UnsupportedClassVersionError";
}
