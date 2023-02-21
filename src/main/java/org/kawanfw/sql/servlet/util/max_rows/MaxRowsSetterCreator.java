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
package org.kawanfw.sql.servlet.util.max_rows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class MaxRowsSetterCreator {

    private static MaxRowsSetter maxRowsSetter = null;

    /**
     * Creates a MaxRowsSetter instance.
     * 
     * @return a MaxRowsSetter instance.
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws SQLException
     */
    public static MaxRowsSetter createInstance() throws SQLException {

	if (maxRowsSetter == null) {
	    Class<?> c;
	    try {
		c = Class.forName("org.kawanfw.sql.pro.reflection.builders.ProEditionMaxRowsSetter");
		Constructor<?> constructor = c.getConstructor();
		maxRowsSetter = (MaxRowsSetter) constructor.newInstance();
		return maxRowsSetter;
	    } catch (ClassNotFoundException e) {
		return new DefaultMaxRowsSetter();
	    } catch (Exception e) {
		throw new SQLException(e);
	    }
	}

	return maxRowsSetter;
    }
}
