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
package org.kawanfw.sql.servlet.sql.json_return;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

/**
 * @author Nicolas de Pomereu
 *
 */
public class JsonUtil {

    public static boolean DEFAULT_PRETTY_PRINTING = true;

    /**
     * protected
     */
    protected JsonUtil() {

    }

    /**
     * JsonGeneratorFactory getter with pretty printing on/off
     * 
     * @param prettyPrintingif
     *            true, JSON will be pretty printed
     * @return
     */
    public static JsonGeneratorFactory getJsonGeneratorFactory(
	    boolean prettyPrinting) {
	Map<String, Object> properties = new HashMap<>(1);
	if (prettyPrinting) {
	    // Putting any value sets the pretty printing to true... So test
	    // must be done
	    properties.put(JsonGenerator.PRETTY_PRINTING, prettyPrinting);
	}

	JsonGeneratorFactory jf = Json.createGeneratorFactory(properties);
	return jf;
    }

}
