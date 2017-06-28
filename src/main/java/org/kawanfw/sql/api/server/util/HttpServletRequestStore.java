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

import javax.servlet.http.HttpServletRequest;

import org.kawanfw.sql.servlet.RequestInfoStore;


/**
 * Allows to retrieve the {@code HttpServletRequest} instance corresponding to the client request.
 * <p>
 * {@code HttpServletRequest} most used methods for security are directly delegated by convenience:
 * <ul>
 * <li>{@code getRemoteAddr()}</li>
 * <li>{@code getRemoteHost()}</li>
 * <li>{@code isSecure()}</li>
 * </ul>
 * 
 * @author Nicolas de Pomereu
 */

public class HttpServletRequestStore {

    /** The http s request */
    private HttpServletRequest httpServletRequest = null;

    /**
     * Constructor
     */
    public HttpServletRequestStore() {
	httpServletRequest = RequestInfoStore.getHttpServletRequest();
    }

    /**
     * Returns {@code HttpServletRequest.getRemoteAddr()} for this client request
     * @return {@code HttpServletRequest.getRemoteAddr()} for this client request
     */
    public String getRemoteAddr() {
	
	if (httpServletRequest == null) {
	    return null;
	}
	return httpServletRequest.getRemoteAddr();
    }
    
    /**
     * Returns {@code HttpServletRequest.getRemoteHost()} for this client request
     * @return {@code HttpServletRequest.getRemoteHost()} for this client request
     */    
    public String getRemoteHost() {
	if (httpServletRequest == null) {
	    return null;
	}
	
	return httpServletRequest.getRemoteHost();
    }    
    
    /**
     * Returns {@code HttpServletRequest.isSecure()} for this client request
     * @return {@code HttpServletRequest.isSecure()} for this client request
     */   
    public boolean isSecure() {
	
	if (httpServletRequest == null) {
	    return false;
	}
	
	return httpServletRequest.isSecure();
    }    
    
    /**
     * Returns the  {@code HttpServletRequest} instance for this client request
     * @return the  {@code HttpServletRequest} instance for this client request
     */
    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }
    
}
