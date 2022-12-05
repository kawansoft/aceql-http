/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.test.stored_procedure;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Nicolas de Pomereu
 *
 */
public class TestStoredProcedureCommons {
    
    public static void selectCustomerExecute(Connection connection) throws SQLException {
	String sql = "select * from customer where customer_id >= 1 order by customer_id";
	Statement statement = connection.createStatement();
	statement.execute(sql);

	ResultSet rs = statement.getResultSet();

	while (rs.next()) {
	    System.out.println();
	    System.out.println("customer_id   : " + rs.getInt("customer_id"));
	    System.out.println("customer_title: " + rs.getString("customer_title"));
	    System.out.println("fname         : " + rs.getString("fname"));
	    System.out.println("lname         : " + rs.getString("lname"));
	}

	statement.close();
	rs.close();
    }
    

}
