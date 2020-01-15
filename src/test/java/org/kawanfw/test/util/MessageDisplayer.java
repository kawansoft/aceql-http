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
package org.kawanfw.test.util;

import java.lang.reflect.Method;
import java.util.Date;

import org.kawanfw.sql.util.FrameworkSystemUtil;
import org.kawanfw.sql.util.Tag;

/**
 * This class is used in all tests to display messages If verbose mode is on all
 * messages are displayed on System.out
 * 
 * If Running on Android messages are sent to MainActivity to be displayed on
 * device screen
 */

public class MessageDisplayer {

    private static boolean verbose = false;

    private static Object activityInstance;

    private static Class<?> activityClass;

    private static Method displayMessageMethod;

    public static void setVerbose(boolean v) {
	verbose = v;
    }

    public static void setActivity(Object activeInstance) {

	try {
	    activityClass = Class
		    .forName("org.kawanfw.sql.android.MainActivity");
	    displayMessageMethod = activityClass.getMethod("displayMessage",
		    new Class[] { String.class });
	} catch (Exception e) {
	    throw new IllegalArgumentException(Tag.PRODUCT_PRODUCT_FAIL
		    + "Impossible to load method displayMessage. Cause: "
		    + e.toString());
	}

	activityInstance = activeInstance;
    }

    /** Initial display to do a top of each class */
    public static void initClassDisplay(String className) {
	display("");
	display("Class " + className);
	display("Begin : " + new Date());
	display("");
    }

    public static void display(String str) {

	if (FrameworkSystemUtil.isAndroid()) {
	    // Display message on device screen
	    // Using reflection api to call
	    // activity.displayMessage(str);
	    try {
		displayMessageMethod.invoke(activityInstance, str);
	    } catch (Exception e) {
		e.printStackTrace(System.out);
	    }

	    if (verbose) {
		// Verbose mode on print messages on System.out
		System.out.println(str);
	    }

	} else {
	    System.out.println(str);
	}
    }

    public static void display() {

	if (FrameworkSystemUtil.isAndroid()) {
	    // Display message on device screen
	    // Using reflection api to call
	    // activity.displayMessage(str);
	    try {
		displayMessageMethod.invoke(activityInstance, "");
	    } catch (Exception e) {
		e.printStackTrace(System.out);
	    }

	    if (verbose) {
		// Verbose mode on print messages on System.out
		System.out.println();
	    }

	} else {
	    System.out.println();
	}
    }
}
