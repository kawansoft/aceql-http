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

import java.io.IOException;
import java.io.StringWriter;

import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

/**
 * See
 * http://docs.oracle.com/middleware/1213/wls/WLPRG/java-api-for-json-proc.htm#WLPRG1065
 * 
 * @author Nicolas de Pomereu
 */
public class JsonExample {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
	StringWriter writer = new StringWriter();

	JsonGeneratorFactory jf = JsonUtil.getJsonGeneratorFactory(true);
	JsonGenerator gen = jf.createGenerator(writer);

	gen.writeStartObject().write("firstName", "Duke")
		.write("lastName", "Java").write("age", 18)
		.write("streetAddress", "100 Internet Dr")
		.write("city", "JavaTown").write("state", "JA")
		.write("postalCode", "12345")

		.writeStartArray("phoneNumbers").writeStartObject()
		.write("type", "mobile").write("number", "111-111-1111")
		.writeEnd().writeStartObject().write("type", "home")
		.write("number", "222-222-2222").writeEnd().writeEnd()

		.writeEnd();
	gen.close();

	System.out.println(writer.toString());

    }

}
