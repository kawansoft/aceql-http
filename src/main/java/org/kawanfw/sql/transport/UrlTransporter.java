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
package org.kawanfw.sql.transport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.util.Base64;

/**
 * @author Nicolas de Pomereu
 *
 *         Allows to transform with serialization an URL to Base64 String and
 *         vice versa.
 */
public class UrlTransporter {

    public static final String URL_HEADER = "**!kawanfw_url_header!**";

    /**
     * Transforms an URL to serialized Base 64 String.
     *
     * @param url
     *            the URL to transform
     * @return a serialized URL in Base64 format
     * @throws IOException
     */
    public String toBase64(URL url) throws IOException {

	ByteArrayOutputStream bos = new ByteArrayOutputStream();

	ObjectOutputStream oos = null;
	try {
	    oos = new ObjectOutputStream(bos);
	    oos.writeObject(url);
	    oos.flush();

	    byte[] byteArray = bos.toByteArray();
	    String base64 = URL_HEADER + Base64.byteArrayToBase64(byteArray);
	    return base64;
	} finally {
	    if (oos != null) {
		oos.close();
	    }
	}
    }

    /**
     * Transforms a serialized Base 64 String to an URL .
     *
     * @param string
     *            a serialized URL in Base64 format
     * @return the rebuilt URL
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public URL fromBase64(final String string) throws IOException, ClassNotFoundException {

	String stringNew = StringUtils.substringAfter(string, URL_HEADER);

	byte[] byteArray = Base64.base64ToByteArray(stringNew);
	ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);

	ObjectInputStream ois = new ObjectInputStream(bis);

	URL url = null;
	try {
	    url = (URL) ois.readObject();
	    return url;
	} finally {
	    if (ois != null) {
		ois.close();
	    }
	}
    }

}
