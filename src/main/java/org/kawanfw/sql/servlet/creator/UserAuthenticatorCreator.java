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

import org.kawanfw.sql.api.server.auth.DefaultUserAuthenticator;
import org.kawanfw.sql.api.server.auth.LdapUserAuthenticator;
import org.kawanfw.sql.api.server.auth.SshUserAuthenticator;
import org.kawanfw.sql.api.server.auth.UserAuthenticator;
import org.kawanfw.sql.api.server.auth.WebServiceUserAuthenticator;
import org.kawanfw.sql.api.server.auth.WindowsUserAuthenticator;

/**
 * @author Nicolas de Pomereu
 *
 */
public class UserAuthenticatorCreator {

    private static String[] PREDEFINED_CLASS_NAMES = {
	    DefaultUserAuthenticator.class.getSimpleName(),
	    SshUserAuthenticator.class.getSimpleName(),
	    LdapUserAuthenticator.class.getSimpleName(),
	    WebServiceUserAuthenticator.class.getSimpleName(),
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

	    Class<?> c = Class.forName(theUserAuthenticatorClassNameNew);
	    Constructor<?> constructor = c.getConstructor();
	    userAuthenticator = (UserAuthenticator) constructor.newInstance();
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
