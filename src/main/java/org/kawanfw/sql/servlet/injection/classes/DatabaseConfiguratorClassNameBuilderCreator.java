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
package org.kawanfw.sql.servlet.injection.classes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * @author Nicolas de Pomereu
 *
 */
public class DatabaseConfiguratorClassNameBuilderCreator {

    private static DatabaseConfiguratorClassNameBuilder databaseConfiguratorClassNameBuilder = null;

    /**
     * Creates a DatabaseConfiguratorClassNameBuilder instance.
     * 
     * @return a DatabaseConfiguratorClassNameBuilder instance.
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws SQLException
     */
    public static DatabaseConfiguratorClassNameBuilder createInstance() throws SQLException {

	if (databaseConfiguratorClassNameBuilder == null) {
	    Class<?> c;
	    try {
		c = Class.forName("org.kawanfw.sql.pro.reflection.builders.ProEditionDatabaseConfiguratorClassNameBuilder");
		Constructor<?> constructor = c.getConstructor();
		databaseConfiguratorClassNameBuilder = (DatabaseConfiguratorClassNameBuilder) constructor.newInstance();
		return databaseConfiguratorClassNameBuilder;
	    } catch (ClassNotFoundException e) {
		return new DefaultDatabaseConfiguratorClassNameBuilder();
	    } catch (Exception e) {
		throw new SQLException(e);		
	    }
	}

	return databaseConfiguratorClassNameBuilder;
    }

}
