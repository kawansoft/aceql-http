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
     * Constructor.
     */
    public UrlTransporter() {

    }

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
     * @param s
     *            a serialized URL in Base64 format
     * @return the rebuilt URL
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public URL fromBase64(String s) throws IOException, ClassNotFoundException {

	s = StringUtils.substringAfter(s, URL_HEADER);

	byte[] byteArray = Base64.base64ToByteArray(s);
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
