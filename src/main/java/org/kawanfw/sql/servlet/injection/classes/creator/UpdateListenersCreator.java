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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.SqlEventWrapper;
import org.kawanfw.sql.api.server.listener.JsonLoggerUpdateListener;
import org.kawanfw.sql.api.server.listener.UpdateListener;

public class UpdateListenersCreator {

    private static final boolean TRACE_ON_START = false;

    private static String[] PREDEFINED_CLASS_NAMES = { JsonLoggerUpdateListener.class.getSimpleName() };

    private Set<String> updateListenerClassNames = new LinkedHashSet<>();
    private Set<UpdateListener> updateListenerManagers = new LinkedHashSet<>();

    public UpdateListenersCreator(Set<String> updateListenerClassNames, String database,
	    DatabaseConfigurator databaseConfigurator)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, IOException {

	if (updateListenerClassNames != null && !updateListenerClassNames.isEmpty()) {

	    for (String updateListenerClassName : updateListenerClassNames) {

		updateListenerClassName = updateListenerClassName.trim();
		updateListenerClassName = getNameWithPackage(updateListenerClassName);

		Class<?> c = Class.forName(updateListenerClassName);
		Constructor<?> constructor = c.getConstructor();
		UpdateListener updateListenerManager = (UpdateListener) constructor.newInstance();

		if (TRACE_ON_START) {
		    try (Connection connection = databaseConfigurator.getConnection(database);) {
			List<Object> parameterValues = new ArrayList<>();
			parameterValues.add("value1");
			parameterValues.add("value2");

			// We call code just to verify it's OK:
			SqlEvent sqlEvent = SqlEventWrapper.sqlEventBuild("username", database, "127.0.0.1",
				"select * from table", false, parameterValues, false);
			updateListenerManager.updateActionPerformed(sqlEvent, connection);
		    }
		}

		updateListenerClassName = updateListenerManager.getClass().getName();

		this.updateListenerManagers.add(updateListenerManager);
		this.updateListenerClassNames.add(updateListenerClassName);
	    }

	}
//	else 
//	{
//	    UpdateListener updateListenerManager = new DefaultUpdateListener();
//	    String updateListenerClassName = updateListenerManager.getClass().getName();
//
//	    this.updateListenerManagers.add(updateListenerManager);
//	    this.updateListenerClassNames.add(updateListenerClassName);
//	}
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
		String theClassNameNew = UpdateListener.class.getPackage().getName() + "." + theClassName;
		return theClassNameNew;
	    }
	}

	return theClassName;
    }

    public Set<UpdateListener> getUpdateListeners() {
	return updateListenerManagers;
    }

    public Set<String> getUpdateListenerClassNames() {
	return updateListenerClassNames;
    }

}
