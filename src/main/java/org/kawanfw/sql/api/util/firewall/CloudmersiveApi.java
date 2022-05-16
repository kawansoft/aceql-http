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
package org.kawanfw.sql.api.util.firewall;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

import org.kawanfw.sql.servlet.injection.properties.PropertiesFileUtil;

import com.cloudmersive.client.TextInputApi;
import com.cloudmersive.client.invoker.ApiClient;
import com.cloudmersive.client.invoker.ApiException;
import com.cloudmersive.client.invoker.Configuration;
import com.cloudmersive.client.invoker.auth.ApiKeyAuth;
import com.cloudmersive.client.model.SqlInjectionDetectionResult;

/**
 * Cloudmersive API wrapper for SQL injection detection.
 * <br><br>
 * Usage requires a free creation account at https://account.cloudmersive.com/signup
 * @author Nicolas de Pomereu
 *
 */
public class CloudmersiveApi {

    private TextInputApi apiInstance;
    private String detectionLevel;
    private SqlInjectionDetectionResult sqlInjectionDetectionResult = null;

    /**
     * Connect to Cloudmersive using elements stored in a properties file
     * @param file	the properties file that contain the Cloudmersive elements
     * @throws IOException if any I/O Exception occurs
     */
    public void connect(File file ) throws IOException {
	
	if (apiInstance != null ) {
	    return;
	}
	
	Objects.requireNonNull(file, "file cannot be null!");
	if (!file.exists()) {
	    throw new FileNotFoundException("The Cloudmersive elements file does not exist: " + file);
	}
	
	Properties properties = PropertiesFileUtil.getProperties(file);
	String apiKey = (String) properties.get("apiKey");
	
	if (apiKey == null || apiKey.isEmpty()) {
	    throw new IllegalArgumentException("apiKey property not found in file: " + file);
	}
	
	String apiKeyPrefix = (String) properties.get("apiKeyPrefix");
	detectionLevel = (String) properties.get("detectionLevel");
	
	if (detectionLevel == null || detectionLevel.isEmpty()) {
	    detectionLevel = "Normal";
	}
	
	if (! detectionLevel.equals("High") && ! detectionLevel.equals("Normal")) {
	    throw new IllegalArgumentException("detectionLevel can be \"Normal\" or \"High\" only. Is: " + detectionLevel);
	}
	
	ApiClient defaultClient = Configuration.getDefaultApiClient();
	// Configure API key authorization: Apikey
	ApiKeyAuth Apikey = (ApiKeyAuth) defaultClient.getAuthentication("Apikey");
	Apikey.setApiKey(apiKey);
	
	if (apiKeyPrefix != null && ! apiKeyPrefix.isEmpty()) {
	    Apikey.setApiKeyPrefix(apiKeyPrefix);
	}
	

	apiInstance = new TextInputApi();
	
    }

    
    /**
     * Detects if the passed SQL statement contains a SQL injection attack.
     * @param sql	the SQL statement to analyze
     * @return true if the passed SQL statement contains a SQL injection attack, else false
     * @throws SQLException if any erro occurs. (Wraps the {@link ApiException})
     */
    public boolean sqlInjectionDetect(String sql) throws SQLException {
	try {
	    sqlInjectionDetectionResult = apiInstance.textInputCheckSqlInjection(sql, detectionLevel);
	    return sqlInjectionDetectionResult.isSuccessful();
	} catch (ApiException e) {
	    e.printStackTrace();
	    throw new SQLException(e);
	}
    }

}
