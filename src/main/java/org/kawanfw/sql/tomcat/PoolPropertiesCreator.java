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
package org.kawanfw.sql.tomcat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.SqlTag;

/**
 * @author Nicolas de Pomereu
 * 
 *         Creates a PoolProperties from the passed Properties on constructor.
 *         <br>
 *         Uses reflection to call all setXxx methods of PoolProperties using
 *         the property names.
 * 
 */
public class PoolPropertiesCreator {

	/** Debug info */
	private static boolean DEBUG = FrameworkDebug.isSet(PoolPropertiesCreator.class);

	/** The properties */
	private Properties properties = null;

	private Class<?> theClass = null;
	private Map<String, Class<?>[]> methodNamesAndParms = null;

	private Object theObject = null;

	private String database = null;

	/**
	 * Create a PoolProperties from the passed properties
	 * 
	 * @param properties
	 * @param database
	 *            the database Name.
	 */
	public PoolPropertiesCreator(Properties properties, String database) {
		super();
		this.properties = properties;
		this.database = database;

	}

	/**
	 * Creates the PoolProperties from the properties passed on constructor.
	 * 
	 * @return a PoolProperties
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * 
	 * @throws NumberFormatException
	 *             if a numeric property is with letters
	 * @throws Exception
	 *             for all others cases
	 */
	public PoolProperties create() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			SecurityException, IllegalArgumentException, NoSuchMethodException, InvocationTargetException {

		// theClass =
		// Class.forName("org.apache.tomcat.jdbc.pool.PoolProperties");
		// theObject = theClass.newInstance();

		theClass = org.apache.tomcat.jdbc.pool.PoolProperties.class;
		theObject = new org.apache.tomcat.jdbc.pool.PoolProperties();

		Method[] allMethods = theClass.getDeclaredMethods();
		Field[] fieldsArray = theClass.getDeclaredFields();

		Set<String> fields = new HashSet<String>();

		for (Field theField : fieldsArray) {
			String fieldName = theField.getName();
			fields.add(fieldName);
		}

		methodNamesAndParms = new HashMap<String, Class<?>[]>();

		for (Method m : allMethods) {
			String methodName = m.getName();
			Class<?>[] pType = m.getParameterTypes();
			methodNamesAndParms.put(methodName, pType);
		}

		// First step: build the map httpClientParams
		for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();) {

			String propertyName = (String) e.nextElement();
			String propertyValue = properties.getProperty(propertyName);

			if (propertyValue != null) {
				propertyValue = propertyValue.trim();
			}

			propertyName = propertyName.trim();

			// Test that the property is a field of PoolProperties
			if (propertyName.startsWith(database + ".")) {

				propertyName = StringUtils.substringAfter(propertyName, database + ".");

				debug("property.name: " + propertyName);

				if (fields.contains(propertyName)) {
					try {
						callMethod(propertyName, propertyValue);
					} catch (NumberFormatException e1) {
						throw new DatabaseConfigurationException(
								"The " + propertyName + " value is not numeric: " + propertyValue);
					}
				}
				// No! Does not work because all properties in
				// server-sql.properties are not Tomcat JDBC pool properties
				// else {
				// throw new DatabaseConfigurationException("The property " +
				// propertyName +
				// " does not match a Tomcat JDBC Pool property.");
				// }
			}

		}

		PoolProperties poolProperties = (PoolProperties) theObject;
		return poolProperties;
	}

	/**
	 * Call the method corresponding to the property name with the property value.
	 * 
	 * @param propertyName
	 * @param propertyValue
	 * 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	private void callMethod(String propertyName, String propertyValue) throws SecurityException, NoSuchMethodException,
			NumberFormatException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		String theMethod = "set" + StringUtils.capitalize(propertyName);

		String propertyValueToDisplay = propertyValue;
		if (propertyName.equals("password")) {
			propertyValueToDisplay = TomcatStarter.MASKED_PASSWORD;
		}

		Class<?>[] pType = methodNamesAndParms.get(theMethod);

		// if (pType[0] == String.class) {
		// System.out.println(SqlTag.SQL_PRODUCT_START + "poolProperties." +
		// theMethod
		// + "(\"" + propertyValueToDisplay + "\")");
		// } else {
		// System.out.println(SqlTag.SQL_PRODUCT_START + "poolProperties." +
		// theMethod
		// + "(" + propertyValueToDisplay + ")");
		// }

		System.out.println(SqlTag.SQL_PRODUCT_START + "  -> " + propertyName + " = " + propertyValueToDisplay);

		// Invoke the method
		Method main = theClass.getDeclaredMethod(theMethod, pType);

		// if (argTypes[i] == Connection.class) {
		if (pType[0] == long.class) {
			main.invoke(theObject, Long.parseLong(propertyValue));
		} else if (pType[0] == String.class) {
			main.invoke(theObject, propertyValue);
		} else if (pType[0] == boolean.class) {
			main.invoke(theObject, Boolean.parseBoolean(propertyValue));
		} else if (pType[0] == int.class) {
			main.invoke(theObject, Integer.parseInt(propertyValue));
		} else {
			throw new DatabaseConfigurationException("Invalid Connection Pool property: " + propertyName);
		}

	}

	/**
	 * Print debug info
	 * 
	 * @param s
	 */

	private void debug(String s) {
		if (DEBUG)
			System.out.println(new Date() + " " + s);
	}

}
