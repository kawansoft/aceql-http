/*
 * Copyright (c)2022 KawanSoft S.A.S.
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-30
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
import org.kawanfw.sql.api.server.firewall.trigger.SqlFirewallTrigger;
import org.kawanfw.sql.api.util.firewall.cloudmersive.CloudmersiveApi;
import org.kawanfw.sql.api.util.firewall.cloudmersive.DenySqlInjectionManagerUtil;
import org.kawanfw.sql.util.Tag;

/**
 * A firewall manager that allows detecting SQL <i>asynchronously</i> injection
 * attacks, using the third-party
 * <a href="https://www.cloudmersive.com">Cloudmersive</a> API: <br>
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
 * The SQL injection detection is asynchronous: this means that
 * {@code allowSqlRunAfterAnalysis} will always immediately return {@code true}
 * and that the result of the analysis will trigger later all
 * {@code SqlFirewallTrigger} defined in the {@code aceql-server.properties} file. <br>
 * <br>
 * Note that because of the asynchronous behavior, a new {@code Connection} will
 * be extracted from the pool in order to process the
 * {@link SqlFirewallTrigger#runIfStatementRefused(SqlEvent, SqlFirewallManager, Connection)}
 * methods. <br>
 * The {@code Connection} will be cleanly released after all calls.
 * 
 * @see DenySqlInjectionManager
 * 
 * @author Nicolas de Pomereu
 * @since 11.0
 */
public class DenySqlInjectionManagerAsync implements SqlFirewallManager {

    /** The running instance */
    private CloudmersiveApi cloudmersiveApi = null;
    private Logger logger;

    /**
     * Allows detecting in background / asynchronously if
     * <a href="https://www.cloudmersive.com">Cloudmersive</a> SQL injection
     * detector accepts the SQL statement. (The {@code allowSqlRunAfterAnalysis} call thus always returns immediately
     * {@code true}).
     */
    @Override
    public boolean allowSqlRunAfterAnalysis(SqlEvent sqlEvent, Connection connection) throws IOException, SQLException {

	try {
	    if (logger == null) {
		logger = new DefaultDatabaseConfigurator().getLogger();
	    }
	    // If not loaded, load the APIs & connect to Cloudmersive
	    if (cloudmersiveApi == null) {
		cloudmersiveApi = new CloudmersiveApi(DenySqlInjectionManagerUtil.getCloudmersivePropertiesFile());
	    }

	    cloudmersiveApi.sqlInjectionDetectAsync(sqlEvent, this);
	    return true;
	} catch (Exception exception) {
	    exception.printStackTrace();
	    try {
		logger.log(Level.WARNING, Tag.PRODUCT + ": " + DenySqlInjectionManagerAsync.class.getSimpleName()
			+ " Unable to verify SQL injection in async mode: " + exception.toString());
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
