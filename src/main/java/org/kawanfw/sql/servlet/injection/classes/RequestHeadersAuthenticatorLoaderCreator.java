/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2021,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.
 *
 * AceQL HTTP is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * AceQL HTTP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301  USA
 *
 * Any modifications to this file must keep this entire header
 * intact.
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
