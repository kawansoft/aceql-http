/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP                                     
 * Copyright (C) 2021,  KawanSoft SAS
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
package org.kawanfw.test.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * @author Nicolas de Pomereu
 *
 */
public class PreparedStatementNormalizerTest {

    public static String CR_LF = System.getProperty("line.separator");
    
    /**
     * @param args
     */
    public static void main(String[] args) {
//	String sql = "    SELECT 	*         " + CR_LF + " from     my_table 	where      my_colum   =   ? ";
//	System.out.println("sql           : " + sql);
//	System.out.println("normalized sql: " + PreparedStatementNormalizer.getNormalizedText(sql));
//	System.out.println();
//	System.out.println();
	
	String str = "This is a string that will be highlighted when your '.  1 regular  2 expression  3 .'  text '.  4 regular  5 expression  6 .'   matches something.";
	System.out.println(str);
	System.out.println();
	enhancedStringTokenizer(str);
    }

    private static String enhancedStringTokenizer(String sql) {
	Objects.requireNonNull(sql, "sql cannot be null!");
	sql = sql.replace("''", "**aceql_quotes**");
	
	List <String> tokens = new ArrayList<>();
	StringTokenizer stringTokenizer = new StringTokenizer(sql, "'", false);

	while (stringTokenizer.hasMoreElements()) {
	    tokens.add(stringTokenizer.nextToken().trim());
	}
	
	for (int i = 0; i < tokens.size(); i++) {
	    System.out.println(i + ": " + tokens.get(i));
	}
	
	return null;
    }
    
    

}
