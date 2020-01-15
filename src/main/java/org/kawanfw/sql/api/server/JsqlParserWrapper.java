/**
 *
 */
package org.kawanfw.sql.api.server;

import java.util.ArrayList;
import java.util.List;

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
 * Allows to get the table names ans the statement type.
 * @author Nicolas de Pomereu
 *
 */
class JsqlParserWrapper {

    private Statement statement = null;
    private List<String> tables = new ArrayList<>();

    private String statementName = null;

    private boolean isDCL = false;
    private boolean isDDL = false;
    private boolean isDML = false;
    private boolean isTCL = false;

    public JsqlParserWrapper(Statement statement) {
	if (statement == null) throw new NullPointerException("statement is null!");
	this.statement = statement;

	parse();
    }

    public void parse() {

	TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
	this.tables = tablesNamesFinder.getTableList(statement);

	isDCL = false;
	isDDL = false;
	isDML = false;
	isTCL = false;

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
	else if (statement instanceof CreateIndex) {
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
	else if (statement instanceof Delete) {
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
	else if (statement instanceof ExplainStatement) {
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
	else if (statement instanceof SetStatement) {
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
	else if (statement instanceof Upsert) {
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
