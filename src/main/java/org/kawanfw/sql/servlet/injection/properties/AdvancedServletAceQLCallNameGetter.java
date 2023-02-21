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
package org.kawanfw.sql.servlet.injection.properties;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.tomcat.ServletAceQLCallNameGetter;
import org.kawanfw.sql.util.SqlTag;

/**
 * @author Nicolas de Pomereu
 *
 */
public class AdvancedServletAceQLCallNameGetter implements ServletAceQLCallNameGetter {

    @Override
    public String getName() throws IOException, SQLException {
	
	Properties properties = PropertiesFileUtil.getProperties(PropertiesFileStore.get());
		
	//String aceQLManagerServletCallName = ProEditionServletAceQLCallNameGetterWrap.getNameWrap(properties);
        String aceQLManagerServletCallName = properties.getProperty("aceQLManagerServletCallName");
        
	// Support old name:
	if (aceQLManagerServletCallName == null || aceQLManagerServletCallName.isEmpty()) {
	    aceQLManagerServletCallName = properties.getProperty("serverSqlManagerServletName");
	}

	if (aceQLManagerServletCallName == null || aceQLManagerServletCallName.isEmpty()) {
	    throw new DatabaseConfigurationException(
		    "aceQLManagerServletCallName property is null. " + SqlTag.PLEASE_CORRECT);
	}

	if (aceQLManagerServletCallName.contains("/")) {
	    throw new DatabaseConfigurationException(
		    "aceQLManagerServletCallName property can not contain \"/\" separator. " + SqlTag.PLEASE_CORRECT);
	}

	aceQLManagerServletCallName = aceQLManagerServletCallName.trim();
	return aceQLManagerServletCallName;
    }
    

}
