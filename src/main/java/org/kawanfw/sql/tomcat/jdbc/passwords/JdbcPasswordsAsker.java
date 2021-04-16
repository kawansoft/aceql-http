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
package org.kawanfw.sql.tomcat.jdbc.passwords;

import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.SystemUtils;

/**
 * 
 * @author Nicolas de Pomereu
 *
 */
public class JdbcPasswordsAsker {

    private Properties properties = null;

    /**
     * Constructor
     * 
     * @param properties
     */
    public JdbcPasswordsAsker(Properties properties) {
	this.properties = properties;
    }

    /**
     * Check the JDBC passwords
     * 
     * @return true if all passwords are correct, else false
     */
    public boolean askPasswords() {

	List<DriverInstanceInfo> instances = ConnectionCreatorUtil.createInstances(properties);
	for (DriverInstanceInfo driverInstanceInfo : instances) {
	    boolean passwordOk = askPassword(driverInstanceInfo);
	    if (!passwordOk) {
		return false;
	    }
	}
	return true;
    }

    private boolean askPassword(DriverInstanceInfo driverInstanceInfo) {
	if (SystemUtils.IS_OS_LINUX) {
	    return askPasswordLinux(driverInstanceInfo);
	} else {
	    return askPasswordWindows(driverInstanceInfo);
	}
    }

    private boolean askPasswordWindows(DriverInstanceInfo driverInstanceInfo) {
	return false;
    }

    private boolean askPasswordLinux(DriverInstanceInfo driverInstanceInfo) {
	return false;
    }
}
