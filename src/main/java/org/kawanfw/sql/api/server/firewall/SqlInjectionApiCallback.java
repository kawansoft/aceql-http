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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.SqlEvent;
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
     * @param sqlFirewallManager the instance that that triggers this call.
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
     * {@code SqlFirewallTrigger} defined in the {@code aceql.properties} file. <br>
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
