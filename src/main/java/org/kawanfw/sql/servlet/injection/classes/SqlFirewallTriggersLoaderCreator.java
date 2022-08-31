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
package org.kawanfw.sql.servlet.injection.classes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * @author Nicolas de Pomereu
 *
 */
public class SqlFirewallTriggersLoaderCreator  {

    private static SqlFirewallTriggersLoader sqlFirewallTriggersLoader = null;

    /**
     * Creates a SqlFirewallTriggersLoader instance.
     * 
     * @return a SqlFirewallTriggersLoader instance.
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws SQLException
     */
    public static SqlFirewallTriggersLoader createInstance() throws SQLException {

	if (sqlFirewallTriggersLoader == null) {
	    
	    Class<?> c;
	    try {
		c = Class.forName("org.kawanfw.sql.pro.reflection.builders.ProEditionSqlFirewallTriggersLoader");
		Constructor<?> constructor = c.getConstructor();
		sqlFirewallTriggersLoader = (SqlFirewallTriggersLoader) constructor.newInstance();
		return sqlFirewallTriggersLoader;
	    } catch (ClassNotFoundException e) {
		return new DefaultSqlFirewallTriggersLoader();
	    } catch (Exception e) {
		throw new SQLException(e);
	    }
	}

	return sqlFirewallTriggersLoader;
    }

}
