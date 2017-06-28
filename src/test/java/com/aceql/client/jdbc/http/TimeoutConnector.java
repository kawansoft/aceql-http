/*
 * This file is part of AceQL Client SDK.
 * AceQL Client SDK: Remote JDBC access over HTTP with AceQL HTTP.                                 
 * Copyright (C) 2017,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.                                
 *                                                                               
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
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
     * Gets an output stream from the HttpUrlConnection in less than connectTimeout milliseconds, otherwise
     * throws a SocketTimeoutException
     * 
     * @return the HttpUrlConnection output stream 
     * @throws IOException
     * @throws SocketTimeoutException
     */
    public OutputStream getOutputStream() throws IOException,
	    SocketTimeoutException {
	
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
		    System.out.println("Outut Connection get in: " + (end -begin));
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
