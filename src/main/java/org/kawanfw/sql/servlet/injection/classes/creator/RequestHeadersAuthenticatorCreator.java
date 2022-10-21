/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.injection.classes.creator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.kawanfw.sql.api.server.auth.headers.RequestHeadersAuthenticator;

/**
 * @author Nicolas de Pomereu
 *
 */
public class RequestHeadersAuthenticatorCreator {

    private static String[] PREDEFINED_CLASS_NAMES = {};

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
