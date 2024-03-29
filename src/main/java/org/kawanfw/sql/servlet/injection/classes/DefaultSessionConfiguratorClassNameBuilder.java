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

import org.kawanfw.sql.api.server.session.DefaultSessionConfigurator;
import org.kawanfw.sql.api.server.session.JwtSessionConfigurator;
import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesStore;
import org.kawanfw.sql.util.Tag;

/**
 * @author Nicolas de Pomereu
 *
 */
public class DefaultSessionConfiguratorClassNameBuilder implements SessionConfiguratorClassNameBuilder {

    @Override
    public String getClassName() {
	String SessionConfiguratorClassName = ConfPropertiesStore.get().getSessionConfiguratorClassName();

	if (!SessionConfiguratorClassName.endsWith(DefaultSessionConfigurator.class.getSimpleName())
		&& SessionConfiguratorClassName.endsWith(JwtSessionConfigurator.class.getSimpleName())) {
	    throw new UnsupportedOperationException(Tag.PRODUCT + " "
		    + "Session Configurator other than DefaultSessionConfigurator & JwtSessionConfigurator "
		    + Tag.REQUIRES_ACEQL_ENTERPRISE_EDITION);
	}

	return SessionConfiguratorClassName;

    }

}
