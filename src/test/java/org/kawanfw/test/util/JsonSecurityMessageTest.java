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
	String jsonErrorMessage = JsonSecurityMessage.statementNotAllowedBuild(sqlOrder, errorMessage, doPrettyPrinting);
	System.out.println(jsonErrorMessage);

	Map<Integer, String> parameters = new HashMap<>();
	parameters.put(1, "VARCHAR");
	parameters.put(2, "INTEGER");
	List<Object> values = new ArrayList<>();
	values.add("Doe");
	values.add(1);

	sqlOrder = "UPDATE CUSTOMER SET ? WHERE CUSTOMER_ID = ?";
	errorMessage = "Prepared Statement not allowed.";
	jsonErrorMessage = JsonSecurityMessage.prepStatementNotAllowedBuild(sqlOrder, errorMessage, parameters, values, doPrettyPrinting);
	System.out.println();
	System.out.println(jsonErrorMessage);
    }
}
