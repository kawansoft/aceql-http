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
package org.kawanfw.sql.transport.no_obfsucation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Array;

import org.kawanfw.sql.util.Base64;

/**
 * @author Nicolas de Pomereu
 *
 *         Allows to transform with serialization a java.sql.Array holder to
 *         Base64 String and vice versa.
 */
class ArrayTransporter {

    /**
     * Transforms a java.sql.Array to serialized Base 64 String.
     *
     * @param array
     *            the Array holder to transport
     * @return a serialized array holder in base 64 format
     * @throws IOException
     */
    public String toBase64(Array array) throws IOException {

	ByteArrayOutputStream bos = new ByteArrayOutputStream();

	ObjectOutputStream oos = null;
	try {
	    oos = new ObjectOutputStream(bos);
	    oos.writeObject(array);
	    oos.flush();

	    byte[] byteArray = bos.toByteArray();
	    String base64 = Base64.byteArrayToBase64(byteArray);
	    return base64;
	} finally {
	    if (oos != null) {
		oos.close();
	    }
	}
    }

    /**
     * Transforms a serialized Base 64 String to a java.sql.Array.
     *
     * @param s
     *            a serialized java.sql.Array in Base64 format
     * @return the rebuilt java.sql.Array
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Array fromBase64(String s)
	    throws IOException, ClassNotFoundException {

	byte[] byteArray = Base64.base64ToByteArray(s);
	ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);

	ObjectInputStream ois = new ObjectInputStream(bis);

	Array array = null;
	try {
	    array = (Array) ois.readObject();
	    return array;
	} finally {
	    if (ois != null) {
		ois.close();
	    }
	}
    }

}
