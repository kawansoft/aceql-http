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
package org.kawanfw.sql.api.util.firewall.cloudmersive;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.firewall.DenySqlInjectionManagerAsync;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.api.util.firewall.SqlFirewallTriggerWrapper;
import org.kawanfw.sql.servlet.injection.classes.InjectedClassesStore;
import org.kawanfw.sql.util.FrameworkDebug;

import com.cloudmersive.client.invoker.ApiCallback;
import com.cloudmersive.client.invoker.ApiException;
import com.cloudmersive.client.model.SqlInjectionDetectionResult;

/**
 * Manages callback for {@code DenySqlInjectionManagerAsync}. Will execute all
 * the {@code SqlFirewallTrigger} implementations defined in the
 * {@code aceql-server.properties} file.
 * 
 * @see DenySqlInjectionManagerAsync
 * @author Nicolas de Pomereu
 * @since 11.0
 */
public class SqlInjectionApiCallback implements ApiCallback<SqlInjectionDetectionResult> {

    private static boolean DEBUG = FrameworkDebug.isSet(SqlInjectionApiCallback.class);

    /**
     * The elements that called the Cloudmersive
     * {@code TextInputApi.textInputCheckSqlInjectionAsync} call
     **/
    private SqlEvent sqlEvent;
    private SqlFirewallManager sqlFirewallManager;

    /**
     * Constructor
     * 
     * @param sqlEvent           the SQL event asked by the client side. Contains
     *                           all info about the SQL call (client username,
     *                           database name, IP Address of the client, and SQL
     *                           statement details).
     * @param sqlFirewallManager the instance that triggers this call.
     */
    public SqlInjectionApiCallback(SqlEvent sqlEvent, SqlFirewallManager sqlFirewallManager) {
	this.sqlEvent = Objects.requireNonNull(sqlEvent, "sqlEvent cannot ne null!");
	this.sqlFirewallManager = Objects.requireNonNull(sqlFirewallManager, "sqlFirewallManager cannot ne null!");
    }

    /**
     * In case of Cloudmersive API failure, we will just display on stderr the
     * {@code SqlEvent}, the {@code sqlFirewallManager} class name and the
     * {@code ApiException} stack trace.
     */
    @Override
    public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
	System.err.println();
	System.err.println("Cloudmersive failure on DenySqlInjectionManagerAsync defered execution: ");
	System.err.println("sqlEvent           : " + sqlEvent.toString());
	System.err.println("sqlFirewallManager : " + sqlFirewallManager.getClass().getName());
	e.printStackTrace();

    }

    /**
     * Will extract a new {@code Connection} from the database and the process all
     * {@code SqlFirewallTrigger} defined in the {@code aceql-server.properties} file. <br>
     * The {@code Connection} will be cleanly closed in a {@code finally} block.
     */
    @Override
    public void onSuccess(SqlInjectionDetectionResult result, int statusCode,
	    Map<String, List<String>> responseHeaders) {

	long begin = System.currentTimeMillis();
	debug("onSucces: result.isContainedSqlInjectionAttack():" + result.isContainedSqlInjectionAttack());

	// Exit if not a SQL Injection attack
	if (!result.isContainedSqlInjectionAttack()) {
	    return;
	}

	debug("Loading DatabaseConfigurator...");
	String database = sqlEvent.getDatabase();
	DatabaseConfigurator databaseConfigurator = InjectedClassesStore.get().getDatabaseConfigurators().get(database);

	Connection connection = null;
	try {
	    debug("Connection creation...");
	    connection = databaseConfigurator.getConnection(database);
	    debug("Running SqlFirewallTriggers runIfStatementRefused: " + sqlEvent + "  "
		    + sqlFirewallManager.getClass().getSimpleName());
	    SqlFirewallTriggerWrapper.runIfStatementRefused(sqlEvent, sqlFirewallManager, connection);
	    long end = System.currentTimeMillis();
	    debug("Running SqlFirewallTriggers done! (" + (end-begin) + "ms.)");
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    try {
		if (connection != null) {
		    databaseConfigurator.close(connection);
		}
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}

    }

    /**
     * Not used for our SQL injection detection process
     */
    @Override
    public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {
	// Ignore. Not related to SqlInjectionDetectionResult
    }

    /**
     * Not used for our SQL injection detection process
     */
    @Override
    public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {
	// Ignore. Not related to SqlInjectionDetectionResult
    }

    private void debug(String string) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + this.getClass().getSimpleName() + " " + string);
	}
    }
}
