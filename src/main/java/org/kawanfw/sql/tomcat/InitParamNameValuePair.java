/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.tomcat;
/**
 * Defines a init parameter (name, value) pair
 * 
 * @author Nicolas de Pomereu
 *
 */
public class InitParamNameValuePair
	implements Comparable<InitParamNameValuePair> {
    private final String name;
    private final String value;
    public InitParamNameValuePair(String name, String value) {
	if (name == null) {
	    throw new IllegalArgumentException("Name may not be null");
	}
	this.name = name;
	this.value = value;
    }
    /**
     * @return the name
     */
    public String getName() {
	return name;
    }
    /**
     * @return the value
     */
    public String getValue() {
	return value;
    }
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "InitParamNameValuePair [name=" + name + ", value=" + value
		+ "]";
    }
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result + ((value == null) ? 0 : value.hashCode());
	return result;
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
	InitParamNameValuePair other = (InitParamNameValuePair) obj;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	if (value == null) {
	    if (other.value != null)
		return false;
	} else if (!value.equals(other.value))
	    return false;
	return true;
    }
    @Override
    public int compareTo(InitParamNameValuePair o) {
	return this.getName().compareToIgnoreCase(o.getName());
    }
}
