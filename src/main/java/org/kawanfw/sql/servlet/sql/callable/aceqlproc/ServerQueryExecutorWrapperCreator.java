/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.sql.callable.aceqlproc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ServerQueryExecutorWrapperCreator {

    private static ServerQueryExecutorWrapper serverQueryExecutorWrapper = null;

    /**
     * Creates a ServerQueryExecutorWrapper instance.
     * @return a ServerQueryExecutorWrapper instance.
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static ServerQueryExecutorWrapper createInstance()
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException {

	if (serverQueryExecutorWrapper == null) {
	    Class<?> c = Class.forName("org.kawanfw.sql.pro.reflection.builders.DefaultServerQueryExecutorWrapper");
	    Constructor<?> constructor = c.getConstructor();
	    serverQueryExecutorWrapper = (ServerQueryExecutorWrapper) constructor.newInstance();
	}

	return serverQueryExecutorWrapper;
    }

}
