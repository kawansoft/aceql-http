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
     * 
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
	    for (Map.Entry<Integer, String> entry : parameters.entrySet()) {
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
     * 
     * @param sqlOrder
     * @param errorMessage
     * @param doPrettyPrinting
     * @return
     */
    public static String statementNotAllowedBuild(String sqlOrder,
	    String errorMessage, boolean doPrettyPrinting) {

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
