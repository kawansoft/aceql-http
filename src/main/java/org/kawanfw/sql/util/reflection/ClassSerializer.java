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
package org.kawanfw.sql.util.reflection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.kawanfw.sql.util.Base64;

/**
 *
 */

/**
 * @author Nicolas de Pomereu
 *
 *         Allows to serialize any serializable class instance to base64 string
 *         with serialization and to get back the instance with deserialization.
 *         <br>
 *         <br>
 *         The {@link #toPropertyValue(Object)} allows to serialize a
 *         {@link HttpProxy} or {@link SessionParameters} instance and to pass
 *         the serialized instance as a property value to the
 *         {@link RemoteDriver}.
 *
 */
public class ClassSerializer<E> {


    /**
     * Serializes a class instance into a base64 String.
     *
     * @param element
     *            the class instance to Serialize
     * @return the base64 string containing the serialized class instance
     *
     * @throws IOException
     *             Any exception thrown by the underlying OutputStream.
     */
    public String toBase64(E element) throws IOException {

	String serializedBase64 = null;

	ByteArrayOutputStream bos = new ByteArrayOutputStream();

	try (ObjectOutputStream oos = new ObjectOutputStream(bos);) {

	    oos.writeObject(element);
	    oos.flush();
	    byte[] byteArray = bos.toByteArray();
	    serializedBase64 = Base64.byteArrayToBase64(byteArray);

	}

	return serializedBase64;
    }

    /**
     * Deserializes a class instance from a Base64 String created with
     * {@link #toBase64(Object)}
     *
     * @param serializedBase64
     *            the Base64 serialized class
     * @return the deserialized class instance
     *
     * @throws IOException
     *             Any of the usual Input/Output related exceptions.
     * @throws ClassNotFoundException
     *             Class of a serialized object cannot be found.
     */
    @SuppressWarnings("unchecked")
    public E fromBase64(String serializedBase64)
	    throws IOException, ClassNotFoundException {

	E element = null;

	byte[] byteArray = Base64.base64ToByteArray(serializedBase64);

	ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);

	try (ObjectInputStream ois = new ObjectInputStream(bis);) {

	    element = (E) ois.readObject();
	    return element;
	}
    }

    /**
     * Returns a clean serialized Base64 representation of the class instance
     * that can be passed as a property value to the {@code RemoteDriver} that
     * will be able to deserialize all properties of the class instance and add
     * them as properties values.
     *
     * @param element
     *            the class instance to pass
     *
     * @return a clean serialized Base64 representation of the class instance
     *         that can be passed as a property value to the
     *         {@code RemoteDriver}.
     */

    public static String toPropertyValue(Object element) {

	ClassSerializer<Object> classeStorer = new ClassSerializer<Object>();
	String serializedBase64 = null;

	try {
	    serializedBase64 = classeStorer.toBase64(element);
	} catch (IOException e) {
	    throw new IllegalArgumentException(e);
	}

	return serializedBase64;
    }

}
