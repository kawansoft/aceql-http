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

import java.io.Serializable;
import java.sql.RowId;
import java.util.Arrays;

/**
 * Virtual RowId that contains the server info.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class RowIdHttp implements RowId, Serializable {

    /**
     * Generated serial version Id
     */
    private static final long serialVersionUID = -1145263653472786404L;
    private int hashCode = 0;
    private byte[] rowIdBytes = null;

    /**
     * Constructor
     * 
     * @param rowIdBytes
     */
    public RowIdHttp(int hashCode, byte[] rowIdBytes) {

	this.hashCode = hashCode;
	this.rowIdBytes = rowIdBytes;
    }

    @Override
    public byte[] getBytes() {
	return rowIdBytes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	return this.hashCode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	RowId other = (RowId) obj;
	// if ( hashCode() != other.hashCode())
	// return false;
	if (!Arrays.equals(rowIdBytes, other.getBytes()))
	    return false;
	return true;
    }

}
