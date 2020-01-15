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
package com.aceql.client.jdbc.http;

import java.sql.SQLException;

/**
 * Wrapper class for Exceptions thrown on client side or server side.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class AceQLException extends SQLException {

    private static final long serialVersionUID = 1L;

    private int httpStatusCode;
    private String remoteStackTrace;

    /**
     * Builds an AceQLException that wraps/traps an Exception.
     * 
     * @param reason
     *            the error message
     * @param vendorCode
     *            The error type:
     *            <ul>
     *            <li>0 for local Exception.</li>
     *            <li>1 for JDBC Driver Exception on the server.</li>
     *            <li>2 for AceQL Exception on the server.</li>
     *            <li>3 for AceQL Security Exception on the server.</li>
     *            <li>4 for AceQL failure.</li>
     *            </ul>
     * @param cause
     *            the wrapped/trapped Exception
     * @param remoteStackTrace
     *            the stack trace in case for remote Exception
     * @param httpStatusCode
     *            the http status code
     */
    public AceQLException(String reason, int vendorCode, Throwable cause,
	    String remoteStackTrace, int httpStatusCode) {
	super(reason, null, vendorCode, cause);
	this.remoteStackTrace = remoteStackTrace;
	this.httpStatusCode = httpStatusCode;
    }

    /**
     * Returns the http status code associated to the Exception
     * 
     * @return the http status code associated to the Exception
     */
    public int getHttpStatusCode() {
	return httpStatusCode;
    }

    /**
     * Returns the stack trace of the Exception thrown on server side
     * 
     * @return the stack trace of the Exception thrown on server side
     */
    public String getRemoteStackTrace() {
	return remoteStackTrace;
    }

}
