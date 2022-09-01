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

import org.kawanfw.sql.api.server.auth.DefaultUserAuthenticator;
import org.kawanfw.sql.api.server.auth.JdbcUserAuthenticator;
import org.kawanfw.sql.api.server.auth.SshUserAuthenticator;
import org.kawanfw.sql.api.server.auth.UserAuthenticator;
import org.kawanfw.sql.api.server.auth.WindowsUserAuthenticator;

/**
 * @author Nicolas de Pomereu
 *
 */
public class UserAuthenticatorCreator {

    private static String[] PREDEFINED_CLASS_NAMES = {
	    DefaultUserAuthenticator.class.getSimpleName(),
	    JdbcUserAuthenticator.class.getSimpleName(),
	    "LdapUserAuthenticator",
	    SshUserAuthenticator.class.getSimpleName(),
	    "WebServiceUserAuthenticator",
	    WindowsUserAuthenticator.class.getSimpleName() };

    private UserAuthenticator userAuthenticator = null;
    private String userAuthenticatorClassName = null;

    /**
     * @param userAuthenticatorClassName
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     *
     */
    public UserAuthenticatorCreator(final String theUserAuthenticatorClassName)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException {

	if (theUserAuthenticatorClassName != null && !theUserAuthenticatorClassName.isEmpty()) {

	    String theUserAuthenticatorClassNameNew = getNameWithPackage(theUserAuthenticatorClassName);

	    Class<?> clazz = Class.forName(theUserAuthenticatorClassNameNew);
	    Constructor<?> ctr = clazz.getConstructor();
	    userAuthenticator = (UserAuthenticator) ctr.newInstance();
	    userAuthenticatorClassName = theUserAuthenticatorClassNameNew;
	} else {
	    userAuthenticator = new DefaultUserAuthenticator();
	    userAuthenticatorClassName = userAuthenticator.getClass().getName();
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
		String theClassNameNew = UserAuthenticator.class.getPackage()
			.getName() + "." + theClassName;
		return theClassNameNew;
	    }
	}

	return theClassName;
    }

    public UserAuthenticator getUserAuthenticator() {
	return userAuthenticator;
    }

    public String getUserAuthenticatorClassName() {
	return userAuthenticatorClassName;
    }

}
