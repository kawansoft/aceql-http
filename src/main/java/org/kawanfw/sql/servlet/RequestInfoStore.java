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
package org.kawanfw.sql.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Nicolas de Pomereu
 * 
 *         Stores the HttpServletRequest at each query of KawanSoft framework - Can be
 *         used by server Configurators to query the host in use. Infos are
 *         extract from <code>HttpServletRequest</code>.
 */

public class RequestInfoStore {

    /** The http serlet request */
    private static HttpServletRequest httpServletRequest = null;
    

    /**
     * Constructor to be used for query.
     */
    public RequestInfoStore() {

    }

    /**
     * Sets the HttpServletRequest in memory - This method is called by File or SQL
     * Kawansoft framework at server startup and should not be used.
     * 
     * @param theRequest
     *            the Http Servlet Request instance at server startup.
     */
     public static void init(HttpServletRequest request) {
	if (request == null) {
	    throw new IllegalArgumentException("request can not be null!");
	}

	httpServletRequest = request;
    }
     
     
    /**
     * Returns the HttpServletRequest
     * @return the HttpServletRequest
     */
    public static HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

}
