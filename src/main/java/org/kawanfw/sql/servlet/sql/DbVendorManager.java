/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP                                     
 * Copyright (C) 2017,  KawanSoft SAS
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
package org.kawanfw.sql.servlet.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.api.util.SqlUtil;

/**
 * 
 * Helper class for treatment depending on DB Vendor.
 * 
 * @author Nicolas de Pomereu
 *
 */
/*
 * 
 * This class needs an oracle driver jar to compile. (Example: ojdbc6.jar with
 * Oracle Database 11g.)
 * 
 * If you don't use Oracle, just comment the code between the two tags: // BEGIN
 * COMMENT & // END COMMENT to allow compilation.
 */

public class DbVendorManager {
    
    /**
     * No constructor
     */
    protected DbVendorManager() {

    }

    public static String addLmt1(String sqlOrder, Connection connection)
	    throws SQLException {

	sqlOrder = sqlOrder.replace('\t', ' ');
	sqlOrder = sqlOrder.trim();

	if (!sqlOrder.toLowerCase().startsWith("select ")) {
	    return sqlOrder;
	}

	if (DbVendorManagerUtil.containsWord(sqlOrder, "l" + "i" + "m" + "i" +"t")) {
	    return sqlOrder;
	}

	sqlOrder = DbVendorManagerUtil.removeSemicolon(sqlOrder);

	sqlOrder += " L" + "I" + "M" + "I" +"T 1";
	return sqlOrder;
    }

    public static boolean checkDbVendor(Properties properties,
	    Connection connection) throws DatabaseConfigurationException {

	SqlUtil sqlUtil = null;
	
	try {
	    sqlUtil = new SqlUtil(connection);
	    
	    if (sqlUtil.isH2() || sqlUtil.isHSQLDB() || sqlUtil.isMySQL() || sqlUtil.isPostgreSQL()) {
		return true;
	    } 
	    
	} catch (SQLException e) {
	    throw new DatabaseConfigurationException(e.getMessage());
	}
	
	return false;	
    }

}
