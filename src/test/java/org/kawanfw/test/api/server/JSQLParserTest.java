/**
 *
 */
package org.kawanfw.test.api.server;

import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Commit;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;

/**
 * @author Nicolas de Pomereu
 *
 */
public class JSQLParserTest {

    /**
     *
     */
    public JSQLParserTest() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

	Statement statement = CCJSqlParserUtil.parse("DROP TABLE CUSTOMER");
	TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
	List<String> tableList = tablesNamesFinder.getTableList(statement);
	System.out.println(tableList);

	if (statement instanceof Commit) {
	    Commit commit = (Commit) statement;
		System.out.println(commit.getClass());
	}
    }

    /**
     * @throws JSQLParserException
     */
    public static void selectTest() throws JSQLParserException {
	Statement statement = CCJSqlParserUtil.parse("SELECT * FROM customer where toto = 'titi' ");

	Select selectStatement = (Select) statement;
	TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
	List<String> tableList = tablesNamesFinder.getTableList(selectStatement);
	System.out.println(tableList);
    }

}
