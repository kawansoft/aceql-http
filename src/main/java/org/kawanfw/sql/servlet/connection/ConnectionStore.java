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
package org.kawanfw.sql.servlet.connection;

import java.sql.Array;
import java.sql.Connection;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.kawanfw.sql.api.server.connectionstore.ConnectionKey;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 *
 * Stores the Connection in static for subsequent new calls by remote device/PC
 * clients.
 *
 * @author Nicolas de Pomereu
 */

public class ConnectionStore {

    private static boolean DEBUG = FrameworkDebug.isSet(ConnectionStore.class);

    /**
     * The connection store key composed of client username and client connection id
     */
    private ConnectionKey connectionKey = null;

    /** Map of (username + sessionId + connectionId), connection= */
    private static Map<ConnectionKey, Connection> connectionMap = new ConcurrentHashMap<>();

    /** The map of Savepoints */
    private static Map<ConnectionKey, Set<Savepoint>> savepointMap = new ConcurrentHashMap<>();

    /** The map of Arrays */
    private static Map<ConnectionKey, Set<Array>> arrayMap = new ConcurrentHashMap<>();

    /** The map of RowIds */
    private static Map<ConnectionKey, Set<RowId>> rowIdMap = new ConcurrentHashMap<>();

    /**
     * Constructor
     *
     * @param username
     * @param sessionId
     * @param connectionId
     */
    public ConnectionStore(String username, String sessionId, String connectionId) {	
	if (username == null) {
	    throw new IllegalArgumentException("username is null!");
	}

	if (sessionId == null) {
	    throw new IllegalArgumentException("sessionId is null!");
	}

	//8.0: Connections Id is still not mandatory!
	// Because of legacy with API without languages.
	
	this.connectionKey = new ConnectionKey(username, sessionId, connectionId);

    }

    /**
     * Stores the Connection in static for username + connectionId
     *
     * @param connection the Connection to store
     */
    public void put(Connection connection) {

	debug("Creating a Connection for user: " + connectionKey);
	if (connection == null) {
	    throw new IllegalArgumentException("connection is null!");
	}

	connectionMap.put(connectionKey, connection);
    }

    /**
     * Stores the Savepoint in static for username + connectionId
     *
     * @param savepoint the Savepoint to store
     */
    public void put(Savepoint savepoint) {

	debug("Creating a Savepoint for user: " + connectionKey);
	if (savepoint == null) {
	    throw new IllegalArgumentException("savepoint is null!");
	}

	Set<Savepoint> savepointSet = savepointMap.get(connectionKey);
	if (savepointSet == null) {
	    savepointSet = new LinkedHashSet<Savepoint>();
	}

	savepointSet.add(savepoint);
	savepointMap.put(connectionKey, savepointSet);
    }

    /**
     * Returns the Savepoint associated to username + connectionId and Savepoint ID
     *
     * @param savepointId the ID of the Savepoint
     *
     * @return the Savepoint associated to username + connectionId and Savepoint ID
     */
    public Savepoint getSavepoint(int savepointId) {
	Set<Savepoint> savepointSet = savepointMap.get(connectionKey);

	for (Iterator<Savepoint> iterator = savepointSet.iterator(); iterator.hasNext();) {
	    Savepoint savepoint = iterator.next();

	    try {
		if (savepoint.getSavepointId() == savepointId) {
		    return savepoint;
		}
	    } catch (Throwable e) {
		// Ignore. We don't care: it's a named Savepoint
	    }
	}

	return null;
    }
    
    /**
     * Returns the Savepoint associated to username + connectionId and Savepoint Name
     *
     * @param savepointName the ID of the Savepoint
     *
     * @return the Savepoint associated to username + connectionId and Savepoint Name
     */
    public Savepoint getSavepoint(String savepointName) {
	Set<Savepoint> savepointSet = savepointMap.get(connectionKey);

	for (Iterator<Savepoint> iterator = savepointSet.iterator(); iterator.hasNext();) {
	    Savepoint savepoint = iterator.next();

	    try {
		if (savepoint.getSavepointName().equals(savepointName)) {
		    return savepoint;
		}
	    } catch (Throwable e) {
		// Ignore. We don't care: it's a named Savepoint
	    }
	}

	return null;
    }

    /**
     * Remove the Savepoint associated to username + connectionId and savepointInfo
     *
     * @param a Savepoint that is just a container with the info to find the real
     *          one
     *
     */
    public void remove(Savepoint savepointInfo) {
	Set<Savepoint> savepointSet = savepointMap.get(connectionKey);

	for (Iterator<Savepoint> iterator = savepointSet.iterator(); iterator.hasNext();) {
	    Savepoint savepoint = iterator.next();

	    try {
		if (savepoint.getSavepointId() == savepointInfo.getSavepointId()) {
		    savepointSet.remove(savepoint);
		}
	    } catch (SQLException e) {
		// We don't care: it's a named Savepoint
	    }

	    try {
		if (savepoint.getSavepointName().equals(savepointInfo.getSavepointName())) {
		    savepointSet.remove(savepoint);
		}
	    } catch (SQLException e) {
		// We don't care: it's a unnamed Savepoint
	    }
	}

    }

    /**
     * Stores the Array in static for username + connectionId
     *
     * @param array the Array to store
     */
    public void put(Array array) {

	debug("Creating an array for user: " + connectionKey);
	if (array == null) {
	    throw new IllegalArgumentException("array is null!");
	}

	Set<Array> arraySet = arrayMap.get(connectionKey);
	if (arraySet == null) {
	    arraySet = new LinkedHashSet<Array>();
	}

	arraySet.add(array);
	arrayMap.put(connectionKey, arraySet);

    }

    /**
     * Returns the Array associated to username + connectionId and savepointInfo
     *
     * @param arrayId the array id (it's haschode())
     *
     * @return the Array associated to username + connectionId and arrayId
     */
    public Array getArray(int arrayId) {
	Set<Array> arraySet = arrayMap.get(connectionKey);

	for (Iterator<Array> iterator = arraySet.iterator(); iterator.hasNext();) {
	    Array array = iterator.next();

	    if (array.hashCode() == arrayId) {
		return array;
	    }
	}

	return null;
    }


    /**
     * Stores the RowId in static for username + connectionId
     *
     * @param rowId the RowId to store
     */
    public void put(RowId rowId) {

	debug("Creating a rowId for user: " + connectionKey);
	if (rowId == null) {
	    throw new IllegalArgumentException("rowId is null!");
	}

	Set<RowId> rowIdSet = rowIdMap.get(connectionKey);
	if (rowIdSet == null) {
	    rowIdSet = new LinkedHashSet<RowId>();
	}

	rowIdSet.add(rowId);
	rowIdMap.put(connectionKey, rowIdSet);
    }

    /**
     * Returns the RowId associated to username + connectionId and hashCode
     *
     * @param rowIdHashCode the RowId id (it's haschode())
     *
     * @return the Array associated to username + connectionId and arrayId
     */
    public RowId getRowId(int rowIdHashCode) {
	Set<RowId> rowIdSet = rowIdMap.get(connectionKey);

	for (Iterator<RowId> iterator = rowIdSet.iterator(); iterator.hasNext();) {
	    RowId rowId = iterator.next();

	    if (rowId.hashCode() == rowIdHashCode) {
		return rowId;
	    }
	}

	return null;
    }


    /**
     * Returns the Connection associated to username + connectionId
     *
     * @return the Connection associated to username + connectionId
     */
    public Connection get() {
	return connectionMap.get(connectionKey);
    }
    
    /**
     * Returns the full Map of Connections associated to username + connectionId
     * @return the connectionMap
     */
    public static Map<ConnectionKey, Connection> getConnectionMap() {
        return connectionMap;
    }


    /**
     * Remove all stored instances in the ConnectionStore. This must be done only in
     * a Logout stage ({@code Connection#close()}).
     */
    public void remove() {
	debug("Removing a Connection for user: " + connectionKey);
	connectionMap.remove(connectionKey);
	savepointMap.remove(connectionKey);
	arrayMap.remove(connectionKey);
	rowIdMap.remove(connectionKey);
    }

    /**
     * Static remove. To be used by an JdbcInterceptorTest.
     * @param connectionKey
     */
    public static void remove(ConnectionKey connectionKey) {
	connectionMap.remove(connectionKey);
	savepointMap.remove(connectionKey);
	arrayMap.remove(connectionKey);
	rowIdMap.remove(connectionKey);
    }
    
    /**
     * Returns the size of the Connection Store
     *
     * @return the size of the Connection Store
     */
    public int size() {
	return connectionMap.size();
    }

    /**
     * Returns the keys of the store
     *
     * @return the keys of the store
     */
    public static Set<ConnectionKey> getKeys() {
	return connectionMap.keySet();
    }

    public static Set<Connection> getAllConnections(String username, String sessionId) {

	Set<Connection> connections = new HashSet<>();

	for (ConnectionKey connectionKey : connectionMap.keySet()) {
	    if (connectionKey.getUsername().equals(username) && connectionKey.getSessionId().equals(sessionId)) {
		Connection connection = connectionMap.get(connectionKey);
		connections.add(connection);
	    }
	}

	return connections;

    }

    /**
     * Returns the first available Connection of all Connections for
     * couple(username, sessionId)
     *
     * @return the first available Connection of all Connections for
     *         couple(username, sessionId)
     */
    public Connection getFirst() throws SQLException {
	Set<Connection> connections = getAllConnections(this.connectionKey.getUsername(),
		this.connectionKey.getSessionId());
	if (connections.isEmpty()) {
	    throw new SQLException("No Connection stored for (" + this.connectionKey.getUsername() + ", "
		    + this.connectionKey.getSessionId() + ")");
	}
	List<Connection> connectionsList = new ArrayList<>();
	connectionsList.addAll(connections);
	return connectionsList.get(0);
    }

    public static void removeAll(String username, String sessionId) {

	// No!! Will trigger a ConcurrentModificationException!
	/**
	<pre><code>
	for (ConnectionKey connectionKey : connectionMap.keySet()) {
	    if (connectionKey.getUsername().equals(username) && connectionKey.getSessionId().equals(sessionId)) {
		connectionMap.remove(connectionKey);
	    }
	}
	</code></pre>
	*/
	// Intermediate Collection to avoid ConcurrentModificationException on Map

	Set<ConnectionKey> connectionsKeys = new HashSet<>(connectionMap.keySet());

	for (ConnectionKey connectionKey : connectionsKeys) {
	    if (connectionKey.getUsername().equals(username) && connectionKey.getSessionId().equals(sessionId)) {
		connectionMap.remove(connectionKey);
	    }
	}
    }

    /**
     * Method called by children Servlet for debug purpose Println is done only if
     * class name name is in kawansoft-debug.ini
     */
    public static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }



}
