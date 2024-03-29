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

import java.io.File;

import org.kawanfw.sql.api.util.webserver.WebServerApiWrapper;
import org.kawanfw.sql.util.Tag;

/**
 * @author Nicolas de Pomereu
 *
 */
public class DefaultWebServerStarter implements WebServerStarter {

    @Override
    public void startServer(WebServerApiWrapper webServerApiWrapper, String host, int port, File propertiesFile) {
	    throw new UnsupportedOperationException(Tag.PRODUCT + " "
		    + "WebServerApi usage "
		    + Tag.REQUIRES_ACEQL_ENTERPRISE_EDITION);
    }


}
