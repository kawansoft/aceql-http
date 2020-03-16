/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2020,  KawanSoft SAS
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

package org.kawanfw.sql.api.server.auth;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonStructure;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.api.server.DefaultDatabaseConfigurator;
import org.kawanfw.sql.api.server.util.SimpleHttpClient;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.tomcat.TomcatStarterUtil;
import org.kawanfw.sql.util.Tag;

/**
 * A concrete {@code UserAuthenticator} that extends allows zero-code remote
 * client {@code (username, password)} authentication against a Web Service.
 *
 * @author Nicolas de Pomereu
 * @since 5.0
 */

public class WebServiceUserAuthenticator implements UserAuthenticator {

    private Properties properties = null;
    private Logger logger = null;

    /**
     * Constructor. {@code UserAuthenticator} implementation must have no
     * constructor or a unique no parameters constructor.
     */
    public WebServiceUserAuthenticator() {

    }

    /**
     * @return <code>true</code> if the Authentication Web Service defined in
     *         {@code aceql-server.properties} returns the JSON String
     *         <code>{"status"="OK"}</code>, else <code>false</code> .
     */
    @Override
    public boolean login(String username, char[] password, String database, String ipAddress)
	    throws IOException, SQLException {

	if (properties == null) {
	    File file = ServerSqlManager.getAceqlServerProperties();
	    properties = TomcatStarterUtil.getProperties(file);
	}

	String url = properties.getProperty("webServiceUserAuthenticator.url");
	String timeoutSecondsStr = properties.getProperty("webServiceUserAuthenticator.timeoutSeconds");
	String httpTraceStr = properties.getProperty("webServiceUserAuthenticator.httpTrace");

	// Accept free login if no Web Service URL defined or is localhost
	if (url == null || url.contentEquals("localhost")) {
	    return true;
	}

	if (timeoutSecondsStr == null) {
	    timeoutSecondsStr = "0";
	}

	if (!StringUtils.isNumeric(timeoutSecondsStr)) {
	    throw new IllegalArgumentException(
		    "The default.login.webService.timeoutSeconds property is not numeric: " + timeoutSecondsStr);
	}

	int timeoutSeconds = Integer.parseInt(timeoutSecondsStr);
	int connectTimeout = timeoutSeconds * 1000;
	int readTimeout = timeoutSeconds * 1000;

	SimpleHttpClient simpleHttpClient = new SimpleHttpClient(connectTimeout, readTimeout);
	if (Boolean.parseBoolean(httpTraceStr)) {
	    SimpleHttpClient.TRACE_ON = true;
	}
	else {
	    SimpleHttpClient.TRACE_ON = false;
	}

	String jsonResult = null;
	Map<String, String> parametersMap = new HashMap<>();
	parametersMap.put("username", username);
	parametersMap.put("password", new String(password));

	try {
	    jsonResult = simpleHttpClient.callWithPost(new URL(url), parametersMap);
	} catch (Exception e) {
	    if (logger == null) {
		logger = new DefaultDatabaseConfigurator().getLogger();
	    }
	    logger.log(Level.SEVERE, Tag.PRODUCT + "Username " + username
		    + " can not authenticate. Error when calling SimpleHttpClient: " + e.getMessage());
	    return false;
	}

	if (jsonResult == null) {
	    return false;
	}

	jsonResult = jsonResult.trim();

	try {
	    JsonReader reader = Json.createReader(new StringReader(jsonResult));
	    JsonStructure jsonst = reader.read();

	    JsonObject object = (JsonObject) jsonst;
	    JsonString status = (JsonString) object.get("status");

	    if (status != null && status.getString().equals("OK")) {
		return true;
	    } else {
		return false;
	    }
	} catch (Exception e) {
	    if (logger == null) {
		logger = new DefaultDatabaseConfigurator().getLogger();
	    }
	    logger.log(Level.SEVERE, Tag.PRODUCT + " Error when parsing jsonResult of Authentication Web Service: " + e.getMessage());
	    return false;
	}

    }

}
