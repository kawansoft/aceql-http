/*
 * Copyright (c)2023 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-02-21
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.injection.classes.creator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.kawanfw.sql.api.server.logging.DefaultLoggerCreator;
import org.kawanfw.sql.api.server.logging.LoggerCreator;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * @author Nicolas de Pomereu
 *
 */
public class LoggerCreatorBuilder {
    private static boolean DEBUG = FrameworkDebug.isSet(LoggerCreatorBuilder.class);

    private static String[] PREDEFINED_CLASS_NAMES = { DefaultLoggerCreator.class.getSimpleName() };

    private String loggerCreatorClassName;

    public LoggerCreatorBuilder(String loggerCreatorClassName) {
	super();
	this.loggerCreatorClassName = loggerCreatorClassName;
    }

    public LoggerCreator getLoggerCreator() throws ClassNotFoundException, NoSuchMethodException,
	    InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

	if (loggerCreatorClassName == null) {
	    loggerCreatorClassName = DefaultLoggerCreator.class.getSimpleName();
	}

	loggerCreatorClassName = loggerCreatorClassName.trim();
	loggerCreatorClassName = getNameWithPackage(loggerCreatorClassName);

	debug("");
	debug("loggerCreatorClassName with Package to load: " + loggerCreatorClassName + ":");

	Class<?> c = Class.forName(loggerCreatorClassName);
	Constructor<?> constructor = c.getConstructor();
	LoggerCreator loggerCreator = (LoggerCreator) constructor.newInstance();

	debug("loggerCreator implementation loaded: " + loggerCreatorClassName);
	
	return loggerCreator;

    }

    public String getLoggerCreatorClassName() {
        return loggerCreatorClassName;
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
		String theClassNameNew = DefaultLoggerCreator.class.getPackage().getName() + "." + theClassName;
		return theClassNameNew;
	    }
	}

	return theClassName;
    }

    public void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }



}
