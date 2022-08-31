/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.test.run.server;

import org.kawanfw.sql.api.util.webserver.WebServerApiWrapper;

/**
 * @author Nicolas de Pomereu
 *
 */
public class SqlWebServerStopNew {

    /**
     * no constructor
     */
    private SqlWebServerStopNew() {

    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
	stopOnPort(9090);
	stopOnPort(9091);
	stopOnPort(9092);
	stopOnPort(9093);
	stopOnPort(9094);
	stopOnPort(9095);
	stopOnPort(9096);
	stopOnPort(9096);
	stopOnPort(9097);
    }

    /**
     * @param port
     */
    private static void stopOnPort(int port) {
	WebServerApiWrapper webServerApiWrapper = new WebServerApiWrapper();
	try {
	    System.out.println("Stoping AceQL on port: " + port);
	    webServerApiWrapper.stopServer(port);
	} catch (Exception e) {
	    System.err.println(e.getMessage());
	}
    }
}
