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
package org.kawanfw.sql.servlet.injection.classes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * @author Nicolas de Pomereu
 *
 */
public class UpdateListenersLoaderCreator  {

    private static UpdateListenersLoader updateListenersLoader = null;

    /**
     * Creates a UpdateListenersLoader instance.
     * 
     * @return a UpdateListenersLoader instance.
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws SQLException
     */
    public static UpdateListenersLoader createInstance() throws SQLException {

	if (updateListenersLoader == null) {
	    
	    Class<?> c;
	    try {
		c = Class.forName("org.kawanfw.sql.pro.reflection.builders.ProEditionUpdateListenersLoader");
		Constructor<?> constructor = c.getConstructor();
		updateListenersLoader = (UpdateListenersLoader) constructor.newInstance();
		return updateListenersLoader;
	    } catch (ClassNotFoundException e) {
		return new DefaultUpdateListenersLoader();
	    } catch (Exception e) {
		throw new SQLException(e);
	    }
	}

	return updateListenersLoader;
    }

}
