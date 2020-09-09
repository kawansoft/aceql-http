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
package org.kawanfw.sql.metadata;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;

import org.kawanfw.sql.jdbc.metadata.ArrayTransporter;
import org.kawanfw.sql.util.Tag;

/**
 * SQL Array implementation for transport between AceQL Ssrver and clients SDKs
 * @author Nicolas de Pomereu
 *
 */
public class AceQLArray implements Array {

    private static final String KAWANFW_NOT_SUPPORTED_METHOD = Tag.PRODUCT + "Method is not yet implemented.";

    private String baseTypeName;
    private int baseType;
    private String arrayAsJoin;


    /**
     * Necessary void constructor for JSON
     */
    public AceQLArray() {

    }

    public AceQLArray(Array array) throws SQLException {
	if (array == null) {
	    this.baseTypeName = "NULL";
	    this.baseType = 0;
	    this.arrayAsJoin = "NULL";
	    return;
	}

	this.baseTypeName = array.getBaseTypeName();
	this.baseType = array.getBaseType();
	String[] stringArray = (String[]) array.getArray();
	this.arrayAsJoin = ArrayTransporter.arrayToString(stringArray);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.sql.Array#getBaseTypeName()
     */
    @Override
    public String getBaseTypeName() throws SQLException {
	return baseTypeName;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.sql.Array#getBaseType()
     */
    @Override
    public int getBaseType() throws SQLException {
	return baseType;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.sql.Array#getArray()
     */
    @Override
    public Object getArray() throws SQLException {
	return ArrayTransporter.stringToStringArray(arrayAsJoin);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.sql.Array#getArray(java.util.Map)
     */
    @Override
    public Object getArray(Map<String, Class<?>> map) throws SQLException {
	throw new SQLFeatureNotSupportedException(KAWANFW_NOT_SUPPORTED_METHOD);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.sql.Array#getArray(long, int)
     */
    @Override
    public Object getArray(long index, int count) throws SQLException {
	throw new SQLFeatureNotSupportedException(KAWANFW_NOT_SUPPORTED_METHOD);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.sql.Array#getArray(long, int, java.util.Map)
     */
    @Override
    public Object getArray(long index, int count, Map<String, Class<?>> map) throws SQLException {
	throw new SQLFeatureNotSupportedException(KAWANFW_NOT_SUPPORTED_METHOD);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.sql.Array#getResultSet()
     */
    @Override
    public ResultSet getResultSet() throws SQLException {
	throw new SQLFeatureNotSupportedException(KAWANFW_NOT_SUPPORTED_METHOD);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.sql.Array#getResultSet(java.util.Map)
     */
    @Override
    public ResultSet getResultSet(Map<String, Class<?>> map) throws SQLException {
	throw new SQLFeatureNotSupportedException(KAWANFW_NOT_SUPPORTED_METHOD);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.sql.Array#getResultSet(long, int)
     */
    @Override
    public ResultSet getResultSet(long index, int count) throws SQLException {
	throw new SQLFeatureNotSupportedException(KAWANFW_NOT_SUPPORTED_METHOD);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.sql.Array#getResultSet(long, int, java.util.Map)
     */
    @Override
    public ResultSet getResultSet(long index, int count, Map<String, Class<?>> map) throws SQLException {
	throw new SQLFeatureNotSupportedException(KAWANFW_NOT_SUPPORTED_METHOD);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.sql.Array#free()
     */
    @Override
    public void free() throws SQLException {

    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "AceQLArray [baseTypeName=" + baseTypeName + ", baseType=" + baseType + ", arrayAsJoin=" + arrayAsJoin
		+ "]";
    }


}
