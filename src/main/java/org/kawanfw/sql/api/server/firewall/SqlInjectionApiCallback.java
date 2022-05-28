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
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.util.firewall.SqlFirewallTriggerWrapper;
import org.kawanfw.sql.servlet.injection.classes.InjectedClassesStore;

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

    @Override
    public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
	// We don't really care..
	System.err.println("Failure on DenySqlInjectionManagerAsync defered execution: ");
	System.err.println("sqlFirewallManager : " + sqlFirewallManager.getClass().getName());
	e.printStackTrace();

    }

    @Override
    public void onSuccess(SqlInjectionDetectionResult result, int statusCode,
	    Map<String, List<String>> responseHeaders) {

	// Exit if not a SQL Injection attack
	if (!result.isContainedSqlInjectionAttack()) {
	    return;
	}

	String database = sqlEvent.getDatabase();
	DatabaseConfigurator databaseConfigurator = InjectedClassesStore.get().getDatabaseConfigurators().get(database);

	Connection connection = null;
	try {
	    connection = databaseConfigurator.getConnection(database);
	    SqlFirewallTriggerWrapper.runIfStatementRefused(sqlEvent, sqlFirewallManager, connection);
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

    @Override
    public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {
	// Ignore. Not related to SqlInjectionDetectionResult
    }

    @Override
    public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {
	// Ignore. Not related to SqlInjectionDetectionResult
    }

}
