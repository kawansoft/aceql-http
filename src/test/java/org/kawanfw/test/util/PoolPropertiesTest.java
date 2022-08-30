/*
 * Copyright (c)2022 KawanSoft S.A.S.
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-30
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.test.util;

import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 * @author Nicolas de Pomereu
 *
 */
public class PoolPropertiesTest {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	PoolProperties p = new PoolProperties();
	p.setDriverClassName("driverClassName");
	p.setUrl("url");
	p.setUsername("username");
	p.setPassword("password");

	p.setInitialSize(10);
	p.setMaxActive(100);

	// p.setMinIdle(10);
	// p.setMaxIdle(maxIdle);
	// p.setValidationQuery("SELECT 1");
	// p.setTimeBetweenEvictionRunsMillis(30000);

	// Other possible values to set
	// p.setTestOnBorrow(true);
	// p.setTestOnReturn(false);
	p.setValidationInterval(30000);

	p.setMaxWait(10000);
	p.setMinEvictableIdleTimeMillis(30000);

	p.setLogAbandoned(true);

	// p.setRemoveAbandonedTimeout(86400);
	p.setRemoveAbandoned(true);
	p.setRemoveAbandonedTimeout(3600);
	p.setRollbackOnReturn(true);

	p.setJdbcInterceptors("ConnectionState;StatementFinalizer");
    }

}
