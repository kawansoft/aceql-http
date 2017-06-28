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

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.connectionstore.ConnectionKey;
import org.kawanfw.sql.servlet.ConnectionCloser;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * @author Nicolas de Pomereu
 * 
 */
public class ConnectionStoreCleaner extends Thread {

    private static boolean DEBUG = FrameworkDebug.isSet(ConnectionStoreCleaner.class);

    public static boolean IS_RUNNING = false;

    private String database = null;


    /**
     * Constructor
     * @param database the database name
     */
    public ConnectionStoreCleaner(String database) {
	this.database = database;
    }

    /**
     * Cleans the ConnectionStore of old connections
     * 
     * @throws SQLException
     * @throws IOException
     */
    public void run() {

	try {

	    /*
	     * if (IS_RUNNING) { return; }
	     * 
	     * IS_RUNNING = true;
	     */

	    if (!continueRunning()) {
		return;
	    }

	    debug("Starting ConnectionStoreCleaner...");

	    while (true) {

		try {
		    Thread.sleep(5000);
		} catch (InterruptedException e) {
		}

		
		DatabaseConfigurator databaseConfigurator = ServerSqlManager.getDatabaseConfigurator(database);
		int maxAge = databaseConfigurator.getConnectionMaxAge();
		Set<ConnectionKey> keys = ConnectionStore.getKeys();

		debug("ConnectionStore Size: " + keys.size());

		// Duplicate the Set otherwise we will have a
		// java.util.ConcurrentModificationException...
		Set<ConnectionKey> keysDuplicate = new LinkedHashSet<ConnectionKey>();
		keysDuplicate.addAll(keys);

		for (Iterator<ConnectionKey> iterator = keysDuplicate
			.iterator(); iterator.hasNext();) {
		    ConnectionKey key = (ConnectionKey) iterator.next();

		    int age = ConnectionStore.getAge(key);

		    ConnectionStore connectionStore = new ConnectionStore(
			    key.getUsername(), key.getConnectionId());

		    debug("");
		    debug("size(): " + connectionStore.size());
		    debug("key   : " + key);
		    debug("maxAge: " + maxAge);
		    debug("age   : " + age);

		    if (age > maxAge) {
			debug("Cleaning Connection: " + key);
			debug("Connection age     : " + age);

			Connection connection = connectionStore.get();
			connectionStore.remove();
			debug("Store size         : " + connectionStore.size());

			ConnectionCloser.freeConnection(connection,
				databaseConfigurator);
		    }
		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static synchronized boolean continueRunning() {

	if (IS_RUNNING) {
	    return false;
	}

	IS_RUNNING = true;
	return true;
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
