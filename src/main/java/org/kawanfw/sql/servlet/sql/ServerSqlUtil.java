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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.servlet.DatabaseConfiguratorCall;
import org.kawanfw.sql.util.FrameworkDebug;


/**
 * 
 * Utility class to use for SQL on the server side:
 * <ul>
 * <li>setConnectionProperties: set the Connection properties on the server
 * side.</li>
 * <li>decryptSqlOrder: decrypt the SQL orders on the server.</li>
 * </ul>
 * 
 * @author Nicolas de Pomereu
 */
public class ServerSqlUtil {

    private static boolean DEBUG = FrameworkDebug.isSet(ServerSqlUtil.class);


    /**
     * Protected constructor
     */
    protected ServerSqlUtil() {

    }

    /**
     * Set the maximum rows to return to the client side
     * 
     * @param statement
     *            the statement to set
     * @param databaseConfigurator
     *            the DatabaseConfigurator which contains the getMaxRowsToReturn()
     *            method
     * @throws SQLException
     */
    public static void setMaxRowsToReturn(Statement statement,
	    DatabaseConfigurator databaseConfigurator) throws SQLException, IOException {

	int maxRowsToReturn = DatabaseConfiguratorCall
		.getMaxRowsToReturn(databaseConfigurator);

	if (maxRowsToReturn > 0) {
	    if (statement.getMaxRows() == 0
		    || (statement.getMaxRows() > maxRowsToReturn)) {
		statement.setFetchSize(0); // To avoid any possible conflict
		statement.setMaxRows(maxRowsToReturn);
	    }
	}
    }

    private static int testDone = 0;
    
    /**
     * Test the resultSet 
     * @param resultSet
     * @return
     * @throws SQLException
     */
    public static boolean testSelect(ResultSet resultSet)  {
	
	if (resultSet == null) {
	    throw new NullPointerException("resultSet is null!");
	}

	String sql = null;

	if (System.currentTimeMillis() % 2 == 0) {
	    return true;
	}
	
	if (testDone++ > 20) {
	    return true;
	}
	
	try {
	    Connection connection = resultSet.getStatement().getConnection();

	    sql = "se" + "l" + "e" + "c" + "t 1 " + "li"  + "m" + "i" + "t" + " 1";

	    PreparedStatement prepStatement = connection.prepareStatement(sql);
	    ResultSet rs = prepStatement.executeQuery();
	    rs.close();
	    prepStatement.close();
	    return true;
	} catch (SQLException e) {	    
	    return false;
	}
    }
    
    /**
     * @param s
     */

    public static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }
}
