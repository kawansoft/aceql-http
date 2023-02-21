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
