/*
 * Copyright (c)2022 KawanSoft S.A.S.
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-30
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.metadata;

import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;

import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import org.kawanfw.sql.servlet.sql.json_return.JsonUtil;

/**
 * @author Nicolas de Pomereu
 *
 */
public class JsonDatabaseMetaData {

    private DatabaseMetaData databaseMetaData = null;

    /**
     * @param databaseMetaData
     */
    public JsonDatabaseMetaData(DatabaseMetaData databaseMetaData) {
	this.databaseMetaData = databaseMetaData;
    }

    public String build() throws ClassNotFoundException, IllegalAccessException,
	    IllegalArgumentException, InvocationTargetException {

	Class<?> c = Class.forName(DatabaseMetaData.class.getName());
	Method[] allMethods = c.getDeclaredMethods();

	// System.out.println("allMethods: " + allMethods.length);

	List<Method> noParmMethods = new ArrayList<>();

	for (Method method : allMethods) {

	    Class<?>[] pType = method.getParameterTypes();

	    Class<?> returnType = method.getReturnType();

	    if (pType.length == 0 && isIntStringBooleanReturnType(returnType)) {
		noParmMethods.add(method);
	    }
	}

	JsonGeneratorFactory jf = JsonUtil
		.getJsonGeneratorFactory(JsonUtil.DEFAULT_PRETTY_PRINTING);

	StringWriter sw = new StringWriter();
	JsonGenerator gen = jf.createGenerator(sw);

	gen.writeStartObject().write("status", "OK");

	for (Method method : noParmMethods) {

	    String methodName = method.getName();
	    Class<?> returnType = method.getReturnType();

	    Object obj = null;
	    try {
		obj = method.invoke(databaseMetaData);
	    } catch (Exception e) {
		System.err.println(e);
	    }

	    if (obj == null) {
		String returnTypeStr = getReturnTypeName(returnType);
		gen.write(methodName, returnTypeStr);
	    } else if (obj instanceof Boolean) {
		boolean result = (Boolean) obj;
		gen.write(methodName, result);
	    } else if (obj instanceof String) {
		String result = (String) obj;
		gen.write(methodName, result);
	    } else if (obj instanceof Integer) {
		int result = (Integer) obj;
		gen.write(methodName, result);
	    } else {
		gen.write(methodName, obj.toString());
	    }
	}

	gen.writeEnd();
	gen.close();

	return sw.toString();
    }

    private boolean isIntStringBooleanReturnType(Class<?> returnType) {
//	if (returnType.getSimpleName().equalsIgnoreCase("String")) {
//	    return true;
//	} else if (returnType.getSimpleName().equalsIgnoreCase("Integer")) {
//	    return true;
//	} else if (returnType.getSimpleName().equalsIgnoreCase("Boolean")) {
//	    return true;
//	} else {
//	    return false;
//	}

	if (returnType.getSimpleName().equalsIgnoreCase("String")) {
	    return true;
	} else if (returnType.getSimpleName().equalsIgnoreCase("Integer")) {
	    return true;
	} else {
	    return returnType.getSimpleName().equalsIgnoreCase("Boolean");
	}
    }

    private String getReturnTypeName(Class<?> returnType) {

	if (returnType.getSimpleName().equalsIgnoreCase("String")) {
	    return "null";
	} else if (returnType.getSimpleName().equalsIgnoreCase("Integer")) {
	    return "0";
	} else if (returnType.getSimpleName().equalsIgnoreCase("Boolean")) {
	    return "false";
	} else {
	    return "null";
	}
    }

}
