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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.kawanfw.sql.api.server.session.SessionIdentifierGenerator;

/**
 * 
 */

/**
 * 
 * Token ID generator with 26 long strings. Inspired from
 * http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
 * 
 * @author Nicolas de Pomereu
 *
 */
public class SessionIdentifierGeneratorTest {

    /**
     * @param args
     */
    public static void main(String[] args) {

	Set<String> firstSet = new HashSet<>();

	SessionIdentifierGenerator sessionIdentifierGenerator = new SessionIdentifierGenerator();

	for (int i = 0; i < 10_000_000; i++) {

	    String s = sessionIdentifierGenerator.nextSessionId();
	    firstSet.add(s);

	    if (i % 10000 == 0)
		System.out.println(new Date() + " " + i);
	}

	System.out.println(new Date() + " Set Done!");

	for (int i = 0; i < 10_000_000; i++) {
	    String s = sessionIdentifierGenerator.nextSessionId();

	    if (i % 10000 == 0)
		System.out.println(new Date() + " " + i);
	    if (firstSet.contains(s)) {
		throw new IllegalArgumentException(
			"failure: string already in set: " + s);
	    }
	}

	System.out.println(new Date() + " Test Done!");

    }

}
