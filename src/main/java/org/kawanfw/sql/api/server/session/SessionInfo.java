/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.api.server.session;

import java.util.Date;
import java.util.Objects;

/**
 *
 * Utility holder class for session info.
 *
 * @author Nicolas de Pomereu
 */
public class SessionInfo {

    private String sessionId = null;
    private String username = null;
    private String database = null;
    private long creationTime;

    /**
     * Constructor
     *
     * @param sessionId the unique session id that is generated after login by
     *                  {@link SessionConfigurator#generateSessionId(String, String)}
     * @param username  the logged client username
     * @param database  the database to use for this session
     */
    public SessionInfo(String sessionId, String username, String database) {

	Objects.requireNonNull(sessionId, "file cannot be null!");
	Objects.requireNonNull(username, "username cannot be null!");
	Objects.requireNonNull(database, "database cannot be null!");

	this.sessionId = sessionId;
	this.username = username;
	this.database = database;

	this.creationTime = new Date().getTime();
    }

    /**
     * Returns the session id
     *
     * @return the session id
     */
    public String getSessionId() {
	return sessionId;
    }

    /**
     * Returns the client username
     *
     * @return the client username
     */
    public String getUsername() {
	return username;
    }

    /**
     * Returns the database in use for this session
     *
     * @return the database in use for this session
     */
    public String getDatabase() {
	return database;
    }

    /**
     * Returns the date/time in milliseconds when this {@code SessionInfo} instance was created
     * @return the date/time in milliseconds when this {@code SessionInfo} instance was created
     */
    public long getCreationTimeMillis() {
	return creationTime;
    }

}
