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
package org.kawanfw.sql.tomcat.properties.threadpool;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ThreadPoolExecutorBuilderCreator {

    private static ThreadPoolExecutorBuilder updateListenersLoader = null;

    /**
     * Creates a ThreadPoolExecutorBuilder instance.
     * 
     * @return a ThreadPoolExecutorBuilder instance.
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws SQLException
     */
    public static ThreadPoolExecutorBuilder createInstance() throws SQLException {

	if (updateListenersLoader == null) {
	    Class<?> c;
	    try {
		c = Class.forName("org.kawanfw.sql.pro.reflection.builders.ProEditionThreadPoolExecutorBuilder");
		Constructor<?> constructor = c.getConstructor();
		updateListenersLoader = (ThreadPoolExecutorBuilder) constructor.newInstance();
		return updateListenersLoader;
	    } catch (ClassNotFoundException e) {
		return new DefaultThreadPoolExecutorBuilder();
	    } catch (Exception e) {
		throw new SQLException(e);
	    }
	}

	return updateListenersLoader;
    }

}
