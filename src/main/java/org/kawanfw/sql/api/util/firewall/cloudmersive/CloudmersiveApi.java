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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileUtil;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.SqlTag;

import com.cloudmersive.client.TextInputApi;
import com.cloudmersive.client.invoker.ApiCallback;
import com.cloudmersive.client.invoker.ApiClient;
import com.cloudmersive.client.invoker.ApiException;
import com.cloudmersive.client.invoker.Configuration;
import com.cloudmersive.client.invoker.auth.ApiKeyAuth;
import com.cloudmersive.client.model.SqlInjectionDetectionResult;

/**
 * Cloudmersive API wrapper for SQL injection detection. <br>
 * <br>
 * Usage requires a free creation account at
 * https://account.cloudmersive.com/signup
 * 
 * @author Nicolas de Pomereu
 *
 */
public class CloudmersiveApi {

    private static boolean DEBUG = FrameworkDebug.isSet(CloudmersiveApi.class);

    private static final int FIVE_MINUTES_IN_MILLISECONDS = 5 * 60 * 1000;

    private TextInputApi apiInstance;
    private String detectionLevel;
    private long snapshot;

    private File file;

    /**
     * Constructor
     * 
     * @param file the cloudmersive.properties files
     * @throws FileNotFoundException
     */
    public CloudmersiveApi(File file) throws FileNotFoundException {
	this.file = Objects.requireNonNull(file, "file cannot be null!");
	if (!file.exists()) {
	    throw new FileNotFoundException("The Cloudmersive elements file does not exist: " + file);
	}
	this.snapshot = 0;
    }

    /**
     * Connect to Cloudmersive using elements stored in a properties file
     * 
     * @throws IOException if any I/O Exception occurs
     */
    private void connect() throws IOException {
	
	long begin = System.currentTimeMillis();
	debug("Begin Connect...");
	Properties properties = PropertiesFileUtil.getProperties(file);
	String apiKey = (String) properties.get("apiKey");

	if (apiKey == null || apiKey.isEmpty()) {
	    throw new IllegalArgumentException(
		    SqlTag.USER_CONFIGURATION + " apiKey property not found in file: " + file);
	}

	String apiKeyPrefix = (String) properties.get("apiKeyPrefix");
	detectionLevel = (String) properties.get("detectionLevel");

	if (detectionLevel == null || detectionLevel.isEmpty()) {
	    detectionLevel = "Normal";
	}

	if (!detectionLevel.equals("High") && !detectionLevel.equals("Normal")) {
	    throw new IllegalArgumentException(SqlTag.USER_CONFIGURATION
		    + " detectionLevel can be \"Normal\" or \"High\" only. Is: " + detectionLevel);
	}

	ApiClient defaultClient = Configuration.getDefaultApiClient();
	// Configure API key authorization: Apikey
	ApiKeyAuth Apikey = (ApiKeyAuth) defaultClient.getAuthentication("Apikey");
	Apikey.setApiKey(apiKey);

	if (apiKeyPrefix != null && !apiKeyPrefix.isEmpty()) {
	    Apikey.setApiKeyPrefix(apiKeyPrefix);
	}

	apiInstance = new TextInputApi();
	snapshot = new Date().getTime();
	
	long end = System.currentTimeMillis();
	debug("End Connect. " + (end-begin));
    }

    /**
     * Detects if the passed SQL statement contains a SQL injection attack.
     * 
     * @param sql the SQL statement to analyze
     * @return true if the passed SQL statement contains a SQL injection attack,
     *         else false
     * @throws SQLException if any error occurs. (Wraps the {@link ApiException})
     * @throws IOException
     */
    public boolean sqlInjectionDetect(String sql) throws SQLException, IOException {
	Objects.requireNonNull(sql, "sql cannot ne null!");
	
	long now = new Date().getTime();

	if (now - snapshot > FIVE_MINUTES_IN_MILLISECONDS) {
	    debug("Reloading with connect()!");
	    connect();
	}

	try {
	    debug("Detecting SQLI in sql: " + sql);
	    long begin = System.currentTimeMillis();
	    SqlInjectionDetectionResult sqlInjectionDetectionResult = apiInstance.textInputCheckSqlInjection(sql, detectionLevel);
	    boolean attack =  sqlInjectionDetectionResult.isContainedSqlInjectionAttack();
	    debug("attack: " + attack + " Detection time: " + (begin-System.currentTimeMillis()));
	    return attack;
	} catch (ApiException apiException) {
	    connect();
	    throw new SQLException(apiException);
	}
    }

    
    public void sqlInjectionDetectAsync(SqlEvent sqlEvent, SqlFirewallManager sqlFirewallManager)
	    throws IOException, SQLException {
	
	Objects.requireNonNull(sqlEvent, "sqlEvent cannot ne null!");
	Objects.requireNonNull(sqlFirewallManager, "sqlFirewallManager cannot ne null!");
	
	long now = new Date().getTime();

	if (now - snapshot > FIVE_MINUTES_IN_MILLISECONDS) {
	    debug("Reloading with connect()!");
	    connect();
	}

	try {
	    debug("Detecting Async SQLI in sql: " + sqlEvent.getSql());
	    ApiCallback<SqlInjectionDetectionResult> sqlInjectionApiCallback = new SqlInjectionApiCallback(sqlEvent,
		    sqlFirewallManager);
	    apiInstance.textInputCheckSqlInjectionAsync(sqlEvent.getSql(), detectionLevel, sqlInjectionApiCallback);
	} catch (ApiException apiException) {
	    connect();
	    throw new SQLException(apiException);
	}

    }

    private void debug(String string) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + this.getClass().getSimpleName() + " " + string);
	}
    }
    
}
