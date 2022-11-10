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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ServletsNamesGetterCreator {

    private static ServletNamesGetter servletNamesGetter = null;
    
    /**
     * Creates a ServletsNamesGetterCreator instance.
     * 
     * @return a ServletsNamesGetterCreator instance.
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws SQLException
     */
    public static ServletNamesGetter createInstance() throws SQLException {

	if (servletNamesGetter == null) {
	    Class<?> c;
	    try {
		c = Class.forName("org.kawanfw.sql.pro.reflection.builders.ProEditionServletNamesGetter");
		Constructor<?> constructor = c.getConstructor();
		servletNamesGetter = (ServletNamesGetter) constructor.newInstance();
		return servletNamesGetter;
	    } catch (ClassNotFoundException e) {
		return new DefaultServletNamesGetter();
	    } catch (Exception e) {
		throw new SQLException(e);
	    }
	}

	return servletNamesGetter;
    }

}
