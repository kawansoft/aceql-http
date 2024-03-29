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
package org.kawanfw.sql.api.server.session;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileStore;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileUtil;
import org.kawanfw.sql.util.Tag;

/**
 * Default implementation of session management:
 * <ul>
 * <li>Session id are generated using a {@code SecureRandom} with the
 * {@link SessionIdentifierGenerator} class.</li>
 * <li>Session info (username, database) and session date/time creation are
 * stored in a {@code HashMap} whose key is the session id.</li>
 * <li>Session id is sent by client side at each API call. AceQL verifies that
 * the {@code HashMap} contains the username and that the session is not expired
 * to grant access to the APIexecution.</li>
 * </ul>
 * <p>
 * It is not required nor recommended to extend this class or to develop another
 * {@code SessionConfigurator} implementation. <br>
 * Do it if you want to implement you own session mechanism and/or want to
 * manage how session info are stored.<br>
 * <br>
 * Note that {@code getSessionTimelife()} returns 0 and that sessions never
 * expires. <br>
 * Extend this class and override {@code getSessionTimelife()} if you want to
 * define expirable sessions.
 *
 * @author Nicolas de Pomereu
 */
public class DefaultSessionConfigurator implements SessionConfigurator {

    private Properties properties = null;

    private SessionIdentifierGenerator sessionIdentifierGenerator = new SessionIdentifierGenerator();
    private Map<String, SessionInfo> sessionInfoStore = new ConcurrentHashMap<>();

    /*
     * (non-Javadoc)
     *
     * @see org.kawanfw.sql.api.server.session.SessionConfigurator#
     * generateSessionId()
     */
    /**
     * The method builds an authentication session id by a call to: <br>
     * {@link SessionIdentifierGenerator}
     */
    @Override
    public String generateSessionId(String username, String database) {
	String sessionId = sessionIdentifierGenerator.nextSessionId();
	SessionInfo sessionInfo = new SessionInfo(sessionId, username, database);
	sessionInfoStore.put(sessionId, sessionInfo);

	return sessionId;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kawanfw.sql.api.server.session.SessionConfigurator#getUsername
     * (java.lang.String)
     */
    @Override
    public String getUsername(String sessionId) {
	SessionInfo sessionInfo = sessionInfoStore.get(sessionId);

	if (sessionInfo == null) {
	    return null;
	}

	return sessionInfo.getUsername();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kawanfw.sql.api.server.session.SessionConfigurator#getDatabase
     * (java.lang.String)
     */
    @Override
    public String getDatabase(String sessionId) {
	SessionInfo sessionInfo = sessionInfoStore.get(sessionId);

	if (sessionInfo == null) {
	    return null;
	}

	return sessionInfo.getDatabase();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kawanfw.sql.api.server.session.SessionConfigurator#remove(
     * java.lang.String)
     */
    @Override
    public void remove(String sessionId) {
	sessionInfoStore.remove(sessionId);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kawanfw.sql.api.server.session.SessionConfigurator#verifySessionId
     * (java.lang.String)
     */
    /**
     * This implementation:
     * <ul>
     * <li>Verify that the sessionId exists</li>
     * <li>Verify that the sessionId is not expired (must be less that 12
     * hours).</li>
     * </ul>
     *
     * @throws IOException if any I/O error occurs
     */
    @Override
    public boolean verifySessionId(String sessionId) throws IOException {
	SessionInfo sessionInfo = sessionInfoStore.get(sessionId);

	if (sessionInfo == null) {
	    // System.err.println("sessionInfo is null!");
	    return false;
	}

	if (getSessionTimelifeMinutes() == 0) {
	    return true;
	}

	// Check if session is expired.
	return new Date().getTime() - sessionInfo.getCreationTimeMillis() <= getSessionTimelifeMinutes() * 60 * 1000;

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.kawanfw.sql.api.server.session.SessionConfigurator#getSessionTimelife
     * (java.lang.String)
     */
    /**
     * Returns the value of {@code session.timelifeMinutes} property of {@code aceql-server.properties}.
     * Defaults to 0. If 0, session is infinite.
     * @throws IOException if any I/O error occurs
     */
    @Override
    public int getSessionTimelifeMinutes() throws IOException {

	if (properties == null) {
	    File file = PropertiesFileStore.get();
	    properties = PropertiesFileUtil.getProperties(file);
	}

	return getSessionTimelifeMinutesPropertyValue(properties);
    }

    /**
     * Returns the session.TimelifeMinutes property value defined in the
     * {@code aceql-server.properties} configuration file.
     *
     * @return the session.TimelifeMinutes property value defined in the
     * {@code aceql-server.properties} configuration file.
     * @throws IOException
     * @throws IOException
     */
    static int getSessionTimelifeMinutesPropertyValue(Properties properties)
	    throws IOException {
	Objects.requireNonNull(properties, Tag.PRODUCT +  " properties cannot be null!");

	String sessionTimelifeInMinutesStr = properties.getProperty("session.timelifeMinutes");
	if (sessionTimelifeInMinutesStr == null) {
	    return 0;
	}

	if (!StringUtils.isNumeric(sessionTimelifeInMinutesStr)) {
	    throw new IllegalArgumentException(Tag.PRODUCT + " The session.timelifeMinutes property is not numeric: "
		    + sessionTimelifeInMinutesStr);
	}

	int sessionTimelifeMinutes = Integer.parseInt(sessionTimelifeInMinutesStr);
	return sessionTimelifeMinutes;
    }

}
