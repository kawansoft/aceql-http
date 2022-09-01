/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.sql.json_return;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

/**
 * @author Nicolas de Pomereu
 *
 */
public class JsonUtil {

    /** Always force pretty printing */
    public static final boolean DEFAULT_PRETTY_PRINTING = true;

    /**
     * protected
     */
    protected JsonUtil() {

    }

    /**
     * JsonGeneratorFactory getter with pretty printing on/off
     *
     * @param prettyPrintingif
     *            true, JSON will be pretty printed
     * @return
     */
    public static JsonGeneratorFactory getJsonGeneratorFactory(
	    boolean prettyPrinting) {
	Map<String, Object> properties = new HashMap<>(1);
	if (prettyPrinting) {
	    // Putting any value sets the pretty printing to true... So test
	    // must be done
	    properties.put(JsonGenerator.PRETTY_PRINTING, prettyPrinting);
	}

	JsonGeneratorFactory jf = Json.createGeneratorFactory(properties);
	return jf;
    }

}
