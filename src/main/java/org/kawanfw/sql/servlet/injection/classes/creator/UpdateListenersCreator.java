/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2021,  KawanSoft SAS
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

package org.kawanfw.sql.servlet.injection.classes.creator;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.listener.DefaultUpdateListener;
import org.kawanfw.sql.api.server.listener.JsonLoggerUpdateListener;
import org.kawanfw.sql.api.server.listener.SqlActionEventWrapper;
import org.kawanfw.sql.api.server.listener.UpdateListener;

public class UpdateListenersCreator {

    private static String[] PREDEFINED_CLASS_NAMES = {
	    JsonLoggerUpdateListener.class.getSimpleName()
	    };

    private List<String> updateListenerClassNames = new ArrayList<>();
    private List<UpdateListener> updateListenerManagers = new ArrayList<>();

    public UpdateListenersCreator(List<String> updateListenerClassNames, String database,
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

		try (Connection connection = databaseConfigurator.getConnection(database);) {
		    List<Object> parameterValues = new ArrayList<>();

		    // We call code just to verify it's OK:
		    SqlActionEventWrapper.sqlActionEventBuilder("username", database, "127.0.0.1", "select * from table",  false, parameterValues);
		    updateListenerManager.updateActionPerformed(null, connection);
		}

		updateListenerClassName = updateListenerManager.getClass().getName();

		this.updateListenerManagers.add(updateListenerManager);
		this.updateListenerClassNames.add(updateListenerClassName);
	    }

	} else {
	    UpdateListener updateListenerManager = new DefaultUpdateListener();
	    String updateListenerClassName = updateListenerManager.getClass().getName();

	    this.updateListenerManagers.add(updateListenerManager);
	    this.updateListenerClassNames.add(updateListenerClassName);
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
		String theClassNameNew = UpdateListener.class.getPackage()
			.getName() + "." + theClassName;
		return theClassNameNew;
	    }
	}

	return theClassName;
    }

    public List<UpdateListener> getUpdateListeners() {
	return updateListenerManagers;
    }

    public List<String> getUpdateListenerClassNames() {
	return updateListenerClassNames;
    }

}
