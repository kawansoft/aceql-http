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
package org.kawanfw.sql.servlet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;

import org.kawanfw.sql.util.FrameworkDebug;

/**
 * 
 * Class that builds the class and the object from a transported value on http.
 * <br>
 * Value is transported as (java class name, string value). JavaValueBuilder
 * allows to rebuild:
 * <ul>
 * <li>The <code>Class</code> type.</li>
 * <li>The <code>Object</code> value cast to the native type.
 * </ul>
 * 
 * @author Nicolas de Pomereu
 */
public class JavaValueBuilder {

    /** Set to true to display/log debug info */
    private static boolean DEBUG = FrameworkDebug.isSet(JavaValueBuilder.class);

    /**
     * The transported value java type (without package name). Example: "Integer"
     */
    private String javaType = null;

    /** The transported value in string format */
    private String stringValue = null;

    /** The class corresponding to the parameter */
    private Class<?> classOfValue = null;

    /** The object value corresponding to the string value, correctly casted */
    private Object value = null;

    /**
     * Constructor.
     * 
     * @param javaType    the transported value java type (without package name).
     *                    Example: "Integer"
     * @param stringValue the transported value
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IllegalArgumentException
     */
    public JavaValueBuilder(String javaType, String stringValue)
	    throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException,
	    InstantiationException, IllegalAccessException, InvocationTargetException {
	this.javaType = javaType;
	this.stringValue = stringValue;

	this.decode();
    }

    /**
     * Decode the java type and build the class type and the casted object
     * corresponding to the value
     * 
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IllegalArgumentException
     */
    private void decode() throws ClassNotFoundException, SecurityException, NoSuchMethodException,
	    IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {

	if (javaType.endsWith("null")) // Should never happen
	{
	    classOfValue = Object.class;
	    value = null;
	} else if (javaType.endsWith("BigDecimal")) {
	    classOfValue = BigDecimal.class;
	    value = new BigDecimal(stringValue);
	} else if (javaType.endsWith("Boolean")) {
	    classOfValue = boolean.class;
	    value = Boolean.valueOf(stringValue);
	} else if (javaType.endsWith("Date")) {
	    classOfValue = java.sql.Date.class;
	    value = java.sql.Date.valueOf(stringValue);
	} else if (javaType.endsWith("Double")) {
	    classOfValue = double.class;
	    value = new Double(stringValue);
	} else if (javaType.endsWith("Float")) {
	    classOfValue = float.class;
	    value = new Float(stringValue);
	} else if (javaType.endsWith("Integer")) {
	    classOfValue = int.class;
	    value = new Integer(stringValue);
	} else if (javaType.endsWith("Long")) {
	    classOfValue = long.class;
	    value = new Long(stringValue);
	} else if (javaType.endsWith("Object")) {
	    classOfValue = Object.class;
	    value = (Object) stringValue;
	} else if (javaType.endsWith("Short")) {
	    classOfValue = short.class;
	    value = new Short(stringValue);
	} else if (javaType.endsWith("String")) {
	    classOfValue = String.class;
	    value = stringValue;
	} else if (javaType.endsWith("Time")) {
	    classOfValue = java.sql.Date.class;
	    value = java.sql.Time.valueOf(stringValue);
	} else if (javaType.endsWith("Timestamp")) {
	    classOfValue = java.sql.Date.class;
	    value = java.sql.Timestamp.valueOf(stringValue);
	} else // Support all classes that have a TheClass(String s) contructor:
	{
	    debug("javaType   : " + javaType + ":");
	    debug("stringValue: " + stringValue + ":");

	    Class<?> paramClass = Class.forName(javaType);
	    Constructor<?> ctor = paramClass.getDeclaredConstructor(String.class);
	    ctor.setAccessible(true);
	    Object o = ctor.newInstance(stringValue);

	    classOfValue = paramClass;
	    value = o;
	}
    }

    /**
     * Returns the class corresponding to the parameter.
     * 
     * @return the the class corresponding to the parameter
     */
    public Class<?> getClassOfValue() {
	return this.classOfValue;
    }

    /**
     * 
     * @return the object value corresponding to the string value, correctly casted
     */
    public Object getValue() {
	return this.value;
    }

    public static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
