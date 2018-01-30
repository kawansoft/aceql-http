/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP                                     
 * Copyright (C) 2017,  KawanSoft SAS
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
package org.kawanfw.sql.servlet.connection;

import java.sql.Array;
import java.sql.Connection;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.kawanfw.sql.api.server.connectionstore.ConnectionKey;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * 
 * Stores the Connection in autocommit mode false in static for subsequent
 * returns
 * 
 * @author Nicolas de Pomereu
 */

public class ConnectionStore {

    private static boolean DEBUG = FrameworkDebug.isSet(ConnectionStore.class);

    /**
     * The connection store key composed of client username and client
     * connection id
     */
    private ConnectionKey connectionKey = null;

    /** Map of (username + connectionId), connection= */
    private static Map<ConnectionKey, Connection> connectionMap = new HashMap<>();

    /** Timestamp to compute the age of a stored Connection */
    private static Map<ConnectionKey, Integer> connectionAge = new HashMap<>();

    /** The map of Savepoints */
    private static Map<ConnectionKey, Set<Savepoint>> savepointMap = new HashMap<>();

    /** The map of Arrays */
    private static Map<ConnectionKey, Set<Array>> arrayMap = new HashMap<>();

    /** The map of RowIds */
    private static Map<ConnectionKey, Set<RowId>> rowIdMap = new HashMap<>();

    /**
     * Constructor
     * 
     * @param username
     * @param connectionId
     */
    public ConnectionStore(String username, String connectionId) {

	if (username == null) {
	    throw new IllegalArgumentException("username is null!");
	}

	if (connectionId == null) {
	    throw new IllegalArgumentException("connectionId is null!");
	}

	this.connectionKey = new ConnectionKey(username, connectionId);

    }

    /**
     * Says if pair (Username, connectionId) is Stateless or Stateful It it
     * Stateful id the connectionMap contains en entry
     * 
     * @param username
     * @param connectionId
     * @return
     */
    public static boolean isStateless(String username, String connectionId) {
	ConnectionKey connectionKey = new ConnectionKey(username, connectionId);
	boolean isStateless = (connectionMap.containsKey(connectionKey)) ? false
		: true;
	return isStateless;
    }

    /**
     * Stores the Connection in static for username + connectionId
     * 
     * @param connection
     *            the Connection to store
     */
    public void put(Connection connection) {

	debug("Creating a Connection for user: " + connectionKey);
	if (connection == null) {
	    throw new IllegalArgumentException("connection is null!");
	}

	connectionMap.put(connectionKey, connection);
	connectionAge.put(connectionKey,
		(int) (new Date().getTime() / 1000 / 60));
    }

    /**
     * Stores the Savepoint in static for username + connectionId
     * 
     * @param savepoint
     *            the Savepoint to store
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
     * Returns the Savepoint associated to username + connectionId and
     * savepointInfo
     * 
     * @param a
     *            Savepoint that is just a container with the info to find the
     *            real one
     * 
     * @return the Savepoint associated to username + connectionId and
     *         savepointInfo
     */
    public Savepoint getSavepoint(Savepoint savepointInfo) {
	Set<Savepoint> savepointSet = savepointMap.get(connectionKey);

	for (Iterator<Savepoint> iterator = savepointSet.iterator(); iterator
		.hasNext();) {
	    Savepoint savepoint = (Savepoint) iterator.next();

	    try {
		if (savepoint.getSavepointId() == savepointInfo
			.getSavepointId()) {
		    return savepoint;
		}
	    } catch (SQLException e) {
		// We don't care: it's a named Savepoint
	    }

	    try {
		if (savepoint.getSavepointName()
			.equals(savepointInfo.getSavepointName())) {
		    return savepoint;
		}
	    } catch (SQLException e) {
		// We don't care: it's a unnamed Savepoint
	    }

	}

	return null;
    }

    /**
     * Remove the Savepoint associated to username + connectionId and
     * savepointInfo
     * 
     * @param a
     *            Savepoint that is just a container with the info to find the
     *            real one
     * 
     */
    public void remove(Savepoint savepointInfo) {
	Set<Savepoint> savepointSet = savepointMap.get(connectionKey);

	Set<Savepoint> savepointSetNew = new TreeSet<Savepoint>();

	for (Iterator<Savepoint> iterator = savepointSet.iterator(); iterator
		.hasNext();) {
	    Savepoint savepoint = (Savepoint) iterator.next();

	    boolean addIt = true;
	    try {
		if (savepoint.getSavepointId() == savepointInfo
			.getSavepointId()) {
		    addIt = false;
		}
	    } catch (SQLException e) {
		// We don't care: it's a named Savepoint
	    }

	    try {
		if (savepoint.getSavepointName()
			.equals(savepointInfo.getSavepointName())) {
		    addIt = false;
		}
	    } catch (SQLException e) {
		// We don't care: it's a unnamed Savepoint
	    }

	    if (addIt) {
		savepointSetNew.add(savepoint);
	    }
	}

	// Replace old map by new
	savepointMap.put(connectionKey, savepointSetNew);
    }

    /**
     * Stores the Array in static for username + connectionId
     * 
     * @param array
     *            the Array to store
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
     * @param arrayId
     *            the array id (it's haschode())
     * 
     * @return the Array associated to username + connectionId and arrayId
     */
    public Array getArray(int arrayId) {
	Set<Array> arraySet = arrayMap.get(connectionKey);

	for (Iterator<Array> iterator = arraySet.iterator(); iterator
		.hasNext();) {
	    Array array = (Array) iterator.next();

	    if (array.hashCode() == arrayId) {
		return array;
	    }
	}

	return null;
    }

    // /**
    // * Remove the Array associated to username + connectionId and ArrayId
    // *
    // * @param arrayId
    // * the array id (it's haschode())
    // *
    // */
    // public void removeArray(int arrayId) {
    // Set<Array> arraySet = arrayMap.get(connectionKey);
    //
    // Set<Array> ArraySetNew = new TreeSet<Array>();
    //
    // for (Iterator<Array> iterator = arraySet.iterator(); iterator.hasNext();)
    // {
    // Array array = (Array) iterator.next();
    //
    // boolean addIt = true;
    //
    // if (array.hashCode() == arrayId) {
    // addIt = false;
    // }
    //
    // if (addIt) {
    // ArraySetNew.add(array);
    // }
    // }
    //
    // // Replace old map by new
    // arrayMap.put(connectionKey, ArraySetNew);
    // }
    //

    /**
     * Stores the RowId in static for username + connectionId
     * 
     * @param rowId
     *            the RowId to store
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
     * @param rowIdHashCode
     *            the RowId id (it's haschode())
     * 
     * @return the Array associated to username + connectionId and arrayId
     */
    public RowId getRowId(int rowIdHashCode) {
	Set<RowId> rowIdSet = rowIdMap.get(connectionKey);

	for (Iterator<RowId> iterator = rowIdSet.iterator(); iterator
		.hasNext();) {
	    RowId rowId = (RowId) iterator.next();

	    if (rowId.hashCode() == rowIdHashCode) {
		return rowId;
	    }
	}

	return null;
    }

    // /**
    // * Remove the RowId associated to username + connectionId and hashCode
    // *
    // * @param arrayId
    // * the array id (it's haschode())
    // *
    // */
    // public void removeRowId(String rowIdHashCode) {
    // Set<RowId> arraySet = rowIdMap.get(connectionKey);
    //
    // Set<RowId> ArraySetNew = new TreeSet<RowId>();
    //
    // for (Iterator<RowId> iterator = arraySet.iterator(); iterator.hasNext();)
    // {
    // RowId rowId = (RowId) iterator.next();
    //
    // boolean addIt = true;
    //
    // if (rowId.hashCode() == Integer.parseInt(rowIdHashCode)) {
    // addIt = false;
    // }
    //
    // if (addIt) {
    // ArraySetNew.add(rowId);
    // }
    // }
    //
    // // Replace old map by new
    // rowIdMap.put(connectionKey, ArraySetNew);
    // }

    /**
     * Returns the Connection associated to username + connectionId
     * 
     * @return the Connection associated to username + connectionId
     */
    public Connection get() {
	return connectionMap.get(connectionKey);
    }

    /**
     * Remove all stored instances in the ConnectionStore. This must be done
     * only in a Logout stage ({@code Connection#close()}).
     */
    public void remove() {
	debug("Removing a Connection for user: " + connectionKey);
	clean();
	connectionMap.remove(connectionKey);
    }

    /**
     * Remove the Connection info associated to username + connectionId. <br>
     * But keeps the entry in connectionMap do that program knows if client user
     * is in Stateful mode or not.
     */
    public void clean() {
	debug("Cleaning a Connection for user: " + connectionKey);

	// NO: says the connection is stateful
	// connectionMap.remove(connectionKey);

	connectionAge.remove(connectionKey);
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
     * Returns the age of the connection in minutes for an id username +
     * connectionId
     * 
     * @param connectionkey
     *            the connection key that contains the username and the
     *            connection Id
     * @return the age of the connection in minutes
     */
    public static int getAge(ConnectionKey connectionkey) {

	Integer ageInMinutes = connectionAge.get(connectionkey);
	if (ageInMinutes == null || ageInMinutes == 0) {
	    // ageInMinutes = new Integer(0);
	    ageInMinutes = Integer.valueOf(0);
	}

	int nowInMinutes = (int) (new Date().getTime() / 1000 / 60);
	return nowInMinutes - ageInMinutes;
    }

    /**
     * Returns the keys of the store
     * 
     * @return the keys of the store
     */
    public static Set<ConnectionKey> getKeys() {
	return connectionMap.keySet();
    }

    /**
     * Method called by children Servlet for debug purpose Println is done only
     * if class name name is in kawansoft-debug.ini
     */
    public static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
