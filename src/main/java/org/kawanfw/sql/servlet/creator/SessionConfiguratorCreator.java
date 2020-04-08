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

import org.kawanfw.sql.api.server.session.DefaultSessionConfigurator;
import org.kawanfw.sql.api.server.session.SessionConfigurator;

/**
 * @author Nicolas de Pomereu
 *
 */
public class SessionConfiguratorCreator {

    private static String[] PREDEFINED_CLASS_NAMES = {
	    org.kawanfw.sql.api.server.session.DefaultSessionConfigurator.class.getSimpleName(),
	    org.kawanfw.sql.api.server.session.JwtSessionConfigurator.class.getSimpleName()
	    };

    /**
     * Allows to add automatically the package for predefined classes
     *
     * @param theClassName
     * @return
     */
    private static String getNameWithPackage(String theClassName) {

	for (int i = 0; i < PREDEFINED_CLASS_NAMES.length; i++) {
	    if (PREDEFINED_CLASS_NAMES[i].equals(theClassName)) {
		// Add prefix package
		theClassName = org.kawanfw.sql.api.server.session.SessionConfigurator.class.getPackage()
			.getName() + "." + theClassName;
		return theClassName;
	    }
	}

	return theClassName;
    }

    private String sessionConfiguratorClassName = null;
    private SessionConfigurator sessionConfigurator = null;

    public SessionConfiguratorCreator(String theSessionConfiguratorClassName)
	    throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException, NoSuchMethodException, SecurityException {

	if (theSessionConfiguratorClassName != null && !theSessionConfiguratorClassName.isEmpty()) {

	    theSessionConfiguratorClassName = getNameWithPackage(theSessionConfiguratorClassName);

	    Class<?> c = Class.forName(theSessionConfiguratorClassName);
	    Constructor<?> constructor = c.getConstructor();
	    sessionConfigurator = (SessionConfigurator) constructor.newInstance();
	    this.sessionConfiguratorClassName = theSessionConfiguratorClassName;
	} else {
	    sessionConfigurator = new DefaultSessionConfigurator();
	    this.sessionConfiguratorClassName = sessionConfigurator.getClass().getName();
	}

    }

    public String getSessionConfiguratorClassName() {
        return sessionConfiguratorClassName;
    }

    public SessionConfigurator getSessionConfigurator() {
        return sessionConfigurator;
    }


}
