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
package org.kawanfw.sql.transport.no_obfsucation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.RowId;

import org.kawanfw.sql.util.Base64;

/**
 * @author Nicolas de Pomereu
 *
 */
public class RowIdTransporter {

    /**
     * Constructor
     */
    public RowIdTransporter() {

    }

    /**
     * Transforms a RowId to serialized Base 64 String.
     * 
     * @param rowId
     *            the RowId to transport
     * @return a serialized RowId in base 64 format
     * @throws IOException
     */
    public String toBase64(RowId rowId) throws IOException {

	ByteArrayOutputStream bos = new ByteArrayOutputStream();

	ObjectOutputStream oos = null;
	try {
	    oos = new ObjectOutputStream(bos);
	    oos.writeObject(rowId);
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
    public RowId fromBase64(String s)
	    throws IOException, ClassNotFoundException {

	byte[] byteArray = Base64.base64ToByteArray(s);
	ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);

	ObjectInputStream ois = new ObjectInputStream(bis);

	RowId rowId = null;
	try {
	    rowId = (RowId) ois.readObject();
	    return rowId;
	} finally {
	    if (ois != null) {
		ois.close();
	    }
	}
    }
}
