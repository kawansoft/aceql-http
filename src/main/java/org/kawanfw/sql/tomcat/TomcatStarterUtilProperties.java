/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2021,  KawanSoft SAS
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
