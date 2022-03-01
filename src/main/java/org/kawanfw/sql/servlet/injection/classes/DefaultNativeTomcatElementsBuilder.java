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

package org.kawanfw.sql.servlet.injection.classes;

import java.io.IOException;
import java.sql.SQLException;

import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.util.Tag;

/**
 * Process to do for Native Tomcat only
 * 
 * @author Nicolas de Pomereu
 *
 */
public class DefaultNativeTomcatElementsBuilder implements NativeTomcatElementsBuilder {

    /**
     * Creates the the datasources and the ConfProperties.
     * 
     * @param the properties file (native Tomcat only);
     * @throws DatabaseConfigurationException
     * @throws IOException
     * @throws SQLException
     */
    @Override
    public void create(String propertiesFile) throws DatabaseConfigurationException, IOException, SQLException {
	throw new UnsupportedOperationException(Tag.PRODUCT + " " + "Using Tomcat or other servlet containers "
		+ Tag.REQUIRES_ACEQL_PROFESSIONAL_EDITION);
    }

}
