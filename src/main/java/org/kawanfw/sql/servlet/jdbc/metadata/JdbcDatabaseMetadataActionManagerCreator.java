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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Nicolas de Pomereu
 *
 */
public class JdbcDatabaseMetadataActionManagerCreator {

    private static JdbcDatabaseMetadataActionManager jdbcDatabaseMetadataActionManager = null;

    /**
     * Creates a JdbcDatabaseMetadataActionManager instance.
     * @return a JdbcDatabaseMetadataActionManager instance.
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static JdbcDatabaseMetadataActionManager createInstance()
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException {

	if (jdbcDatabaseMetadataActionManager == null) {
	    Class<?> c = Class.forName("org.kawanfw.sql.pro.reflection.jdbc.metadata.DefaultJdbcDatabaseMetadataActionManager");
	    Constructor<?> constructor = c.getConstructor();
	    jdbcDatabaseMetadataActionManager = (JdbcDatabaseMetadataActionManager) constructor.newInstance();
	}

	return jdbcDatabaseMetadataActionManager;
    }

}
