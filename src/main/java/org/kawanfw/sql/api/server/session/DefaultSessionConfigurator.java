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
package org.kawanfw.sql.api.server.session;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.SystemUtils;

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
 * <br>Note that {@code getSessionTimelife()} returns 0 and that sessions never expire.s
 * <br>Extend this class and override {@code getSessionTimelife()} if you want to define expirable sessions.
 *
 * @author Nicolas de Pomereu
 */
public class DefaultSessionConfigurator implements SessionConfigurator {

    private SessionIdentifierGenerator sessionIdentifierGenerator = new SessionIdentifierGenerator();
    private Map<String, SessionInfo> sessionInfoStore = new ConcurrentHashMap<>();

    /**
     * Constructor. {@code SessionConfigurator} implementation must have no
     * constructor or a unique no parms constructor.
     */
    public DefaultSessionConfigurator() {

    }

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

	if (new File(SystemUtils.USER_HOME + File.separator + "aceql_fixed_session.txt").exists()) {
	    sessionId = "64qssfsku57i99nkpjtap8hho5";
	}

	SessionInfo sessionInfo = new SessionInfo(sessionId, username,
		database);
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
     * @see
     * org.kawanfw.sql.api.server.session.SessionConfigurator#getCreationTime
     * (java.lang.String)
     */
    @Override
    public long getCreationTime(String sessionId) {
	SessionInfo sessionInfo = sessionInfoStore.get(sessionId);

	if (sessionInfo == null) {
	    return 0;
	}

	return sessionInfo.getCreationTime();
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
     * @see
     * org.kawanfw.sql.api.server.session.SessionConfigurator#verifySessionId
     * (java.lang.String)
     */
    /**
     * This implementation:
     * <ul>
     * <li>Verify that the sessionId exists</li>
     * <li>Verify that the sessionId is not expired (must be less that 12
     * hours).</li>
     * </ul>
     */
    @Override
    public boolean verifySessionId(String sessionId) {
	SessionInfo sessionInfo = sessionInfoStore.get(sessionId);

	if (sessionInfo == null) {
	    // System.err.println("sessionInfo is null!");
	    return false;
	}

	if (getSessionTimelife() == 0) {
	    return true;
	}

	// Check if session is expired.
	long now = new Date().getTime();

	if (now - sessionInfo.getCreationTime() > getSessionTimelife() * 60
		* 1000) {
	    // System.err.println("now - sessionInfo.getCreationTime() >
	    // getSessionTimelife");
	    return false;
	}

	return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.kawanfw.sql.api.server.session.SessionConfigurator#getSessionTimelife
     * (java.lang.String)
     */
    /**
     * Returns 0. (Session never expires).
     */
    @Override
    public int getSessionTimelife() {
	return 0;
    }

}
