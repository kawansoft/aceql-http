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
		.write("type", "mobile")
		.write("number", "111-111-1111")
		.writeEnd()
		.writeStartObject().write("type", "home")
		.write("number", "222-222-2222").writeEnd().writeEnd()

		.writeEnd();
	gen.close();

	System.out.println(writer.toString());

    }

}
