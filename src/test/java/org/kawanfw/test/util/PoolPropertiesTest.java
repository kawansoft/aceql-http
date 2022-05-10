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
