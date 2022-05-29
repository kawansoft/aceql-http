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
package org.kawanfw.sql.api.server.firewall;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kawanfw.sql.api.server.DefaultDatabaseConfigurator;
import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.util.firewall.cloudmersive.CloudmersiveApi;
import org.kawanfw.sql.api.util.firewall.cloudmersive.DenySqlInjectionManagerUtil;
import org.kawanfw.sql.util.Tag;

/**
 * A firewall manager that allows detecting SQL injection attacks, using the
 * third-party <a href="https://www.cloudmersive.com">Cloudmersive</a> API: <br>
 * Usage requires getting a Cloudmersive API key through a free or paying
 * account creation at <a href=
 * "https://www.cloudmersive.com/pricing">www.cloudmersive.com/pricing</a>. <br>
 * <br>
 * The Cloudmersive parameters (API key, detection level, ...) are stored in the
 * {@code cloudmersive.properties} file that is loaded at the AceQL server
 * startup. <br>
 * The file must be located in the same directory as the
 * {@code aceql.properties} file used when starting the AceQL server.<br>
 * <br>
 * Note that SQL injections are detected synchronously, which will slow down the
 * SQL calls. The {@code DenySqlInjectionManagerAsync} SQLFirewallManager is
 * provided for asynchronous detection.
 * 
 * 
 * @see DenySqlInjectionManagerAsync
 * @author Nicolas de Pomereu
 * @since 11.0
 */
public class DenySqlInjectionManager extends DefaultSqlFirewallManager implements SqlFirewallManager {

    /** The running instance */
    private CloudmersiveApi cloudmersiveApi = null;
    private Logger logger;

    /**
     * Says if <a href="https://www.cloudmersive.com">Cloudmersive</a> SQL injection
     * detector accepts the SQL statement.
     */
    @Override
    public boolean allowSqlRunAfterAnalysis(SqlEvent sqlEvent, Connection connection) throws IOException, SQLException {

	try {
	    if (logger == null) {
		logger = new DefaultDatabaseConfigurator().getLogger();
	    }

	    String sql = sqlEvent.getSql();

	    // If not loaded, load the APIs & connect to Cloudmersive
	    if (cloudmersiveApi == null) {
		cloudmersiveApi = new CloudmersiveApi(DenySqlInjectionManagerUtil.getCloudmersivePropertiesFile());
	    }

	    return !cloudmersiveApi.sqlInjectionDetect(sql);
	} catch (Exception exception) {
	    exception.printStackTrace();
	    try {
		logger.log(Level.WARNING, Tag.PRODUCT + ": " + DenySqlInjectionManager.class.getSimpleName()
			+ " Unable to verify SQL injection: " + exception.toString());
	    } catch (Exception exception2) {
		exception2.printStackTrace();
	    }
	    return true;
	}
    }

}
