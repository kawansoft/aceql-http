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
package org.kawanfw.sql.api.server.util;

import java.net.InetAddress;
import java.net.NetworkInterface;

import javax.servlet.http.HttpServletRequest;

import org.kawanfw.sql.servlet.RequestInfoStore;

/**
 * 
 * Gets some server info, including info extracted from <code>HttpServletRequest</code> at server startup.
 * Includes:
 * <ul>
 * <li>Computer Hostname.</li>
 * <li>Computer MAC Address.</li>
 * <li>Computer IP Address.</li>
 * <li>Server URL (without the port) in <code>http(s)://www.acme.org</code> format.</li>
 * <li>Server scheme.</li>
 * <li>Server port.</li>
 * </ul>
 * 
 * @author Nicolas de Pomereu.
 */
public class ServerInfo {
    
    public static final String UNKNOWN_HOSTNAME = "unknown_hostname";
    public static final String UNKNOWN_IP_ADDRESS = "unknown_ip_address";
    public static final String UNKNOWN_MAC_ADDRESS = "unknown_mac_address";

    /** The hostname */
    private static String hostName = null;

    /** The MAC address */
    private static String macAddress = null;

    /** The IP address */
    private static String ipAddress = null;

    /**
     * Protected
     */
    protected ServerInfo() {
    }

    /**
     * Returns the computer name (Hostname).
     * 
     * @return the name or <b><code>unknown_hostname</code></b> if the name cannot be found
     */

    public static String getHostname() {
	try {
	    if (hostName == null) {
		final InetAddress addr = InetAddress.getLocalHost();
		hostName = new String(addr.getHostName());

		if (hostName == null) {
		    hostName = UNKNOWN_HOSTNAME;
		}
	    }
	} catch (final Exception e) {
	    hostName = UNKNOWN_HOSTNAME;
	    e.printStackTrace(System.out);
	}

	return hostName;
    }

    /**
     * Returns the computer IP address in 192.168.1.146 format.
     * 
     * @return the name or <b><code>unknown_ip_address</code></b> if the IP address cannot
     *         be found
     */

    public static String getIpAddress() {
	try {
	    if (ipAddress == null) {
		InetAddress ip = InetAddress.getLocalHost();

		ipAddress = ip.getHostAddress();
	    }
	} catch (Exception e) {
	    ipAddress = UNKNOWN_IP_ADDRESS;
	    e.printStackTrace(System.out);
	}

	return ipAddress;
    }

    /**
     * Returns the computer MAC address in 5C-26-0A-88-4E-DA format.
     * 
     * @return the name or <b><code>unknown_mac_address</code></b> if the MAC address cannot
     *         be found
     */

    public static String getMacAddress() {
	try {
	    if (macAddress == null) {
		InetAddress ip = InetAddress.getLocalHost();

		NetworkInterface network = NetworkInterface
			.getByInetAddress(ip);

		if (network == null) {
		    macAddress = UNKNOWN_MAC_ADDRESS;
		    return macAddress;
		}

		byte[] mac = network.getHardwareAddress();

		if (mac == null) {
		    macAddress = UNKNOWN_MAC_ADDRESS;
		    return macAddress;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
		    sb.append(String.format("%02X%s", mac[i],
			    (i < mac.length - 1) ? "-" : ""));
		}

		macAddress = sb.toString();
	    }
	} catch (Exception e) {
	    macAddress = UNKNOWN_MAC_ADDRESS;
	    e.printStackTrace(System.out);
	}

	return macAddress;
    }

    /**
     * Server URL (without the port) in <code>http(s)://www.acme.org</code> format.
     * <br><br>
     * Info extracted from <code>HttpServletRequest</code> at each request
     * 
     * @return Server URL (without the port) 
     */
    public static String getServerUrl() {
	
	HttpServletRequest httpServletRequest = RequestInfoStore.getHttpServletRequest();
	
	String serverName = httpServletRequest.getServerName();
	String scheme = httpServletRequest.getScheme();
	
	if (scheme == null || serverName == null) {
	    return null;
	}
	
	String hostUrl = scheme + "://" + serverName;
	return hostUrl;	
    }

    /**
     * Returns the name of the scheme used to make this request, for example,
     * http, https, or ftp. Different schemes have different rules for
     * constructing URLs, as noted in RFC 1738.
     * <br><br>
     * Info extracted from <code>HttpServletRequest</code> at each request
     * 
     * @return a String containing the name of the scheme used to make this
     *          request
     */
    public static String getScheme() {
	HttpServletRequest httpServletRequest = RequestInfoStore.getHttpServletRequest();
	return httpServletRequest.getScheme();
    }

    /**
     * Returns the host name of the server to which the request was sent. It is
     * the value of the part before ":" in the Host header value, if any, or the
     * resolved server name, or the server IP address.
     * <br><br>
     * Info extracted from <code>HttpServletRequest</code> at each request
     * 
     * @return a String containing the name of the server
     */
    public static String getServerName() {
	HttpServletRequest httpServletRequest = RequestInfoStore.getHttpServletRequest();
	return httpServletRequest.getServerName();
    }

    /**
     * Returns the port number to which the request was sent. It is the value of
     * the part after ":" in the Host header value, if any, or the server port
     * where the client connection was accepted on.
     * <br><br>
     * Info extracted from <code>HttpServletRequest</code> at each request
     *      
     * @return an integer specifying the port number
     */
    public static int getServerPort() {
	HttpServletRequest httpServletRequest = RequestInfoStore.getHttpServletRequest();
	return httpServletRequest.getServerPort();
    }
    
    public static void main(String[] args) throws Exception {
	System.out.println("getHostname()  : " + getHostname());
	System.out.println("getIpAddres()  : " + getIpAddress());
	System.out.println("getMacAddress(): " + getMacAddress());
    }

}
