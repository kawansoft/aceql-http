/**
 * 
 */
package org.kawanfw.sql.servlet.sql.json_return;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import org.kawanfw.sql.util.Tag;

/**
 * @author Nicolas de Pomereu
 *
 */
public class JsonSecurityMessage {

    /**
     * 
     */
    protected JsonSecurityMessage() {

    }


    /**
     * Builds a security error message in JSON format for Prepared Statements
     * @param sqlOrder
     * @param errorMessage
     * @param parameters
     * @param values
     * @param doPrettyPrinting
     * @return
     */
    public static String prepStatementNotAllowedBuild(String sqlOrder,
	    String errorMessage, Map<Integer, String> parameters,
	    List<Object> values, boolean doPrettyPrinting) {

	try {
	    JsonGeneratorFactory jf = JsonUtil
	    	.getJsonGeneratorFactory(doPrettyPrinting);

	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    
	    JsonGenerator gen = jf.createGenerator(out);
	    gen.writeStartObject();
	    gen.write(Tag.PRODUCT_SECURITY, errorMessage);
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
	        gen.write((value != null) ? value.toString() : "null");
	    }
	    gen.writeEnd();
	    
	    gen.writeEnd();
	    gen.close();
	    return out.toString("UTF-8");
	} catch (Exception e) {
	    String returnString = Tag.PRODUCT_SECURITY + " " + errorMessage
		    + " " + sqlOrder + " " + parameters + " " + values;
	    return returnString;
	}
    }

    /**
     * Builds a security error message in JSON format for Statements
     * @param sqlOrder
     * @param errorMessage
     * @param doPrettyPrinting
     * @return
     */
    public static String statementNotAllowedBuild(String sqlOrder, String errorMessage, boolean doPrettyPrinting) {
	
	try {
	    JsonGeneratorFactory jf = JsonUtil
	    	.getJsonGeneratorFactory(doPrettyPrinting);

	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    
	    JsonGenerator gen = jf.createGenerator(out);
	    gen.writeStartObject();
	    gen.write(Tag.PRODUCT_SECURITY, errorMessage);
	    gen.write("SQL order", sqlOrder);
	    gen.writeEnd();
	    gen.close();
	    return out.toString("UTF-8");
	} catch (Exception e) {
	    String returnString = Tag.PRODUCT_SECURITY + " " + errorMessage
		    + " " + sqlOrder;
	    return returnString;
	}
	
    }

}
