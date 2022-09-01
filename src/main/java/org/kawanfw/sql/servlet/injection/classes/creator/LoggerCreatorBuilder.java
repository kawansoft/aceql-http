/*
 * This file is part of KawanWall.
 * KawanWall: Firewall for SQL statements
 * Copyright (C) 2022,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.                 
 *                                                                         
 * KawanWall is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.         
 *              
 * KawanWall is distributed in the hope that it will be useful,               
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU Affero General Public License for more details.       
 *                                  
 * You should have received a copy of the GNU Affero General 
 * Public License along with this program; if not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * If you develop commercial activities using KawanWall, you must: 
 * a) disclose and distribute all source code of your own product,
 * b) license your own product under the GNU General Public License.
 * 
 * You can be released from the requirements of the license by
 * purchasing a commercial license. Buying such a license will allow you 
 * to ship KawanWall with your closed source products without disclosing 
 * the source code.
 *
 * For more information, please contact KawanSoft SAS at this
 * address: sales@kawansoft.com
 * 
 * Any modifications to this file must keep this entire header
 * intact.
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
