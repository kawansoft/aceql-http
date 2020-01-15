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
package org.kawanfw.sql.server.session.test;

import org.kawanfw.sql.api.server.session.SessionIdentifierGenerator;
import org.kawanfw.sql.api.server.session.SessionInfo;

/**
 * @author Nicolas de Pomereu
 *
 */
public class AuthSessionTest {

    /**
     * 
     */
    public AuthSessionTest() {

    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

	String username = "user1";
	String database = "my_database";

	SessionIdentifierGenerator sessionIdentifierGenerator = new SessionIdentifierGenerator();
	String sessionId = sessionIdentifierGenerator.nextSessionId();

	@SuppressWarnings("unused")
	SessionInfo SessionInfo = new SessionInfo(sessionId, username,
		database);
	System.out.println("sessionId: " + sessionId);

    }

}
