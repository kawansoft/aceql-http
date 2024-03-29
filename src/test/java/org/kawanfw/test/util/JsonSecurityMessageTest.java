/*
 * Copyright (c)2023 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-02-21
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.test.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kawanfw.sql.servlet.sql.json_return.JsonSecurityMessage;

public class JsonSecurityMessageTest {

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
