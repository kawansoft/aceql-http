/*
 * This file is part of AceQL Client SDK.
 * AceQL Client SDK: Remote JDBC access over HTTP with AceQL HTTP.                                 
 * Copyright (C) 2017,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.                                
 *                                                                               
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
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
