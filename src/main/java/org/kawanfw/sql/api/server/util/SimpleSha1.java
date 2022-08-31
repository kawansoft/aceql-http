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
package org.kawanfw.sql.api.server.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * Simple SHA-1 implementation.
 *
 * @author Nicolas de Pomereu
 *
 */
public class SimpleSha1 {

    /**
     * Returns the SHA-1 digest of input text in hexadecimal string
     *
     * @param text        the text to hash
     * @param toLowerCase if true, hexadecimal string is returned in lower case
     * @return the SHA-1 digest of input text in hexadecimal string
     */
    public static String sha1(String text, boolean toLowerCase) {
	MessageDigest md = null;
	try {
	    md = MessageDigest.getInstance("SHA-1");
	} catch (Exception e) {
	    throw new IllegalArgumentException(e);
	}
	byte[] sha1hash = new byte[40];
	try {
	    md.update(text.getBytes("UTF-8"), 0, text.length());
	} catch (IllegalArgumentException e) {
	    throw e;
	} catch (UnsupportedEncodingException e) {
	    throw new IllegalArgumentException(e);
	}

	sha1hash = md.digest();

	String hexString = convertToHex(sha1hash);
	if (toLowerCase) {
	    hexString = hexString.toLowerCase();
	}
	return hexString;
    }

    private static String convertToHex(byte[] data) {
	StringBuffer buf = new StringBuffer();
	for (int i = 0; i < data.length; i++) {
	    int halfbyte = (data[i] >>> 4) & 0x0F;
	    int two_halfs = 0;
	    do {
		if ((0 <= halfbyte) && (halfbyte <= 9))
		    buf.append((char) ('0' + halfbyte));
		else
		    buf.append((char) ('a' + (halfbyte - 10)));
		halfbyte = data[i] & 0x0F;
	    } while (two_halfs++ < 1);
	}
	return buf.toString();
    }
}
