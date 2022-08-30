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
public class RequestHeadersAuthenticatorLoaderCreator {

    private static RequestHeadersAuthenticatorLoader requestHeadersAuthenticatorLoader = null;

    /**
     * Creates a RequestHeadersAuthenticatorLoader instance.
     * 
     * @return a RequestHeadersAuthenticatorLoader instance.
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws SQLException
     */
    public static RequestHeadersAuthenticatorLoader createInstance() throws SQLException {

	if (requestHeadersAuthenticatorLoader == null) {
	    Class<?> c;
	    try {
		c = Class.forName("org.kawanfw.sql.pro.reflection.builders.ProEditionRequestHeadersAuthenticatorLoader");
		Constructor<?> constructor = c.getConstructor();
		requestHeadersAuthenticatorLoader = (RequestHeadersAuthenticatorLoader) constructor.newInstance();
		return requestHeadersAuthenticatorLoader;
	    } catch (ClassNotFoundException e) {
		return new DefaultRequestHeadersAuthenticatorLoader();
	    } catch (Exception e) {
		throw new SQLException(e);
	    }
	}

	return requestHeadersAuthenticatorLoader;
    }

}
