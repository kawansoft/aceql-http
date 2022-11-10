/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.metadata.util;

import java.io.BufferedReader;
import java.io.StringReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * GSON utility class
 *
 * @author abecquereau
 *
 */
public final class GsonWsUtil {

    /**
     * Create json string representing object
     *
     * @param obj
     * @return
     */
    public static String getJSonString(final Object obj) {
	final GsonBuilder builder = new GsonBuilder();
	final Gson gson = builder.setPrettyPrinting().create();
	return gson.toJson(obj, obj.getClass());
    }

    /**
     * Create Object from jsonString
     *
     * @param jsonString
     * @param type
     * @return
     */
    public static <T extends Object> T fromJson(final String jsonString, final Class<T> type) {
	final GsonBuilder builder = new GsonBuilder();
	final Gson gson = builder.create();
	final BufferedReader bufferedReader = new BufferedReader(new StringReader(jsonString));
	final T dTO = gson.fromJson(bufferedReader, type);
	return dTO;
    }
}
