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
import java.util.List;
import java.util.Map;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.listener.UpdateListener;
import org.kawanfw.sql.servlet.injection.classes.InjectedClasses.InjectedClassesBuilder;
import org.kawanfw.sql.servlet.injection.classes.creator.UpdateListenersCreator;
import org.kawanfw.sql.util.SqlTag;

public class AdvancedUpdateListenersLoader implements UpdateListenersLoader {

   
    private String classNameToLoad;

    /**
     * Loads a List of UpdateListener
     * 
     * @param database
     * @param injectedClassesBuilder
     * @param updateListenerClassNames
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws SQLException
     * @throws IOException
     */
    @Override
    public List<UpdateListener> loadUpdateListeners(String database, InjectedClassesBuilder injectedClassesBuilder,
	    List<String> updateListenerClassNames)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, IOException {
		
	String tagUpdateListener = null;
	if (updateListenerClassNames.size() < 2)
	    tagUpdateListener = " UpdateListener class: ";
	else
	    tagUpdateListener = " UpdateListener classes: ";

	System.out.println(SqlTag.SQL_PRODUCT_START + " " + database + " Database - Loading" +  tagUpdateListener);

	Map<String, DatabaseConfigurator> databaseConfigurators = injectedClassesBuilder.getDatabaseConfigurators();

	DatabaseConfigurator databaseConfigurator = databaseConfigurators.get(database);
	
	UpdateListenersCreator updateListenersCreator = new UpdateListenersCreator(updateListenerClassNames, database,
		databaseConfigurator);
	List<UpdateListener> updateListeners = updateListenersCreator.getUpdateListeners();

	updateListenerClassNames = updateListenersCreator.getUpdateListenerClassNames();
	classNameToLoad = updateListenerClassNames.toString();

	return AdvancedUpdateListenersLoaderWrap.loadUpdateListenersWrap(database, injectedClassesBuilder, updateListenerClassNames, updateListeners);
    }

    @Override
    public String getClassNameToLoad() {
	return classNameToLoad;
    }

}
