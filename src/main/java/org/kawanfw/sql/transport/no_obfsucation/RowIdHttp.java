/*
 * Copyright (c)2022 KawanSoft S.A.S.
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-30
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
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
class RowIdHttp implements RowId, Serializable {

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

//	if (!Arrays.equals(rowIdBytes, other.getBytes()))
//	    return false;
//	return true;

	return Arrays.equals(rowIdBytes, other.getBytes());

    }

}
