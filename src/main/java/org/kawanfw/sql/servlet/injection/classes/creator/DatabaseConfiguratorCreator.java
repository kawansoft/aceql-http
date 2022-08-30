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
import java.util.logging.Logger;

import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.DefaultDatabaseConfigurator;
import org.kawanfw.sql.util.Tag;

/**
 * @author Nicolas de Pomereu
 *
 */
public class DatabaseConfiguratorCreator {

    private String databaseConfiguratorClassName = null;
    private DatabaseConfigurator databaseConfigurator = null;

    public DatabaseConfiguratorCreator(String theDatabaseConfiguratorClassName)
	    throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException, NoSuchMethodException, SecurityException {

	if (theDatabaseConfiguratorClassName != null && !theDatabaseConfiguratorClassName.isEmpty()) {
	    Class<?> c = Class.forName(theDatabaseConfiguratorClassName);
	    Constructor<?> constructor = c.getConstructor();
	    databaseConfigurator = (DatabaseConfigurator) constructor.newInstance();
	    this.databaseConfiguratorClassName = theDatabaseConfiguratorClassName;
	} else {
	    databaseConfigurator = new DefaultDatabaseConfigurator();
	    this.databaseConfiguratorClassName = databaseConfigurator.getClass().getName();
	}

	// Gets the Logger to trap Exception if any
	try {
	    @SuppressWarnings("unused")
	    Logger logger = databaseConfigurator.getLogger();
	} catch (Exception e) {
	    throw new DatabaseConfigurationException(
		    Tag.PRODUCT_USER_CONFIG_FAIL + " Impossible to get the Logger from DatabaseConfigurator instance",
		    e);
	}

    }

    public String getDatabaseConfiguratorClassName() {
        return databaseConfiguratorClassName;
    }

    public DatabaseConfigurator getDatabaseConfigurator() {
        return databaseConfigurator;
    }


}
