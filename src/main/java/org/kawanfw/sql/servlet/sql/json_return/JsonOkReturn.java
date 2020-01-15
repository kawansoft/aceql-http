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

import java.io.StringWriter;
import java.util.Map;

import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

/**
 * @author Nicolas de Pomereu
 *
 */
public class JsonOkReturn {

    /**
     * Returns just OK
     * 
     * @return just OK
     */
    public static String build() {

	JsonGeneratorFactory jf = JsonUtil
		.getJsonGeneratorFactory(JsonUtil.DEFAULT_PRETTY_PRINTING);

	StringWriter sw = new StringWriter();
	JsonGenerator gen = jf.createGenerator(sw);

	gen.writeStartObject().write("status", "OK").writeEnd();
	gen.close();

	return sw.toString();
    }

    /**
     * Returns a name and a value after the OK
     * 
     * @return just OK
     */
    public static String build(String name, String value) {

	if (name == null) {
	    throw new NullPointerException("name is null");
	}
	if (value == null) {
	    throw new NullPointerException("value is null");
	}

	JsonGeneratorFactory jf = JsonUtil
		.getJsonGeneratorFactory(JsonUtil.DEFAULT_PRETTY_PRINTING);

	StringWriter sw = new StringWriter();
	JsonGenerator gen = jf.createGenerator(sw);

	gen.writeStartObject().write("status", "OK").write(name, value)
		.writeEnd();
	gen.close();

	return sw.toString();
    }

    /**
     * Build a Json with name and values from the passed map
     * 
     * @param namesAndValues
     *            the map of (name, value) to add to the JsonGenerator
     * @return
     */
    public static String build(Map<String, String> namesAndValues) {

	if (namesAndValues == null) {
	    throw new NullPointerException("namesAndValues is null");
	}

	JsonGeneratorFactory jf = JsonUtil
		.getJsonGeneratorFactory(JsonUtil.DEFAULT_PRETTY_PRINTING);

	StringWriter sw = new StringWriter();
	JsonGenerator gen = jf.createGenerator(sw);

	gen.writeStartObject().write("status", "OK");

	for (Map.Entry<String, String> entry : namesAndValues.entrySet()) {

	    //System.out.println(entry.getKey() + "/" + entry.getValue());
	    gen.write(entry.getKey(), entry.getValue());
	}

	gen.writeEnd();
	gen.close();

	return sw.toString();
    }

}
