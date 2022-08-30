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
package org.kawanfw.sql.tomcat.properties.pool;

import java.lang.reflect.Constructor;
import java.sql.SQLException;

/**
 * 
 * @author Nicolas de Pomereu
 *
 */
public class PoolPropertiesInterceptorCreator {

    private static PoolPropertiesInterceptor poolPropertiesInterceptor = null;
    
    public static PoolPropertiesInterceptor createInstance() throws SQLException {
	if (poolPropertiesInterceptor == null) {
	    Class<?> c;
	    try {
		c = Class.forName("org.kawanfw.sql.pro.reflection.builders.ProEditionPoolPropertiesInterceptor");
		Constructor<?> constructor = c.getConstructor();
		poolPropertiesInterceptor = (PoolPropertiesInterceptor) constructor.newInstance();
		return poolPropertiesInterceptor;
	    } catch (ClassNotFoundException e) {
		return new DefaultPoolPropertiesInterceptor();
	    } catch (Exception e) {
		throw new SQLException(e);
	    } 
	}

	return poolPropertiesInterceptor;
    }

}
