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
package org.kawanfw.sql.servlet.sql;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * Utility classes for DbVendorManager
 * 
 * @author Nicolas de Pomereu
 *
 */
public class DbEngineManagerUtil {

    /**
     * Protected constructor, no instanciation
     */
    protected DbEngineManagerUtil() {
    }

    /**
     * Says if the SQL order contains a word surrounded with spaces
     * 
     * @param sqlOrder
     *            the SQL order
     * @param word
     *            the word to contain surrounded by spaces
     * @return true if the SQL order contains the word surrounded with spaces
     */
    public static boolean containsWord(String sqlOrder, String word) {
	String s = sqlOrder;

	s = s.replace('\t', ' ');

	if (s.toLowerCase().contains(" " + word.toLowerCase() + " ")) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Remove ";" from trailing SQL order
     * 
     * @param sqlOrder
     * @return sqlOrder without trailing ";"
     */
    public static String removeSemicolon(String sqlOrder) {
	while (sqlOrder.trim().endsWith(";")) {
	    sqlOrder = sqlOrder.trim();
	    sqlOrder = StringUtils.removeEnd(sqlOrder, ";");
	}
	return sqlOrder;
    }

}
