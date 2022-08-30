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
package org.kawanfw.sql.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.sf.jsqlparser.statement.Block;
import net.sf.jsqlparser.statement.Commit;
import net.sf.jsqlparser.statement.DeclareStatement;
import net.sf.jsqlparser.statement.DescribeStatement;
import net.sf.jsqlparser.statement.ExplainStatement;
import net.sf.jsqlparser.statement.SetStatement;
import net.sf.jsqlparser.statement.ShowColumnsStatement;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.UseStatement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.comment.Comment;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;
import net.sf.jsqlparser.statement.values.ValuesStatement;
import net.sf.jsqlparser.util.TablesNamesFinder;

/**
 *
 * Parses a SQL statement using JSQLParser.
 * <br>
 * Allows to get the table names and the statement type.
 * @author Nicolas de Pomereu
 *
 */
public class JsqlParserWrapper {

    private Statement statement = null;
    private List<String> tables = new ArrayList<>();

    private String statementName = null;

    private boolean isDCL = false;
    private boolean isDDL = false;
    private boolean isDML = false;
    private boolean isTCL = false;

    public JsqlParserWrapper(Statement statement) {
	this.statement =Objects.requireNonNull(statement, "statement cannot be null!");

	parse();
    }

    public void parse() {

	TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
	this.tables = tablesNamesFinder.getTableList(statement);

	isDCL = false;
	isDDL = false;
	isDML = false;
	isTCL = false;

	// Split the parses in order of method to be short
	parse1();
	parse2();
	parse3();
	parse4();
	parse5();
	parse6();
    }

    /**
     *
     */
    private void parse6() {
	if (statement instanceof Upsert) {
	    statementName = "UPSERT";
	    isDML = true;
	}
	else if (statement instanceof UseStatement) {
	    statementName = "USE";
	    isDML = true;
	}
	else if (statement instanceof ValuesStatement) {
	    statementName = "VALUES";
	    isDML = true;
	}
	else {
	    // Can not extract the statement
	    statementName = null;
	}
    }

    /**
     *
     */
    private void parse5() {
	if (statement instanceof SetStatement) {
	    statementName = "SET";
	    isDML = true;
	}
	else if (statement instanceof ShowColumnsStatement) {
	    statementName = "SHOW COLUMNS";
	    isDML = true;
	}
	else if (statement instanceof Truncate) {
	    statementName = "TRUNCATE";
	    isDDL = true;
	}
	else if (statement instanceof Update) {
	    statementName = "UPDATE";
	    isDML = true;
	}
    }

    /**
     *
     */
    private void parse4() {
	if (statement instanceof ExplainStatement) {
	    statementName = "EXPLAIN";
	    isDML = true;
	}
	else if (statement instanceof Insert) {
	    statementName = "INSERT";
	    isDML = true;
	}
	else if (statement instanceof Merge) {
	    statementName = "MERGE";
	    isDML = true;
	}
	else if (statement instanceof Replace) {
	    statementName = "REPLACE";
	    isDML = true;
	}
	else if (statement instanceof Select) {
	    statementName = "SELECT";
	    isDML = true;
	}
    }

    /**
     *
     */
    private void parse3() {
	if (statement instanceof Delete) {
	    statementName = "DELETE";
	    isDML = true;
	}
	else if (statement instanceof DescribeStatement) {
	    statementName = "DESCRIBE";
	    isDML = true;
	}
	else if (statement instanceof Drop) {
	    statementName = "DROP";
	    isDDL = true;
	}
	else if (statement instanceof Execute) {
	    statementName = "EXECUTE";
	    isDML = true;
	}
    }

    /**
     *
     */
    private void parse2() {
	if (statement instanceof CreateIndex) {
	    statementName = "CREATE";
	    isDDL = true;
	}
	else if (statement instanceof CreateTable) {
	    statementName = "CREATE";
	    isDDL = true;
	}
	else if (statement instanceof CreateView) {
	    statementName = "CREATE";
	    isDDL = true;
	}
	else if (statement instanceof DeclareStatement) {
	    statementName = "DECLARE";
	    isDML = true;
	}
    }

    /**
     *
     */
    private void parse1() {
	if (statement instanceof Alter) {
	    statementName = "ALTER";
	    isDDL = true;
	}
	else if (statement instanceof AlterView) {
	    statementName = "ALTER";
	    isDDL = true;
	}
	else if (statement instanceof Block) {
	    statementName = "BLOCK";
	    isDML = true;
	}
	else if (statement instanceof Comment) {
	    statementName = "COMMENT";
	    isDDL = true;
	}
	else if (statement instanceof Commit) {
	    statementName = "COMMIT";
	    isTCL = true;
	}
    }

    public List<String> getTables() {
        return tables;
    }

    public String getStatementName() {
        return statementName;
    }

    public boolean isDCL() {
        return isDCL;
    }

    public boolean isDDL() {
        return isDDL;
    }

    public boolean isDML() {
        return isDML;
    }

    public boolean isTCL() {
        return isTCL;
    }

}
