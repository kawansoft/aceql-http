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
package org.kawanfw.sql.servlet.injection.classes.creator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.kawanfw.sql.api.server.session.DefaultSessionConfigurator;
import org.kawanfw.sql.api.server.session.JwtSessionConfigurator;
import org.kawanfw.sql.api.server.session.SessionConfigurator;

/**
 * @author Nicolas de Pomereu
 *
 */
public class SessionConfiguratorCreator {

    private String sessionConfiguratorClassName = null;
    private SessionConfigurator sessionConfigurator = null;

    private static String[] PREDEFINED_CLASS_NAMES = {
	    DefaultSessionConfigurator.class.getSimpleName(),
	    JwtSessionConfigurator.class.getSimpleName()
	    };

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
		String theClassNameNew = SessionConfigurator.class.getPackage()
			.getName() + "." + theClassName;
		return theClassNameNew;
	    }
	}

	return theClassName;
    }


    public SessionConfiguratorCreator(final String theSessionConfiguratorClassName)
	    throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException, NoSuchMethodException, SecurityException {

	if (theSessionConfiguratorClassName != null && !theSessionConfiguratorClassName.isEmpty()) {

	    String theSessionConfiguratorClassNameNew = getNameWithPackage(theSessionConfiguratorClassName);

	    Class<?> c = Class.forName(theSessionConfiguratorClassNameNew);
	    Constructor<?> constructor = c.getConstructor();
	    sessionConfigurator = (SessionConfigurator) constructor.newInstance();
	    this.sessionConfiguratorClassName = theSessionConfiguratorClassNameNew;
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
