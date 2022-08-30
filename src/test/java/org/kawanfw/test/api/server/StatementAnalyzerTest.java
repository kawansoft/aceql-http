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
package org.kawanfw.test.api.server;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.kawanfw.sql.api.server.StatementAnalyzer;



public class StatementAnalyzerTest {

    public void test() throws IllegalArgumentException, SQLException {
	insertTest();
	selectTest();
	updateTest();
	deleteTest();

	ddlWithCommentsAndSemiColumns();

	dml();
	
	System.out.println();
	String testMessage = 
		  "Note: All majors tests are run using The Client SDK (aceql-http-client-jdbc-driver project):\r\n"
		+ "    	<groupId>com.aceql</groupId>\r\n"
		+ "    	<artifactId>aceql-http-client-jdbc-driver</artifactId>\r\n"
		+ "    	<version>x.y</version>";
	System.out.println(testMessage);
    }

    /**
     * @throws IllegalArgumentException
     * @throws SQLException
     */
    public void insertTest() throws IllegalArgumentException, SQLException {
	String sql = "INSERT INTO ORDERLOG VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

	StatementAnalyzer stAnalyzer = new StatementAnalyzer(sql,
		new Vector<Object>());
	ddlWithNoCommentsNoSemiColumns(stAnalyzer);

	System.out.println(stAnalyzer.getTables());
	List<String> tables = stAnalyzer.getTables();
	assert(! tables.isEmpty());

	assert(tables.get(0).toLowerCase().equals("orderlog"));

	System.out.println("stAnalyzer.isInsert(): " + stAnalyzer.isInsert());
	//assertEquals(true, stAnalyzer.isInsert());
	assert(stAnalyzer.isInsert());
	
	System.out.println("stAnalyzer.getStatementType(): "
		+ stAnalyzer.getStatementName());
	//assertEquals("insert",
	//	stAnalyzer.getStatementName().toLowerCase());
	assert(stAnalyzer.getStatementName().toLowerCase().equals("insert"));

    }

    /**
     * @throws IllegalArgumentException
     * @throws SQLException
     */
    public void selectTest() throws IllegalArgumentException, SQLException {
	String sql = "SELECT * FROM CUSTOMER WHERE CUSTOMER_ID >= ?";

	StatementAnalyzer stAnalyzer = new StatementAnalyzer(sql,
		new Vector<Object>());
	ddlWithNoCommentsNoSemiColumns(stAnalyzer);

	System.out.println(stAnalyzer.getTables());
	List<String> tables = stAnalyzer.getTables();
	//assertEquals(false, tables.isEmpty());
	assert(! tables.isEmpty());
	
	//assertEquals("customer", tables.get(0).toLowerCase());
	assert(tables.get(0).toLowerCase().equals("customer"));
	System.out.println("stAnalyzer.isSelect(): " + stAnalyzer.isSelect());
	//assertEquals(true, stAnalyzer.isSelect());
	assert(stAnalyzer.isSelect());

	System.out.println("stAnalyzer.getStatementType(): "
		+ stAnalyzer.getStatementName());
	//assertEquals("select", stAnalyzer.getStatementName().toLowerCase());
	assert(stAnalyzer.getStatementName().toLowerCase().equals("select"));
    }

    /**
     * @throws IllegalArgumentException
     * @throws SQLException
     */
    public void updateTest() throws IllegalArgumentException, SQLException {
	String sql = "UPDATE ORDERLOG SET " + "   date_placed  = ? "
		+ " , date_shipped = ? " + " , item_cost   = ? "
		+ " , is_delivered = ? " + " , quantity     = ? "
		+ "     WHERE  customer_id = ? AND item_id = ?";

	StatementAnalyzer stAnalyzer = new StatementAnalyzer(sql,
		new Vector<Object>());
	ddlWithNoCommentsNoSemiColumns(stAnalyzer);

	System.out.println(stAnalyzer.getTables());
	List<String> tables = stAnalyzer.getTables();
	//assertEquals(false, tables.isEmpty());
	assert(!tables.isEmpty());

	//assertEquals("orderlog", tables.get(0).toLowerCase());
	assert(tables.get(0).toLowerCase().equals("orderlog"));
	
	System.out.println("stAnalyzer.isUpdate(): " + stAnalyzer.isUpdate());
	//assertEquals(true, stAnalyzer.isUpdate());
	assert(stAnalyzer.isUpdate());

	System.out.println("stAnalyzer.getStatementType(): "
		+ stAnalyzer.getStatementName());
	//assertEquals("update",
	//	stAnalyzer.getStatementName().toLowerCase());
	assert(stAnalyzer.getStatementName().toLowerCase().equals("update"));
    }

    /**
     * @throws IllegalArgumentException
     * @throws SQLException
     */
    public void deleteTest() throws IllegalArgumentException, SQLException {
	String sql = "DELETE FROM ORDERLOG WHERE CUSTOMER_ID = ? AND ITEM_ID = ? ";

	StatementAnalyzer stAnalyzer = new StatementAnalyzer(sql,
		new Vector<Object>());
	ddlWithNoCommentsNoSemiColumns(stAnalyzer);

	System.out.println(stAnalyzer.getTables());

	List<String> tables = stAnalyzer.getTables();
	//assertEquals(false, tables.isEmpty());
	assert(!tables.isEmpty());
	
	//assertEquals("orderlog",table.toLowerCase());
	assert(tables.get(0).toLowerCase().equals("orderlog"));
	
	System.out.println("stAnalyzer.isDelete(): " + stAnalyzer.isDelete());
	//assertEquals(true, stAnalyzer.isDelete());
	assert(stAnalyzer.isDelete());

	System.out.println("stAnalyzer.getStatementType(): "
		+ stAnalyzer.getStatementName());
	//assertEquals("delete", stAnalyzer.getStatementName().toLowerCase());
	assert(stAnalyzer.getStatementName().toLowerCase().equals("delete"));
    }

    /**
     * Common method for Ddl With No Comments and No Semi Columns
     *
     * @param stAnalyzer
     */
    public void ddlWithNoCommentsNoSemiColumns(StatementAnalyzer stAnalyzer) {
	System.out.println();
	System.out.println("-------------------------------------");
	System.out.println();
	System.out.println(stAnalyzer.getSql());
	System.out.println();

	System.out.println("stAnalyzer.isWithSemicolons(): "
		+ stAnalyzer.isWithSemicolons());
	//assertEquals(false, stAnalyzer.isWithSemicolons());
	assert(!stAnalyzer.isWithSemicolons());

	System.out.println(
		"stAnalyzer.isWithComments(): " + stAnalyzer.isWithComments());
	//assertEquals(false, stAnalyzer.isWithComments());
	assert(!stAnalyzer.isWithComments());

	System.out.println("stAnalyzer.isDdl(): " + stAnalyzer.isDdl());
	//assertEquals(false, stAnalyzer.isDdl());
	assert(!stAnalyzer.isDdl());

	System.out.println("stAnalyzer.isDcl(): " + stAnalyzer.isDcl());
	//assertEquals(false, stAnalyzer.isDcl());
	assert(!stAnalyzer.isDcl());
	
	System.out.println("stAnalyzer.isDml(): " + stAnalyzer.isDml());
	//assertEquals(true, stAnalyzer.isDml());
	assert(stAnalyzer.isDml());
    }

    /**
     * Common method for Ddl With No Comments and No Semi Columns
     *
     * @param stAnalyzer
     * @throws SQLException
     */
    public void ddlWithCommentsAndSemiColumns() throws SQLException {
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
	//assertEquals(true, stAnalyzer.isWithSemicolons());
	assert(stAnalyzer.isWithSemicolons());

	System.out.println(
		"stAnalyzer.isWithComments(): " + stAnalyzer.isWithComments());
	//assertEquals(true, stAnalyzer.isWithComments());
	assert(stAnalyzer.isWithComments());
	
	/*
	System.out.println("stAnalyzer.isDdl(): " + stAnalyzer.isDdl());
	assertEquals(false, stAnalyzer.isDdl());

	System.out.println("stAnalyzer.isDcl(): " + stAnalyzer.isDcl());
	assertEquals(false, stAnalyzer.isDcl());

	System.out.println("stAnalyzer.isDml(): " + stAnalyzer.isDml());
	assertEquals(true, stAnalyzer.isDml());
	*/
    }

    /**
     * Common method for Ddl With No Comments and No Semi Columns
     *
     * @param stAnalyzer
     * @throws SQLException
     */
    public void dml() throws SQLException {
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
	//assertEquals(false, stAnalyzer.isWithSemicolons());

	System.out.println(
		"stAnalyzer.isWithComments(): " + stAnalyzer.isWithComments());
	//assertEquals(false, stAnalyzer.isWithComments());

	System.out.println("stAnalyzer.isDdl(): " + stAnalyzer.isDdl());
	//assertEquals(true, stAnalyzer.isDdl());

	System.out.println("stAnalyzer.isDcl(): " + stAnalyzer.isDcl());
	//assertEquals(false, stAnalyzer.isDcl());

	System.out.println("stAnalyzer.isDml(): " + stAnalyzer.isDml());
	//assertEquals(false, stAnalyzer.isDml());
    }

}
