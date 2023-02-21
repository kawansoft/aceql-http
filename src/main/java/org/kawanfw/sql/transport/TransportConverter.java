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
package org.kawanfw.sql.transport;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.util.Base64;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.Tag;

/**
 * @author Nicolas de Pomereu
 * 
 *         Convert a String to a transportable format, aka ascii 7 & HTML
 */
public class TransportConverter {
    /** Debug flag */
    private static boolean DEBUG = FrameworkDebug
	    .isSet(TransportConverter.class);

    public static String CR_LF = System.getProperty("line.separator");

    public static final String KAWANFW_BYTES = "**!kawanfw_bytes!**";

    // To say there is a file to fetch from the server
    public static final String KAWANFW_BYTES_STREAM_FILE = "**!kawanfw_stream_file!**";
    public static final String KAWANFW_STREAM_FAILURE = "**!kawanfw_stream_failure!**";
    public static final String KAWANFW_STREAM_NULL = "**!kawanfw_stream_null!**";

    /**
     * Constructor
     */
    protected TransportConverter() {
	// Not allowed
    }

    /**
     * Convert a byte array to a "transportable" format: - Transform it to Hex
     * value as String - Prefix it with **!kawanfw_bytes!**"
     * 
     * @param x
     *            the byte array to transport
     * @return a byte array in transportable format
     */
    public static String toTransportFormat(byte[] x) {
	String encodedString = null;

	// Allow to set null values and "transport it":
	if (x == null) {
	    encodedString = KAWANFW_BYTES + "null";
	} else {
	    encodedString = KAWANFW_BYTES + Base64.byteArrayToBase64(x);
	}

	return encodedString;
    }

    /**
     * Transform a byte [] transported in hex prefixed by "**!kawanfw_bytes!**"
     * to it's orginal byte []
     * 
     * @param string
     *            the string that contains the bytes prefixed by
     *            "**!kawanfw_bytes!**"
     * @return the bytes
     */
    public static byte[] fromTransportFormatToBytes(String string) {
	String encodedString = StringUtils.substringAfter(string,
		KAWANFW_BYTES);

	if (encodedString.equals("null")) {
	    return null;
	}

	try {
	    byte[] bytes = Base64.base64ToByteArray(encodedString);
	    return bytes;
	} catch (Exception e) {
	    throw new IllegalArgumentException(Tag.PRODUCT_PRODUCT_FAIL
		    + "String is not in BASE64 format: " + encodedString, e);
	}

    }

    /**
     * Displays the given message if DEBUG is set.
     * 
     * @param s
     *            the debug message
     */

    @SuppressWarnings("unused")
    private static void debug(String s) {
	if (DEBUG) {
	    System.out.println(s);
	}
    }

}
