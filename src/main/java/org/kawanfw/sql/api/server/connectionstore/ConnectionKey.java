/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.api.server.connectionstore;
/**
 * Defines a key for the Connection Store that keeps connections in memory.
 * The connections are identified by the client username and an
 * unique generated connection Id in order to identify different connections
 * belonging to the same username.
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */

public class ConnectionKey {

    /** The client username */
    private String username = null;

    /**
     * The client Session Id which is unique per (username, database)
     */
    private String sessionId = null;

    /**
     * The client  Connection Id which is unique per Connection 
     */
    private String connectionId;

    /**
     * Constructor
     * 
     * @param username
     *            the client username
     * @param sessionId
     *            the unique Id per Session 
     * @param connectionId 
     * 		  the unique Connection Id per Connection
     */
    public ConnectionKey(String username, String sessionId, String connectionId) {

	this.username = username;
	this.sessionId = sessionId;
	this.connectionId = connectionId;
	
    }

    /**
     * Returns the client username corresponding to this ConnectionKey.
     * 
     * @return the client username corresponding to this ConnectionKey
     */
    public String getUsername() {
	return username;
    }

    /**
     * Returns the unique session Id corresponding to this ConnectionKey.
     * 
     * @return the client unique session Id corresponding to this
     *         ConnectionKey
     */
    public String getSessionId() {
	return sessionId;
    }

    /**
     * Returns the unique connection Id corresponding to this ConnectionKey.
     * 
     * @return the client unique connection Id corresponding to this
     *         ConnectionKey
     */
    public String getConnectionId() {
        return connectionId;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((connectionId == null) ? 0 : connectionId.hashCode());
	result = prime * result
		+ ((sessionId == null) ? 0 : sessionId.hashCode());
	result = prime * result
		+ ((username == null) ? 0 : username.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	ConnectionKey other = (ConnectionKey) obj;
	if (connectionId == null) {
	    if (other.connectionId != null)
		return false;
	} else if (!connectionId.equals(other.connectionId))
	    return false;
	if (sessionId == null) {
	    if (other.sessionId != null)
		return false;
	} else if (!sessionId.equals(other.sessionId))
	    return false;
	if (username == null) {
	    if (other.username != null)
		return false;
	} else if (!username.equals(other.username))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "ConnectionKey [username=" + username + ", sessionId="
		+ sessionId + ", connectionId=" + connectionId + "]";
    }



}
