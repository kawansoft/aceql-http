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

package org.kawanfw.sql.tomcat.util.jdbc;

import java.util.Objects;

public class JdbcInstanceInfo {

    private String driverClassName;
    private String url;
    private String username;
    
    public JdbcInstanceInfo(String driverClassName, String url, String username) {
	this.driverClassName = Objects.requireNonNull(driverClassName, "driverClassName cannot be null!");
	this.url = Objects.requireNonNull(url, "url cannot be null!");
	this.username = Objects.requireNonNull(username, "username cannot be null!");
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
	return "JdbcInstanceInfo [driverClassName=" + driverClassName + ", url=" + url + ", username=" + username + "]";
    }
   
}
