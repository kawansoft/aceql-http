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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.kawanfw.sql.api.server.listener.UpdateListener;
import org.kawanfw.sql.servlet.injection.classes.InjectedClasses.InjectedClassesBuilder;

public class DefaultUpdateListenersLoader implements UpdateListenersLoader {

    
    @Override
    public List<UpdateListener> loadUpdateListeners(String database, InjectedClassesBuilder injectedClassesBuilder,
	    List<String> updateListenerClassNames)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException {

	List<UpdateListener> updateListeners =  new ArrayList<>();
	return updateListeners;
    }

    @Override
    public String getClassNameToLoad() {
	List<String> classNameToLoad =  new ArrayList<>();
	return classNameToLoad.toString();
    }

}
