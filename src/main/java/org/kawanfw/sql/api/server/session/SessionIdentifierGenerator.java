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
package org.kawanfw.sql.api.server.session;

import java.security.SecureRandom;

/**
 * 
 * Session id generator with 26 long strings.
 * <p>
 * Uses a static {@code SecureRandom}. <br>
 * Each call to {@code nextSessionId()} calls {@code SecureRandom#nextInt(int)}.
 * <br>
 * See Open Source Edition <a href=
 * "https://github.com/kawansoft/aceql-http/blob/master/src/main/java/org/kawanfw/sql/api/server/session/SessionIdentifierGenerator.java">source
 * code</a>.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class SessionIdentifierGenerator {

    private static final String AB = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();

    /**
     * Returns the next session id using a {@code SecureRandom}
     * 
     * @return the next session id using a {@code SecureRandom}
     */
    public String nextSessionId() {
	return randomString(26);
    }

    private String randomString(int len) {
	StringBuilder sb = new StringBuilder(len);
	for (int i = 0; i < len; i++)
	    sb.append(AB.charAt(rnd.nextInt(AB.length())));
	return sb.toString();
    }

}
