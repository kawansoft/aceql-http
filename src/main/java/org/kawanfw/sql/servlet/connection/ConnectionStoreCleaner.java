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
package org.kawanfw.sql.servlet.connection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.kawanfw.sql.api.server.connectionstore.ConnectionKey;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * @author Nicolas de Pomereu
 * 
 */
public class ConnectionStoreCleaner extends Thread {

	/**
	 * Defines the number of milliseconds between two clean calls. One hour is good
	 * value
	 */
	public static final int CLEAN_INTERVAL_MILLISECONDS = 3600 * 1000;

	public static boolean DEBUG = FrameworkDebug.isSet(ConnectionStoreCleaner.class);

	public static long LAST_CLEAN_TIME = 0;

	/**
	 * Cleans the ConnectionStore of old connections
	 * 
	 * @throws SQLException
	 * @throws IOException
	 */
	public void run() {
		LAST_CLEAN_TIME = System.currentTimeMillis();
		runSynchronized();
	}

	/**
	 * Says if it is time to clean connection store.
	 * 
	 * @return true if last clean has been done in more than
	 *         {@code ConnectionStoreCleaner.CLEAN_INTERVAL_MILLISECONDS}
	 */
	public static boolean timeToCleanConnectionStore() {
		long now = System.currentTimeMillis();
		if (now - LAST_CLEAN_TIME > ConnectionStoreCleaner.CLEAN_INTERVAL_MILLISECONDS) {
			return true;
		} else {
			return false;
		}
	}

	private static synchronized void runSynchronized() {
		try {

			debug("");
			debug("Starting ConnectionStoreCleaner...");
			Set<ConnectionKey> keys = ConnectionStore.getKeys();

			debug("ConnectionStore Size: " + keys.size());

			// Duplicate the Set otherwise we will have a
			// java.util.ConcurrentModificationException...
			Set<ConnectionKey> keysToRemove = new LinkedHashSet<ConnectionKey>();

			for (Iterator<ConnectionKey> iterator = keys.iterator(); iterator.hasNext();) {
				ConnectionKey connectionKey = iterator.next();
				ConnectionStore connectionStore = new ConnectionStore(connectionKey.getUsername(),
						connectionKey.getSessionId(), connectionKey.getConnectionId());
				Connection connection = connectionStore.get();
				if (connection == null || connection.isClosed()) {
					keysToRemove.add(connectionKey);
				}
			}

			for (Iterator<ConnectionKey> iterator = keysToRemove.iterator(); iterator.hasNext();) {
				ConnectionKey key = (ConnectionKey) iterator.next();

				ConnectionStore connectionStore = new ConnectionStore(key.getUsername(), key.getSessionId(),
						key.getConnectionId());

				debug("size(): " + connectionStore.size());
				debug("key   : " + key);
				debug("Cleaning Connection: " + key + " (connection is null or closed)");
				connectionStore.remove();
				debug("Store new size     : " + connectionStore.size());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			debug("ConnectionStoreCleaner END.");
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
