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
package org.kawanfw.sql.servlet.sql;

import java.util.List;
import java.util.Map;

/**
 * @author Nicolas de Pomereu
 *
 */
public class StatementFailure {

    /**
     * 
     */
    protected StatementFailure() {

    }

    /**
     * Builds a failure error message for Prepared Statements
     * 
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

	String returnString = "Prepared Statement Exception: " + errorMessage
		+ " - SQL order: " + sqlOrder + " - parms:" + parameters
		+ " - values: " + values;
	return returnString;

	// try {
	// JsonGeneratorFactory jf = JsonUtil
	// .getJsonGeneratorFactory(doPrettyPrinting);
	//
	// ByteArrayOutputStream out = new ByteArrayOutputStream();
	//
	// JsonGenerator gen = jf.createGenerator(out);
	// gen.writeStartObject();
	// gen.write("Prepared Statement Exception", errorMessage);
	// gen.write("SQL order", sqlOrder);
	//
	// gen.writeStartArray("Parameter types");
	// for (Map.Entry<Integer, String> entry : parameters.entrySet()) {
	// int key = entry.getKey();
	// String value = entry.getValue();
	// gen.writeStartObject();
	// gen.write(key + "", value);
	// gen.writeEnd();
	// }
	// gen.writeEnd();
	//
	// gen.writeStartArray("Parameter values");
	// for (Object value : values) {
	// gen.write((value != null) ? value.toString() : "null");
	// }
	// gen.writeEnd();
	//
	// gen.writeEnd();
	// gen.close();
	// return out.toString();
	// } catch (Exception e) {
	// // Never fail, just return the string
	// String returnString = "Prepared Statement Exception: "
	// + errorMessage + " " + sqlOrder + " " + parameters + " "
	// + values;
	// return returnString;
	// }

    }

    /**
     * Builds a failure error message for Statements
     * 
     * @param sqlOrder
     * @param errorMessage
     * @param doPrettyPrinting
     * @return
     */
    public static String statementFailureBuild(String sqlOrder,
	    String errorMessage, boolean doPrettyPrinting) {

	String returnString = "Statement Exception: " + errorMessage
		+ " - SQL order: " + sqlOrder;
	return returnString;

	// try {
	// JsonGeneratorFactory jf = JsonUtil
	// .getJsonGeneratorFactory(doPrettyPrinting);
	//
	// ByteArrayOutputStream out = new ByteArrayOutputStream();
	//
	// JsonGenerator gen = jf.createGenerator(out);
	// gen.writeStartObject();
	// gen.write("Statement Exception", errorMessage);
	// gen.write("SQL order", sqlOrder);
	// gen.writeEnd();
	// gen.close();
	// return out.toString();
	// } catch (Exception e) {
	// // Never fail, just return the string
	// String returnString = "Statement Exception: " + errorMessage + " "
	// + sqlOrder;
	// return returnString;
	// }

    }

}
