/*
 * Copyright (c)2023 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-02-21
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
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
		+ Tag.REQUIRES_ACEQL_ENTERPRISE_EDITION);
    }

}
