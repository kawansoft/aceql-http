/*
 * Copyright (c)2022 KawanSoft S.A.S.
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-30
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.sql.json_return;

import java.io.StringWriter;
import java.util.Map;
import java.util.Objects;

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

	Objects.requireNonNull(name, "name cannot be null!");
	Objects.requireNonNull(value, "value cannot be null!");

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

	Objects.requireNonNull(namesAndValues, "namesAndValues cannot be null!");

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
