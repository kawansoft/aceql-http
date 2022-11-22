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
package org.kawanfw.sql.tomcat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kawanfw.sql.util.FrameworkDebug;

public class TomcatStarterUtilProperties {

    private static boolean DEBUG = FrameworkDebug.isSet(TomcatStarterUtilProperties.class);
    
    public static List<String> getList(String classNameArray) {

	debug("classNameArray: " + classNameArray + ":");
	
	List<String> classNames = new ArrayList<>();

	if (classNameArray == null || classNameArray.isEmpty()) {
	    return classNames;
	}

	String [] array = classNameArray.split(",");
	for (String className  : array) {
	    debug("className: " + className.trim() + ":");
	    classNames.add(className.trim());
	}
	
	return classNames;
    }

    /**
     * Method called by children Servlet for debug purpose Println is done only if
     * class name name is in kawansoft-debug.ini
     */
    public static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }
    
}
