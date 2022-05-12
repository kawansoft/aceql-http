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

import org.junit.Assert;
import org.kawanfw.sql.api.server.StatementNormalizer;

/**
 * @author Nicolas de Pomereu
 *
 */
public class StatementNormalizerTest {

    public static String CR_LF = System.getProperty("line.separator");
    
    /**
     * @param args
     */
    public static void main(String[] args) {
	
	//String str = "This is a      " + CR_LF + "    string that         will be highlighted when your '   1 regular  2 expression  3 .'  text '.  4 regular  5 expression  6 .'   matches something.";
	final String sql1 = "    SELECT 	*         " + CR_LF + " from     my_table 	where   1 >= 2  and  my_colum   =   ? and name = 'John Doe' ";
	System.out.println("sql1: " + sql1);
	
	final String sql1Normalized = StatementNormalizer.getNormalized(sql1);
	System.out.println(sql1Normalized);
	
	System.out.println();
	System.out.println();
	
	final String sql2 = "SELECT 	 * from " + CR_LF + "    my_table  where        1>=2  and  my_colum=? and name = 'John Doe' ";
	System.out.println("sql2: " + sql2);
	final String sql2Normalized = StatementNormalizer.getNormalized(sql2);
	System.out.println(sql2Normalized);
	
	System.out.println();
	Assert.assertEquals("sql1Normalized equals sql2Normalized", sql1Normalized, sql2Normalized);
	System.out.println("sql1Normalized equals sql2Normalized!");
	
	System.out.println();
	final String sql3 = "SELECT 	 col1  , col2 from " + CR_LF + "    my_table  where      my_colum != ? ";
	System.out.println("sql3: " + sql3);
	final String sql3Normalized = StatementNormalizer.getNormalized(sql3);
	System.out.println(sql3Normalized);
	
    }




}
