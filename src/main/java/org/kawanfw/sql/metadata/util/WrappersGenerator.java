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
