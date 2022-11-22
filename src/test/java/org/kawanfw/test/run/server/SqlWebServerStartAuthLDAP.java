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
package org.kawanfw.test.run.server;

import java.util.HashMap;
import java.util.Map;

import org.kawanfw.sql.WebServer;
import org.kawanfw.sql.api.util.webserver.WebServerApiWrapper;

/**
 * @author Nicolas de Pomereu
 *
 */
public class SqlWebServerStartAuthLDAP {

    private static Map<Integer, String> map = new HashMap<>();

    /**
     * no constructor
     */
    private SqlWebServerStartAuthLDAP() {

    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

	int port = 9092;

	map.put(port, "I:\\_dev_awake\\aceql-http-main\\aceql-http\\conf_test\\aceql-server-auth-ldap.properties");
	start(port);

	while(true) {
	    WebServerApiWrapper webServerApiWrapper = new WebServerApiWrapper();
	    if (webServerApiWrapper.isServerRunning(port)) {
		System.out.println("Server running on port " + port + "...");
		break;
	    }
	}
    }


    /**
     *
     */
    public static void start(int port) {
	Thread t = new Thread() {
	    @Override
	    public void run() {
		try {
		    String fileStr = map.get(port);
		    String portStr = port + "";
		    WebServer.main(new String[] { "-start", "-host", "localhost", "-port", portStr, "-properties", fileStr });
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	};
	t.start();
    }

}
