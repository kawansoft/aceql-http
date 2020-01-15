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
package com.aceql.client.jdbc.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;

/**
 * Allows to get the HttpUrlConnection output stream with a real timeout using a
 * thread and a timer.
 * 
 * @author Nicolas de Pomereu
 *
 */
class TimeoutConnector {

    public static boolean DEBUG = false;

    private HttpURLConnection conn = null;

    /** The network output stream */
    private OutputStream os = null;

    /** boolean that says if we are connected to remote server */
    private boolean connected = false;

    /** Exception thrown by URL.connect() */
    private IOException exception = null;

    private int connectTimeout = 0;

    /**
     * Constructor.
     * 
     * @param conn
     *            the current connection
     * @param connectTimeout
     *            the connection timeout
     */
    public TimeoutConnector(HttpURLConnection conn, int connectTimeout) {
	this.conn = conn;
	this.connectTimeout = connectTimeout;
    }

    /**
     * Gets an output stream from the HttpUrlConnection in less than
     * connectTimeout milliseconds, otherwise throws a SocketTimeoutException
     * 
     * @return the HttpUrlConnection output stream
     * @throws IOException
     * @throws SocketTimeoutException
     */
    public OutputStream getOutputStream()
	    throws IOException, SocketTimeoutException {

	os = null;
	connected = false;
	exception = null;

	Thread t = new Thread() {
	    public void run() {
		try {
		    os = conn.getOutputStream();
		    connected = true;
		} catch (IOException e) {
		    exception = e;
		}
	    }
	};

	t.start();

	long begin = System.currentTimeMillis();

	while (true) {

	    if (connected) {

		if (DEBUG) {
		    long end = System.currentTimeMillis();
		    System.out.println(
			    "Outut Connection get in: " + (end - begin));
		}

		return os;
	    }

	    if (exception != null) {
		throw exception;
	    }

	    if (connectTimeout != 0) {
		long end = System.currentTimeMillis();
		if ((end - begin) > connectTimeout) {
		    throw new SocketTimeoutException(
			    "Unable to establish connection in less than required "
				    + connectTimeout + " milliseconds.");
		}
	    }

	    try {
		Thread.sleep(1); // Very, very short sleep...s
	    } catch (InterruptedException ie) {
		ie.printStackTrace();
	    }
	}
    }

}
