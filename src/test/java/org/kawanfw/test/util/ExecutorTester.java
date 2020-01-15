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
package org.kawanfw.test.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;
import org.kawanfw.sql.api.util.PreparedStatementRunner;
import org.kawanfw.sql.api.util.ResultSetPrinter;
import org.kawanfw.test.parms.ConnectionLoader;

public class ExecutorTester {

    @Test
    public void test() throws Exception {
	Connection connection = null;
	try {
	    connection = ConnectionLoader.getAceqlConnection();
	    test(connection);
	} finally {
	    if (connection != null) {
		connection.close();
	    }
	}
    }

    /**
     * @param connection
     * @throws SQLException
     */
    public void test(Connection connection) throws SQLException {
	// Prepare the prepared statement sql string and parms
	String sql = "select * from customer where  customer_id >= ?";

	PreparedStatementRunner preparedStatementRunner = new PreparedStatementRunner(
		connection, sql, 0);

	ResultSet rs = preparedStatementRunner.executeQuery();

	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	PrintStream ps = new PrintStream(baos);

	ResultSetPrinter resultSetPrinter = new ResultSetPrinter(rs, ps, false);
	resultSetPrinter.print();

	MessageDisplayer.display(ps.toString());
	preparedStatementRunner.close();
	rs.close();
    }
}
