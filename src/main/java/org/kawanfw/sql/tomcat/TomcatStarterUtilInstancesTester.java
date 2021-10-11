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
package org.kawanfw.sql.tomcat;

import java.lang.reflect.Constructor;
import java.util.Properties;
import java.util.Set;

import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.util.SqlTag;

public class TomcatStarterUtilInstancesTester {

    public static void testConfigurators(Properties properties) {

        if (properties == null) {
            throw new IllegalArgumentException("properties is null");
        }

        System.out.println(
        	SqlTag.SQL_PRODUCT_START + " Testing Declared Configurators:");

        Set<String> databases = TomcatStarterUtil.getDatabaseNames(properties);
        for (String database : databases) {
            // Database configurator
            String databaseConfiguratorClassName = properties.getProperty(
        	    database + "." + ServerSqlManager.DATABASE_CONFIGURATOR_CLASS_NAME);

            if (databaseConfiguratorClassName != null) {
        	loadInstance(databaseConfiguratorClassName);

        	System.out.println(SqlTag.SQL_PRODUCT_START + "  -> " + database
        		+ " Database Configurator " + TomcatStarterUtil.CR_LF
        		+ SqlTag.SQL_PRODUCT_START + "     "
        		+ databaseConfiguratorClassName + " OK.");
            }
        }

        String className = properties.getProperty(
        	ServerSqlManager.BLOB_DOWNLOAD_CONFIGURATOR_CLASS_NAME);

        if (className != null) {
            loadInstance(className);

            System.out.println(SqlTag.SQL_PRODUCT_START + "  -> Configurator "
        	    + className + " OK.");
        }

        className = properties.getProperty(
        	ServerSqlManager.BLOB_UPLOAD_CONFIGURATOR_CLASS_NAME);

        if (className != null) {
            loadInstance(className);

            System.out.println(SqlTag.SQL_PRODUCT_START + "  -> Configurator "
        	    + className + " OK.");
        }

        className = properties
        	.getProperty(ServerSqlManager.SESSION_CONFIGURATOR_CLASS_NAME);

        if (className != null) {
            loadInstance(className);

            System.out.println(SqlTag.SQL_PRODUCT_START + "  -> Configurator "
        	    + className + " OK.");
        }

    }

    static void loadInstance(String configuratorClassName) {
        Class<?> c = null;

        try {
            c = Class.forName(configuratorClassName);

            // @SuppressWarnings("unused")
            // Object theObject = c.newInstance();
            Constructor<?> constructor = c.getConstructor();
            @SuppressWarnings("unused")
            Object theObject = constructor.newInstance();

        } catch (Exception e) {
            throw new IllegalArgumentException(
        	    "Exception when loading Configurator "
        		    + configuratorClassName + ": " + e.toString() + ". "
        		    + SqlTag.PLEASE_CORRECT,
        	    e);
        }

    }

}
