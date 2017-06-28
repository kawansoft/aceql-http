/**
 * 
 */
package org.kawanfw.sql.servlet.sql.json_return;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

/**
 * @author Nicolas de Pomereu
 *
 */
public class JsonStatementFailure {

    /**
     * 
     */
    protected JsonStatementFailure() {

    }

    /**
     * @param args
     */
    public static void main(String[] args) {

	boolean doPrettyPrinting = true;
	String sqlOrder = "DELETE FROM DUSTOMER";
	
	String errorMessage = "Statement Exception.";
	String jsonErrorMessage = statementFailureBuild(sqlOrder, errorMessage, doPrettyPrinting);
	System.out.println(jsonErrorMessage);

	Map<Integer, String> parameters = new HashMap<>();
	parameters.put(1, "VARCHAR");
	parameters.put(2, "INTEGER");
	List<Object> values = new ArrayList<>();
	values.add("Doe");
	values.add(1);

	sqlOrder = "UPDATE DUSTOMER SET ? WHERE CUSTOMER_ID = ?";
	errorMessage = "Prepared Statement failure.";
	jsonErrorMessage = prepStatementFailureBuild(sqlOrder, errorMessage, parameters, values, doPrettyPrinting);
	System.out.println();
	System.out.println(jsonErrorMessage);
    }

    /**
     * Builds a failure error message in JSON format for Prepared Statements
     * @param sqlOrder
     * @param errorMessage
     * @param parameters
     * @param values
     * @param doPrettyPrinting
     * @return
     */
    public static String prepStatementFailureBuild(String sqlOrder,
	    String errorMessage, Map<Integer, String> parameters,
	    List<Object> values, boolean doPrettyPrinting) {

	JsonGeneratorFactory jf = JsonUtil
		.getJsonGeneratorFactory(doPrettyPrinting);

	ByteArrayOutputStream out = new ByteArrayOutputStream();
	
	JsonGenerator gen = jf.createGenerator(out);
	gen.writeStartObject();
	gen.write("Prepared Statement Exception", errorMessage);
	gen.write("SQL order", sqlOrder);
	
	gen.writeStartArray("Parameter types");
	for(Map.Entry<Integer, String> entry : parameters.entrySet()) {
	    int key = entry.getKey();
	    String value = entry.getValue();
	    gen.writeStartObject();
	    gen.write(key + "", value);
	    gen.writeEnd();
	}
	gen.writeEnd();
	
	gen.writeStartArray("Parameter values");
	for (Object value : values) {
	    gen.write(value.toString());
	}
	gen.writeEnd();
	
	gen.writeEnd();
	gen.close();
	return out.toString();
    }

    /**
     * Builds a failure error message in JSON format for Statements
     * @param sqlOrder
     * @param errorMessage
     * @param doPrettyPrinting
     * @return
     */
    public static String statementFailureBuild(String sqlOrder, String errorMessage, boolean doPrettyPrinting) {
	
	JsonGeneratorFactory jf = JsonUtil
		.getJsonGeneratorFactory(doPrettyPrinting);

	ByteArrayOutputStream out = new ByteArrayOutputStream();
	
	JsonGenerator gen = jf.createGenerator(out);
	gen.writeStartObject();
	gen.write("Statement Exception", errorMessage);
	gen.write("SQL order", sqlOrder);
	gen.writeEnd();
	gen.close();
	return out.toString();
	
    }

}
