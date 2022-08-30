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
package org.kawanfw.sql.servlet.sql.callable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ServerCallableStatementWrapperCreator {

    private static ServerCallableStatementWrapper serverCallableStatementWrapper = null;

    /**
     * Creates a ServerCallableStatementWrapper instance.
     * @return a ServerCallableStatementWrapper instance.
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static ServerCallableStatementWrapper createInstance()
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException {

	if (serverCallableStatementWrapper == null) {
	    Class<?> c = Class.forName("org.kawanfw.sql.pro.reflection.builders.DefaultServerCallableStatementWrapper");
	    Constructor<?> constructor = c.getConstructor();
	    serverCallableStatementWrapper = (ServerCallableStatementWrapper) constructor.newInstance();
	}

	return serverCallableStatementWrapper;
    }

}
