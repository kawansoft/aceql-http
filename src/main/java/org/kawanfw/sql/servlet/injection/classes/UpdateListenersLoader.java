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
package org.kawanfw.sql.servlet.injection.classes;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Set;

import org.kawanfw.sql.api.server.listener.UpdateListener;
import org.kawanfw.sql.servlet.injection.classes.InjectedClasses.InjectedClassesBuilder;

public interface UpdateListenersLoader {

    /**
     * Loads a UpdateListenersLoader instance.
     * 
     * @param database
     * @param injectedClassesBuilder
     * @param updateListenerClassNames
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    Set<UpdateListener> loadUpdateListeners(String database, InjectedClassesBuilder injectedClassesBuilder,
	    Set<String> updateListenerClassNames)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, IOException;

    /**
     * Returns the List of class names to load in .toString() format.
     * 
     * @return the List of class names to load in .toString() format.
     */
    String getClassNameToLoad();

}
