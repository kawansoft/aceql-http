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
package org.kawanfw.sql.servlet.jdbc.metadata;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

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
