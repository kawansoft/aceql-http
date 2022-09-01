/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
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
