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
package org.kawanfw.sql.util.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * 
 * Allows to invoke any method. Usage : to invoke a configurator method that is
 * implemented or not.
 * 
 * @author Nicolas de Pomereu
 * 
 */
public class Invoker {

    /**
     * No constructor usage
     */
    protected Invoker() {
    }

    /**
     * Invoke a method of a concrete instance with it's parameters.
     * 
     * @param concreteInstance
     *            the the concrete instance
     * @param methodName
     *            the method to call
     * @param param
     *            the parameter values of the method, if any
     * 
     * @return the method result when invoked, or null if the method is not
     *         implemented.
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static Object getMethodResult(Object concreteInstance,
	    String methodName, Object... param) throws SecurityException,
	    NoSuchMethodException, IllegalAccessException,
	    IllegalArgumentException, InvocationTargetException {

	Class<?> c = concreteInstance.getClass();
	Object obj = getMethodResult(concreteInstance, methodName, c, param);
	return obj;

    }

    /**
     * Call recursively the instance or parent instance in order to execute the
     * method.
     * 
     * @param concreteInstance
     *            the concrete instance
     * @param methodName
     *            the method we want to call
     * @param c
     *            the class or parent class to call
     * @param param
     *            the parameter values, if any
     * @return the method result when invoked, or null if the method is not
     *         implemented
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private static Object getMethodResult(Object concreteInstance,
	    String methodName, Class<?> c, Object... param)
	    throws SecurityException, NoSuchMethodException,
	    IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException {
	Method[] allMethods = c.getDeclaredMethods();

	boolean methodExists = false;

	for (Method m : allMethods) {
	    if (m.getName().equals(methodName)) {
		methodExists = true;
		break;
	    }
	}

	Class<?>[] argTypes = new Class[param.length];

	for (int i = 0; i < param.length; i++) {
	    argTypes[i] = param[i].getClass();
	}

	if (methodExists) {
	    Method main = c.getDeclaredMethod(methodName, argTypes);
	    Object result = main.invoke(concreteInstance, param);
	    return result;
	} else {
	    // Maybe there is a super class?
	    Class<?> cSuper = c.getSuperclass();

	    if (cSuper == null) {
		return null;
	    } else {
		return getMethodResult(concreteInstance, methodName, cSuper,
			param);
	    }
	}
    }

    /**
     * Says it a method is implemented in a class
     * 
     * @param className
     *            the class name
     * @param methodName
     *            the method name
     * @return true if the method exists in the class
     * 
     * @throws SQLException
     *             if any Exception occurs, it is wrapped into an SQLException
     */
    public static boolean existsMethod(String className, String methodName)
	    throws SQLException {

	Class<?> c = null;

	try {
	    c = Class.forName(className);
	} catch (Exception e) {
	    throw new SQLException(e);
	}

	Method[] allMethods = c.getDeclaredMethods();

	boolean methodExists = false;

	for (Method m : allMethods) {
	    if (m.getName().equals(methodName)) {
		methodExists = true;
		break;
	    }
	}

	return methodExists;
    }
}
