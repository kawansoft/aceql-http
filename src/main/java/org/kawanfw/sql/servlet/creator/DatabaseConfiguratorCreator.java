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
package org.kawanfw.sql.servlet.creator;

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
