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
 * {@code aceql-server.properties} file used when starting the AceQL server.<br>
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
public class DenySqlInjectionManager implements SqlFirewallManager {

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
    
	
    /**
     * @return <code><b>true</b></code>. (Client programs will be allowed to create
     *         raw <code>Statement</code>, i.e. call statements without parameters.)
     */
    @Override
    public boolean allowStatementClass(String username, String database, Connection connection)
	    throws IOException, SQLException {
	return true;
    }

    /**
     * @return <code><b>true</b></code>. (Client programs will be allowed to call
     *         the Metadata Query API).
     */
    @Override
    public boolean allowMetadataQuery(String username, String database, Connection connection)
	    throws IOException, SQLException {
	return true;
    }

}
