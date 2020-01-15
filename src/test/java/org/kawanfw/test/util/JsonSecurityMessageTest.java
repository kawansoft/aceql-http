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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kawanfw.sql.servlet.sql.json_return.JsonSecurityMessage;

public class JsonSecurityMessageTest {

    public JsonSecurityMessageTest() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

	boolean doPrettyPrinting = true;
	String sqlOrder = "DELETE FROM CUSTOMER";

	String errorMessage = "Statement not allowed for ExecuteUpdate";
	String jsonErrorMessage = JsonSecurityMessage.statementNotAllowedBuild(
		sqlOrder, errorMessage, doPrettyPrinting);
	System.out.println(jsonErrorMessage);

	Map<Integer, String> parameters = new HashMap<>();
	parameters.put(1, "VARCHAR");
	parameters.put(2, "INTEGER");
	List<Object> values = new ArrayList<>();
	values.add("Doe");
	values.add(1);

	sqlOrder = "UPDATE CUSTOMER SET ? WHERE CUSTOMER_ID = ?";
	errorMessage = "Prepared Statement not allowed.";
	jsonErrorMessage = JsonSecurityMessage.prepStatementNotAllowedBuild(
		sqlOrder, errorMessage, parameters, values, doPrettyPrinting);
	System.out.println();
	System.out.println(jsonErrorMessage);
    }
}
