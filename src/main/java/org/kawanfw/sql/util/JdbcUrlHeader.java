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
package org.kawanfw.sql.util;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.version.VersionWrapper;

/**
 * @author Nicolas de Pomereu
 *
 *         Global var and methods to manipuat the url header jdbc:aceql. Note
 *         that this class is used oborh on client side and server side.
 */
public class JdbcUrlHeader {

    /** The header of AceQL JDBC URL */
    public static final String JDBC_URL_HEADER = "jdbc:"
	    + VersionWrapper.getName().toLowerCase() + ":";

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
    public static String prefixUrlWithJdbcProductName(final String url) {

	if (url == null) {
	    throw new IllegalArgumentException("url is null!");
	}

	String urlNew = url;

	if (!urlNew.startsWith(JDBC_URL_HEADER)) {
	    urlNew = JDBC_URL_HEADER + urlNew;
	}

	return urlNew;

    }

}
