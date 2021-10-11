/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP                                     
 * Copyright (C) 2021,  KawanSoft SAS
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
