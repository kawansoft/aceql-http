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
package org.kawanfw.sql.servlet.injection.properties;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Nicolas de Pomereu
 *
 */
public class PropertiesLoggerCreatorClassNameGetter {

    /**
     *  Constructor
     */
    public PropertiesLoggerCreatorClassNameGetter() {

    }

    public String getName() throws IOException, SQLException {
	Properties properties = PropertiesFileUtil.getProperties(PropertiesFileStore.get());
	
	//String aceQLManagerServletCallName = ProEditionServletAceQLCallNameGetterWrap.getNameWrap(properties);
        String loggerCreatorClassName = properties.getProperty("loggerCreatorClassName");
        
	if (loggerCreatorClassName == null || loggerCreatorClassName.isEmpty()) {
	    loggerCreatorClassName = "DefaultLoggerCreator";
	}

	loggerCreatorClassName = loggerCreatorClassName.trim();
	return loggerCreatorClassName;
    }

}
