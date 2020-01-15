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
    public static String PRODUCT_USER_CONFIG_FAIL = "[" + RUNNING_PRODUCT
	    + " - USER CONFIGURATION FAILURE]";
    public static String PRODUCT_PRODUCT_FAIL = "[" + RUNNING_PRODUCT
	    + " FAILURE]";
    public static String PRODUCT_SECURITY = "[" + RUNNING_PRODUCT
	    + " SECURITY]";
    public static String PRODUCT_EXCEPTION_RAISED = "[" + RUNNING_PRODUCT
	    + " - EXCEPTION RAISED]";

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
