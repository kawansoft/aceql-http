/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.kawanfw.sql.util.FrameworkDebug;

/**
 * Wrapper/holder for HttpServletRequest that will allow to set some parameters.
 *
 * @author Nicolas de Pomereu
 *
 */
public class HttpServletRequestHolder extends HttpServletRequestWrapper {

    private static boolean DEBUG = FrameworkDebug.isSet(HttpServletRequestHolder.class);

    /** The map of emulated request parameters */
    // No concurrency support to allow null values
    private Map<String, String> mapParameters = new HashMap<>();

    /**
     * Constructor
     *
     * @param request the underlying HttpServletRequest
     */
    public HttpServletRequestHolder(HttpServletRequest request) {
	super(request);
    }

    /**
     * Request parameter emulation by adding a request parameter
     *
     * @param name  the parameter name
     * @param value the parameter value
     */
    public void setParameter(String name, String value) {

	Objects.requireNonNull(name, "name cannot be null!");
	mapParameters.put(name, value);
    }

    /**
     * Will return the request parameter.
     *
     * @param parameterName parameter name
     * @return the parameter value
     */
    @Override
    public String getParameter(String parameterName) {

	debug("getParameter of " + parameterName + ":");

	String value = null;
	if (mapParameters.containsKey(parameterName)) {
	    value = mapParameters.get(parameterName);
	} else {
	    value = super.getParameter(parameterName);
	}

	debug("value: " + value + ":");
	return value;

    }

    private void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
