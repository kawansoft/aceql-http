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
package org.kawanfw.sql.servlet;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
	 * @param request
	 *            the underlying HttpServletRequest
	 */
	public HttpServletRequestHolder(HttpServletRequest request) {
		super(request);
	}

	/**
	 * Request parameter emulation by adding a request parameter
	 * 
	 * @param name
	 *            the parameter name
	 * @param value
	 *            the parameter value
	 */
	public void setParameter(String name, String value) {

		if (name == null) {
			throw new NullPointerException("name is null!");
		}

		mapParameters.put(name, value);
	}

	/**
	 * Will return the request parameter.
	 * 
	 * @param parameterName
	 *            parameter name
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
