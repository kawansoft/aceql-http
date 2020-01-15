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
