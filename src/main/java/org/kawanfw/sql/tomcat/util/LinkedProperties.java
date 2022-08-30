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
package org.kawanfw.sql.tomcat.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author Nicolas de Pomereu
 * 
 *         Allows to read a properties with property names ordered on insertion
 *         order passed to constructor. (Just for reading, ordered store is not
 *         supported.)
 */

public class LinkedProperties extends Properties {

    /**
     * Generated serial ID
     */
    private static final long serialVersionUID = 6588861237819163772L;

    /** The ordered set of properties */
    private Set<String> orderedPropNames = new LinkedHashSet<String>();

    /**
     * Constructor
     * 
     * @param orderedPropNames
     *            the ordered properties names
     */
    public LinkedProperties(Set<String> orderedPropNames) {
	this.orderedPropNames = orderedPropNames;
    }

    /**
     * Returns a set of keys in this property list where the key and its
     * corresponding value are strings, including distinct keys in the default
     * property list if a key of the same name has not already been found from
     * the main properties list. Properties whose key or value is not of type
     * <tt>String</tt> are omitted.
     * <p>
     * The returned set is not backed by the <tt>Properties</tt> object. Changes
     * to this <tt>Properties</tt> are not reflected in the set, or vice versa.
     *
     * @return a set of keys in this property list where the key and its
     *         corresponding value are strings, including the keys in the
     *         default property list.
     * @see java.util.Properties#defaults
     */
    @Override
    public Set<String> stringPropertyNames() {

	Set<String> orderedPropNamesNew = new LinkedHashSet<String>();

	for (Iterator<String> iterator = orderedPropNames.iterator(); iterator
		.hasNext();) {
	    String propertyName = (String) iterator.next();

	    if (super.containsKey(propertyName)) {
		orderedPropNamesNew.add(propertyName);
	    }
	}

	return orderedPropNamesNew;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Properties#propertyNames()
     */
    @Override
    public Enumeration<?> propertyNames() {

	Set<String> orderedPropNamesNew = new LinkedHashSet<String>();

	for (Iterator<String> iterator = orderedPropNames.iterator(); iterator
		.hasNext();) {
	    String propertyName = (String) iterator.next();

	    if (super.containsKey(propertyName)) {
		orderedPropNamesNew.add(propertyName);
	    }
	}

	return Collections.enumeration(orderedPropNamesNew);

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Hashtable#keys()
     */
    @Override
    public synchronized Enumeration<Object> keys() {

	Set<Object> set = this.keySet();
	return Collections.enumeration(set);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Hashtable#keySet()
     */
    @Override
    public Set<Object> keySet() {

	Set<Object> set = new LinkedHashSet<Object>();

	for (Iterator<?> iterator = orderedPropNames.iterator(); iterator
		.hasNext();) {
	    Object obj = (Object) iterator.next();
	    set.add(obj);
	}

	return set;
    }

    /**
     * Returns the properties name in the order of their position in a file.
     * 
     * @param fileProperties
     *            the file containing the properties
     * @return a Set of Strings backed by a LinkedHashSet ordererd by the
     *         property position in the file
     * @throws IOException
     */
    public static Set<String> getLinkedPropertiesName(File fileProperties)
	    throws IOException {

	Set<String> linkedPropertiesName = new LinkedHashSet<String>();

	try (BufferedReader bufferedReader = new BufferedReader(
		new FileReader(fileProperties));) {

	    String line = null;
	    while ((line = bufferedReader.readLine()) != null) {

		if (line.isEmpty()) {
		    continue;
		}

		if (line.startsWith("#")) {
		    continue;
		}

		if (line.startsWith("!")) {
		    continue;
		}

		if (!line.contains("=")) {
		    continue;
		}

		line = StringUtils.substringBefore(line, "=");

		line = line.trim();
		linkedPropertiesName.add(line);
	    }
	}

	return linkedPropertiesName;

    }
}
