/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2020,  KawanSoft SAS
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
package org.kawanfw.sql.servlet.creator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.kawanfw.sql.api.server.auth.headers.DefaultRequestHeadersAuthenticator;
import org.kawanfw.sql.api.server.auth.headers.RequestHeadersAuthenticator;

/**
 * @author Nicolas de Pomereu
 *
 */
public class RequestHeadersAuthenticatorCreator {

    private static String[] PREDEFINED_CLASS_NAMES = {
	    DefaultRequestHeadersAuthenticator.class.getSimpleName() };

    private RequestHeadersAuthenticator requestHeadersAuthenticator = null;
    private String requestHeadersAuthenticatorClassName = null;

    /**
     * @param theRequestHeadersAuthenticatorClassName
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     *
     */
    public RequestHeadersAuthenticatorCreator(final String theRequestHeadersAuthenticatorClassName)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException {

	if (theRequestHeadersAuthenticatorClassName != null && !theRequestHeadersAuthenticatorClassName.isEmpty()) {

	    String theRequestHeadersAuthenticatorClassNameNew = getNameWithPackage(theRequestHeadersAuthenticatorClassName);

	    Class<?> c = Class.forName(theRequestHeadersAuthenticatorClassNameNew);
	    Constructor<?> constructor = c.getConstructor();
	    requestHeadersAuthenticator = (RequestHeadersAuthenticator) constructor.newInstance();
	    requestHeadersAuthenticatorClassName = theRequestHeadersAuthenticatorClassNameNew;
	} else {
	    requestHeadersAuthenticator = new DefaultRequestHeadersAuthenticator();
	    requestHeadersAuthenticatorClassName = requestHeadersAuthenticator.getClass().getName();
	}

    }

    /**
     * Allows to add automatically the package for predefined classes
     *
     * @param theClassName
     * @return
     */
    private static String getNameWithPackage(final String theClassName) {

	for (int i = 0; i < PREDEFINED_CLASS_NAMES.length; i++) {
	    if (PREDEFINED_CLASS_NAMES[i].equals(theClassName)) {
		// Add prefix package
		String theClassNameNew = RequestHeadersAuthenticator.class.getPackage()
			.getName() + "." + theClassName;
		return theClassNameNew;
	    }
	}

	return theClassName;
    }

    public RequestHeadersAuthenticator getRequestHeadersAuthenticator() {
	return requestHeadersAuthenticator;
    }

    public String getRequestHeadersAuthenticatorClassName() {
	return requestHeadersAuthenticatorClassName;
    }

}
