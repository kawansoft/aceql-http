/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.injection.classes;

import java.io.IOException;
import java.sql.SQLException;

import org.kawanfw.sql.api.server.DatabaseConfigurationException;

public interface NativeTomcatElementsBuilder {

    /**
     * Creates the the datasources and the ConfProperties.
     * @param config servlet configuration elements (native Tomcat only);
     * @throws DatabaseConfigurationException
     * @throws IOException
     * @throws SQLException 
     */
    void create(String propertiesFile) throws DatabaseConfigurationException, IOException, SQLException;

}
