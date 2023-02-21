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
package org.kawanfw.sql.tomcat.util.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;

/**
 * Allows to crate a JDBC standalone Connection. Main purpose is for password
 * testing.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class ConnectionCreator {

    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private Exception exception;

    /**
     * Constructor.
     * 
     * @param driverClassName
     * @param url
     * @param username
     * @param password
     */
    public ConnectionCreator(String driverClassName, String url, String username, String password) {
	this.driverClassName = Objects.requireNonNull(driverClassName, "driverClassName cannot be null!");
	this.url = Objects.requireNonNull(url, "url cannot be null!");
	this.username = Objects.requireNonNull(username, "username cannot be null!");
	this.password = Objects.requireNonNull(password, "password cannot be null!");
    }

    /**
     * Tests if a Connection can be created with parameters passed to constructor
     * 
     * @return true if a Connection can be created, else false
     */
    public boolean canCreateConnection() {
	try {
	    Class.forName(driverClassName).newInstance();
	    DriverManager.setLoginTimeout(10); // to set login timeout
	    Connection connection = DriverManager.getConnection(url, username, password);
	    return connection == null ? false : true;
	} catch (Exception e) {
	    this.exception = e;
	    return false;
	}
    }

    /**
     * @return the Exception throwed by canCreateConnection if any
     */
    public Exception getException() {
	return exception;
    }

}
