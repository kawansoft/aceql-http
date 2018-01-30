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
package org.kawanfw.test.api.server;

import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;
import org.kawanfw.sql.api.server.StatementAnalyzer;

public class StatementAnalyzerTest {

    @Test
    public void test() {
	insertTest();
	selectTest();
	updateTest();
	deleteTest();

	ddlWithCommentsAndSemiColumns();

	dml();
    }

    /**
     * @throws IllegalArgumentException
     */
    private void insertTest() throws IllegalArgumentException {
	String sql = "INSERT INTO ORDERLOG VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

	StatementAnalyzer stAnalyzer = new StatementAnalyzer(sql,
		new Vector<Object>());
	ddlWithNoCommentsNoSemiColumns(stAnalyzer);

	System.out.println(stAnalyzer.getTableNameFromDmlStatement());
	Assert.assertEquals("orderlog",
		stAnalyzer.getTableNameFromDmlStatement().toLowerCase());

	System.out.println("stAnalyzer.isInsert(): " + stAnalyzer.isInsert());
	Assert.assertEquals(true, stAnalyzer.isInsert());

	System.out.println("stAnalyzer.getStatementType(): "
		+ stAnalyzer.getStatementType());
	Assert.assertEquals("insert",
		stAnalyzer.getStatementType().toLowerCase());

    }

    /**
     * @throws IllegalArgumentException
     */
    private void selectTest() throws IllegalArgumentException {
	String sql = "SELECT * FROM CUSTOMER WHERE CUSTOMER_ID >= ?";

	StatementAnalyzer stAnalyzer = new StatementAnalyzer(sql,
		new Vector<Object>());
	ddlWithNoCommentsNoSemiColumns(stAnalyzer);

	System.out.println(stAnalyzer.getTableNameFromDmlStatement());
	Assert.assertEquals("customer",
		stAnalyzer.getTableNameFromDmlStatement().toLowerCase());

	System.out.println("stAnalyzer.isSelect(): " + stAnalyzer.isSelect());
	Assert.assertEquals(true, stAnalyzer.isSelect());

	System.out.println("stAnalyzer.getStatementType(): "
		+ stAnalyzer.getStatementType());
	Assert.assertEquals("select",
		stAnalyzer.getStatementType().toLowerCase());

    }

    /**
     * @throws IllegalArgumentException
     */
    private void updateTest() throws IllegalArgumentException {
	String sql = "UPDATE ORDERLOG SET " + "   date_placed  = ? "
		+ " , date_shipped = ? " + " , item_cost   = ? "
		+ " , is_delivered = ? " + " , quantity     = ? "
		+ "     WHERE  customer_id = ? AND item_id = ?;";

	StatementAnalyzer stAnalyzer = new StatementAnalyzer(sql,
		new Vector<Object>());
	ddlWithNoCommentsNoSemiColumns(stAnalyzer);

	System.out.println(stAnalyzer.getTableNameFromDmlStatement());
	Assert.assertEquals("orderlog",
		stAnalyzer.getTableNameFromDmlStatement().toLowerCase());

	System.out.println("stAnalyzer.isUpdate(): " + stAnalyzer.isUpdate());
	Assert.assertEquals(true, stAnalyzer.isUpdate());

	System.out.println("stAnalyzer.getStatementType(): "
		+ stAnalyzer.getStatementType());
	Assert.assertEquals("update",
		stAnalyzer.getStatementType().toLowerCase());
    }

    /**
     * @throws IllegalArgumentException
     */
    private void deleteTest() throws IllegalArgumentException {
	String sql = "DELETE FROM ORDERLOG WHERE CUSTOMER_ID = ? AND ITEM_ID = ? ";

	StatementAnalyzer stAnalyzer = new StatementAnalyzer(sql,
		new Vector<Object>());
	ddlWithNoCommentsNoSemiColumns(stAnalyzer);

	System.out.println(stAnalyzer.getTableNameFromDmlStatement());
	Assert.assertEquals("orderlog",
		stAnalyzer.getTableNameFromDmlStatement().toLowerCase());

	System.out.println("stAnalyzer.isDelete(): " + stAnalyzer.isDelete());
	Assert.assertEquals(true, stAnalyzer.isDelete());

	System.out.println("stAnalyzer.getStatementType(): "
		+ stAnalyzer.getStatementType());
	Assert.assertEquals("delete",
		stAnalyzer.getStatementType().toLowerCase());

    }

    /**
     * Common method for Ddl With No Comments and No Semi Columns
     * 
     * @param stAnalyzer
     */
    private void ddlWithNoCommentsNoSemiColumns(StatementAnalyzer stAnalyzer) {
	System.out.println();
	System.out.println("-------------------------------------");
	System.out.println();
	System.out.println(stAnalyzer.getSql());
	System.out.println();

	System.out.println("stAnalyzer.isWithSemicolons(): "
		+ stAnalyzer.isWithSemicolons());
	Assert.assertEquals(false, stAnalyzer.isWithSemicolons());

	System.out.println(
		"stAnalyzer.isWithComments(): " + stAnalyzer.isWithComments());
	Assert.assertEquals(false, stAnalyzer.isWithComments());

	System.out.println("stAnalyzer.isDdl(): " + stAnalyzer.isDdl());
	Assert.assertEquals(false, stAnalyzer.isDdl());

	System.out.println("stAnalyzer.isDcl(): " + stAnalyzer.isDcl());
	Assert.assertEquals(false, stAnalyzer.isDcl());

	System.out.println("stAnalyzer.isDml(): " + stAnalyzer.isDml());
	Assert.assertEquals(true, stAnalyzer.isDml());
    }

    /**
     * Common method for Ddl With No Comments and No Semi Columns
     * 
     * @param stAnalyzer
     */
    private void ddlWithCommentsAndSemiColumns() {
	String sql = "INSERT INTO /* COMMENTS */ ORDERLOG VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? ); INSERT INTO ORDERLOG VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";
	StatementAnalyzer stAnalyzer = new StatementAnalyzer(sql,
		new Vector<Object>());

	System.out.println();
	System.out.println("-------------------------------------");
	System.out.println();
	System.out.println(stAnalyzer.getSql());
	System.out.println();

	System.out.println("stAnalyzer.isWithSemicolons(): "
		+ stAnalyzer.isWithSemicolons());
	Assert.assertEquals(true, stAnalyzer.isWithSemicolons());

	System.out.println(
		"stAnalyzer.isWithComments(): " + stAnalyzer.isWithComments());
	Assert.assertEquals(true, stAnalyzer.isWithComments());

	System.out.println("stAnalyzer.isDdl(): " + stAnalyzer.isDdl());
	Assert.assertEquals(false, stAnalyzer.isDdl());

	System.out.println("stAnalyzer.isDcl(): " + stAnalyzer.isDcl());
	Assert.assertEquals(false, stAnalyzer.isDcl());

	System.out.println("stAnalyzer.isDml(): " + stAnalyzer.isDml());
	Assert.assertEquals(true, stAnalyzer.isDml());
    }

    /**
     * Common method for Ddl With No Comments and No Semi Columns
     * 
     * @param stAnalyzer
     */
    private void dml() {
	String sql = "DROP TABLE CUSTOMER;";
	StatementAnalyzer stAnalyzer = new StatementAnalyzer(sql,
		new Vector<Object>());

	System.out.println();
	System.out.println("-------------------------------------");
	System.out.println();
	System.out.println(stAnalyzer.getSql());
	System.out.println();

	System.out.println("stAnalyzer.isWithSemicolons(): "
		+ stAnalyzer.isWithSemicolons());
	Assert.assertEquals(false, stAnalyzer.isWithSemicolons());

	System.out.println(
		"stAnalyzer.isWithComments(): " + stAnalyzer.isWithComments());
	Assert.assertEquals(false, stAnalyzer.isWithComments());

	System.out.println("stAnalyzer.isDdl(): " + stAnalyzer.isDdl());
	Assert.assertEquals(true, stAnalyzer.isDdl());

	System.out.println("stAnalyzer.isDcl(): " + stAnalyzer.isDcl());
	Assert.assertEquals(false, stAnalyzer.isDcl());

	System.out.println("stAnalyzer.isDml(): " + stAnalyzer.isDml());
	Assert.assertEquals(false, stAnalyzer.isDml());
    }

}
