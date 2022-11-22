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
package org.kawanfw.sql.servlet.injection.classes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * @author Nicolas de Pomereu
 *
 */
public class SessionConfiguratorClassNameBuilderCreator {

    private static SessionConfiguratorClassNameBuilder sessionConfiguratorClassNameBuilder = null;

    /**
     * Creates a SessionConfiguratorClassNameBuilder instance.
     * 
     * @return a SessionConfiguratorClassNameBuilder instance.
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws SQLException
     */
    public static SessionConfiguratorClassNameBuilder createInstance() throws SQLException {

	if (sessionConfiguratorClassNameBuilder == null) {
	    Class<?> c;
	    try {
		c = Class.forName(
			"org.kawanfw.sql.pro.reflection.builders.ProEditionSessionConfiguratorClassNameBuilder");
		Constructor<?> constructor = c.getConstructor();
		sessionConfiguratorClassNameBuilder = (SessionConfiguratorClassNameBuilder) constructor.newInstance();
		return sessionConfiguratorClassNameBuilder;
	    } catch (ClassNotFoundException e) {
		return new DefaultSessionConfiguratorClassNameBuilder();
	    } catch (Exception e) {
		throw new SQLException(e);
	    }
	}

	return sessionConfiguratorClassNameBuilder;
    }

}
