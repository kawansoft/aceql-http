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
package org.kawanfw.sql.metadata.util;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;


/**
 * @author Nicolas de Pomereu
 *
 */
public class WrappersGenerator {

    /**
     *
     */
    public WrappersGenerator() {

    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

	String name = null; // Index.class.getName();
	Class<?> clazz = Class.forName(name);
	printWrapperForGetFromSet(clazz);
    }

    /**
     * @param clazz
     * @throws SecurityException
     */
    public static void printWrapperForGetFromSet(Class<?> clazz) throws SecurityException {
	String instanceNameGet = clazz.getSimpleName();
	instanceNameGet = Character.toLowerCase(instanceNameGet.charAt(0)) + instanceNameGet.substring(1);
	String instanceNameSet = "aceql" + clazz.getSimpleName();

	Method[] methods = clazz.getDeclaredMethods();

	Set<String> methodsSet = new HashSet<>();
	for (Method method : methods) {
	    methodsSet.add(method.getName());
	}

	System.out.println();
	System.out.println(clazz.getSimpleName() + " " + instanceNameGet + " = null;");
	System.out.println(clazz.getName() + " " + instanceNameSet + " = null;");
	System.out.println();

	for (int i = 0; i < methods.length; i++) {
	    String name = methods[i].getName();
	    if (StringUtils.startsWith(name, "set")) {
		String name1 = name;

		name1 = "get" + StringUtils.substringAfter(name, "set");

		if (! methodsSet.contains(name1)) {
		    name1 = "is" + StringUtils.substringAfter(name, "set");
		}

		System.out.println(instanceNameSet + "." + name + "( " + instanceNameGet + "." + name1 + "() );");
	    }

	}
    }

}
