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
package org.kawanfw.sql.servlet.injection.properties;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import org.kawanfw.sql.api.server.DatabaseConfigurationException;

/**
 * Methods for properties and jasypt encrypted properties loading.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class PropertiesFileUtil {


    /**
     * Returns the Properties extracted from a file.
     *
     * @param file the file containing the properties
     * @return the Properties extracted from the file
     *
     * @throws IOException
     * @throws DatabaseConfigurationException
     * @throws SQLException 
     */
    public static Properties getProperties(File file) throws IOException {
	PropertiesBuilder propertiesBuilder =  PropertiesBuilderCreator.createInstance();
	return propertiesBuilder.getProperties(file);
    }

}
