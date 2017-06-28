/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP                                     
 * Copyright (C) 2017,  KawanSoft SAS
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

import java.io.File;
import java.net.ConnectException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.util.SqlTag;

/**
 * Updates the Tomcat Connectors properties/values from the values defined in
 * the Server properties file.
 * 
 * @author Nicolas de Pomereu
 * 
 */
public class TomcatConnectorsUpdater {

    /** The Tomcat instance to update */
    private Tomcat tomcat = null;

    /** The properties */
    private Properties properties = null;

    /**
     * Constructor
     * 
     * @param tomcat
     *            The Tomcat instance to update
     * @param properties
     *            The properties
     */
    public TomcatConnectorsUpdater(Tomcat tomcat, Properties properties) {
	this.properties = properties;
	this.tomcat = tomcat;
    }

    /**
     * If there are some Connector properties, set them on Tomcat instance
     */
    public void setConnectorValues() {

	// Do we have to set special values to the Connector?
	Enumeration<?> enumeration = properties.propertyNames();

	if (enumeration.hasMoreElements()) {
	    System.out.println(SqlTag.SQL_PRODUCT_START
		    + " Setting Default Connector attribute values:");
	}

	while (enumeration.hasMoreElements()) {
	    String property = (String) enumeration.nextElement();

	    if (property.startsWith("connector.")) {

		String theValue = properties.getProperty(property);
		String tomcatProperty = StringUtils.substringAfter(property,
			"connector.");

		if (theValue != null && !theValue.isEmpty()) {

		    theValue = theValue.trim();

		    System.out.println(SqlTag.SQL_PRODUCT_START + "  -> "
			    + tomcatProperty + " = " + theValue);

		    tomcat.getConnector().setProperty(tomcatProperty, theValue);
		}
	    }
	}
    }

    /**
     * If there are some SSL Connector properties, set them on Tomcat instance
     * 
     * @return the setted Connector
     */
    public Connector setSslConnectorValues() throws DatabaseConfigurationException,
	    ConnectException {

	String sslConnectorSSLEnabled = properties
		.getProperty("sslConnector.SSLEnabled");

	if (sslConnectorSSLEnabled != null) {
	    sslConnectorSSLEnabled = sslConnectorSSLEnabled.trim();
	}

	Connector sslConnector = null;

	// Do we have to add an SSL Connector?
	if (sslConnectorSSLEnabled == null
		|| !sslConnectorSSLEnabled.trim().equals("true")) {
	    return sslConnector;
	}

	// Ok, set the connector properties
	sslConnector = new Connector();

	// Scheme is mandatory
	String scheme = getMandatoryPropertyValue("sslConnector.scheme");
	if (!scheme.equals("https")) {
	    throw new DatabaseConfigurationException(
		    "The property sslConnector.https value must be \"https\" in properties file. "
			    + SqlTag.PLEASE_CORRECT);
	}

	String portStr = getMandatoryPropertyValue("sslConnector.port");

	int port = -1;
	try {
	    port = Integer.parseInt(portStr);
	} catch (NumberFormatException e) {
	    throw new DatabaseConfigurationException(
		    "The property sslConnector.port is not numeric: " + portStr
			    + " in properties file.");
	}

	if (!TomcatStarterUtil.available(port)) {
	    throw new ConnectException("The port for SSL " + port
		    + " is not available for starting Web server. "
		    + SqlTag.PLEASE_CORRECT);
	}

	// Testing the keystore file
	String keyStoreFileStr = getMandatoryPropertyValue("sslConnector.keystoreFile");

	File keystoreFile = new File(keyStoreFileStr);
	if (!keystoreFile.exists()) {
	    throw new DatabaseConfigurationException(
		    "The file specified by sslConnector.keystoreFile property does not exists: "
			    + keystoreFile + ". " + SqlTag.PLEASE_CORRECT);
	}

	// Testing taht keystore & keyPass password are set
	@SuppressWarnings("unused")
	String keystorePass = getMandatoryPropertyValue("sslConnector.keystorePass");
	@SuppressWarnings("unused")
	String keyPass = getMandatoryPropertyValue("sslConnector.keyPass");

	sslConnector.setScheme(scheme);
	sslConnector.setPort(port);
	sslConnector.setSecure(true);

	// Set the SSL connector
	Enumeration<?> enumeration = properties.propertyNames();

	if (enumeration.hasMoreElements()) {
	    System.out.println(SqlTag.SQL_PRODUCT_START
		    + " Setting SSL Connector attribute values:");
	}

	while (enumeration.hasMoreElements()) {
	    String property = (String) enumeration.nextElement();

	    if (property.startsWith("sslConnector.")
		    && !property.equals("sslConnector.scheme")
		    && !property.equals("sslConnector.port")) {

		String theValue = properties.getProperty(property);
		String tomcatProperty = StringUtils.substringAfter(property,
			"sslConnector.");

		if (theValue != null && !theValue.isEmpty()) {

		    theValue = theValue.trim();
		    sslConnector.setProperty(tomcatProperty, theValue);

		    if (property.equals("sslConnector.keyPass")
			    || property.equals("sslConnector.keystorePass")) {
			theValue = TomcatStarter.MASKED_PASSWORD;
		    }

		    System.out.println(SqlTag.SQL_PRODUCT_START + "  -> "
			    + tomcatProperty + " = " + theValue);

		}
	    }
	}

	Service service = tomcat.getService();
	service.addConnector(sslConnector); // Add the connector

	return sslConnector;

    }

    /**
     * Returns the property value from a property name. Throws a
     * DatabaseConfigurationException if the property is not set
     * 
     * @param propertyName
     *            the property name to test
     * @return the property value
     * @throws DatabaseConfigurationException
     *             if the property is not set
     */

    private String getMandatoryPropertyValue(String propertyName)
	    throws DatabaseConfigurationException {
	String properteyValue = properties.getProperty(propertyName);
	if (properteyValue == null || properteyValue.isEmpty()) {
	    throw new DatabaseConfigurationException(
		    "property "
			    + propertyName
			    + " name is not defined or has empty value in properties file. "
			    + SqlTag.PLEASE_CORRECT);
	}
	properteyValue = properteyValue.trim();
	return properteyValue;
    }

}
