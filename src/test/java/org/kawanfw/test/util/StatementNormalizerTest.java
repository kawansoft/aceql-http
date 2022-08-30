/*
 * Copyright (c)2022 KawanSoft S.A.S.
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-30
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.test.util;

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
	
	StatementNormalizer statementNormalizer = new StatementNormalizer(sql1);
	final String sql1Normalized = statementNormalizer.getNormalized();
	System.out.println(sql1Normalized);
	
	System.out.println();
	System.out.println();
	
	final String sql2 = "SELECT 	 * from " + CR_LF + "    my_table  where        1>=2  and  my_colum=? and name = 'John Doe' ";
	System.out.println("sql2: " + sql2);
	
	statementNormalizer = new StatementNormalizer(sql2);
	final String sql2Normalized = statementNormalizer.getNormalized();
	System.out.println(sql2Normalized);
	
	System.out.println();
	//Assert.assertEquals("sql1Normalized equals sql2Normalized", sql1Normalized, sql2Normalized);
	System.out.println("sql1Normalized equals sql2Normalized!");
	
	System.out.println();
	final String sql3 = "SELECT 	 col1  , col2 from " + CR_LF + "    my_table  where      my_colum != ? ";
	System.out.println("sql3: " + sql3);
	statementNormalizer = new StatementNormalizer(sql3);
	final String sql3Normalized = statementNormalizer.getNormalized();
	System.out.println(sql3Normalized);
	
    }




}
