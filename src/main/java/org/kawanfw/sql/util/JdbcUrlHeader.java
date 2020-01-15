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
package org.kawanfw.sql.util;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.version.Version;

/**
 * @author Nicolas de Pomereu
 * 
 *         Global var and methods to manipuat the url header jdbc:aceql. Note
 *         that this class is used oborh on client side and server side.
 */
public class JdbcUrlHeader {

    /** The header of AceQL JDBC URL */
    public static final String JDBC_URL_HEADER = "jdbc:"
	    + Version.PRODUCT.NAME.toLowerCase() + ":";

    /**
     * protected constructor
     */
    protected JdbcUrlHeader() {

    }

    /**
     * Returns the HTTP URL
     * 
     * @param url
     *            the JDBC URL with maybe "jdbc:aceql:" header
     * @return the pure HTTP URL
     */
    public static String getUrlHttpOnly(String url) {

	if (url == null) {
	    throw new IllegalArgumentException("url is null!");
	}

	String urlHttpOnly = url;
	if (url.startsWith(JDBC_URL_HEADER)) {
	    urlHttpOnly = StringUtils.substringAfter(url, JDBC_URL_HEADER);
	}
	return urlHttpOnly;
    }

    /**
     * Return the url prefixed by "jdbc:aceql:"
     * 
     * @param url
     *            the JDBC URL with maybe or not "jdbc:aceql:" header
     * @return the url with "jdbc:aceql:" headers
     */
    public static String prefixUrlWithJdbcProductName(String url) {

	if (url == null) {
	    throw new IllegalArgumentException("url is null!");
	}

	if (!url.startsWith(JDBC_URL_HEADER)) {
	    url = JDBC_URL_HEADER + url;
	}

	return url;

    }

}
