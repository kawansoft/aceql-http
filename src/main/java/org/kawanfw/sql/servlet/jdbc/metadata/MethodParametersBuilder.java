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
package org.kawanfw.sql.servlet.jdbc.metadata;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import org.kawanfw.sql.servlet.JavaValueBuilder;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * @author Nicolas de Pomereu
 *
 */
public class MethodParametersBuilder {

    /** Set to true to display/log debug info */
    private static boolean DEBUG = FrameworkDebug.isSet(MethodParametersBuilder.class);

    private List<String> paramTypes;
    private List<String> paramsValues;

    private Class<?>[] methodParamTypes;
    private Object[] methodParamValues;

    /**
     * Constructor.
     *
     * @param paramTypes
     * @param paramsValues
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IllegalArgumentException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws ClassNotFoundException
     */
    public MethodParametersBuilder(List<String> paramTypes, List<String> paramsValues)
	    throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException,
	    InstantiationException, IllegalAccessException, InvocationTargetException {
	this.paramTypes = paramTypes;
	this.paramsValues = paramsValues;
	this.build();
    }

    private void build() throws ClassNotFoundException, SecurityException, NoSuchMethodException,
	    IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
	methodParamTypes = new Class[paramTypes.size()];
	methodParamValues = new Object[paramsValues.size()];

	for (int i = 0; i < paramTypes.size(); i++) {
	    String value = paramsValues.get(i);

	    String javaType = paramTypes.get(i);
	    JavaValueBuilder javaValueBuilder = new JavaValueBuilder(javaType, value);

	    methodParamTypes[i] = javaValueBuilder.getClassOfValue();
	    methodParamValues[i] = javaValueBuilder.getValue();

	    // Trap NULL methodParamValues
	    if (methodParamValues[i].equals("NULL")) {
		methodParamValues[i] = null;
	    }

	    debug("methodParamTypes[i]: " + methodParamTypes[i]);
	    debug("methodParamValues[i]  : " + methodParamValues[i]);
	}
    }

    public Class<?>[] getMethodParamTypes() {
	return methodParamTypes;
    }

    public Object[] getMethodParamValues() {
	return methodParamValues;
    }

    /**
     * Debug tool
     *
     * @param s
     */
    private void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
