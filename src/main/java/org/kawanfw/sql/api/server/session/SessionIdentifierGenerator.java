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
package org.kawanfw.sql.api.server.session;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * 
 * Session id generator with 26 long strings.
 * <br>
 * Stolen from http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class SessionIdentifierGenerator {

    private SecureRandom random = new SecureRandom();

    /**
     * Returns the next session id using a {@code SecureRandom}
     * @return the next session id using a {@code SecureRandom}
     */
    public String nextSessionId() {
      return new BigInteger(130, random).toString(32);
    }
    
}
