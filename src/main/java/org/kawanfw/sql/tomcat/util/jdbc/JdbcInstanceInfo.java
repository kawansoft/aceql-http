/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
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
