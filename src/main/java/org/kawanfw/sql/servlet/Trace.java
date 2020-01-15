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
package org.kawanfw.sql.servlet;

/**
 * Trace options to ease some demos, debug, etc. Different from DEBUG
 * 
 * @author Nicolas de Pomereu
 *
 */

public class Trace {

    public static boolean TRACE_ON = false;

    /** Trace Token ID */
    public static boolean TRACE_SESSION_ID = true;

    /** Trace all Http Status on error */
    public static boolean TRACE_HTTP_STATUS = true;

    private Trace() {

    }

    public static void httpStatus(String s) {

	if (TRACE_ON && TRACE_HTTP_STATUS) {
	    System.out.println(s);
	}

    }

    public static void sessionId(String s) {

	if (TRACE_ON && TRACE_SESSION_ID) {
	    System.out.println(s);
	}
    }

}
