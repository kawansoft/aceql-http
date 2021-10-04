/**
 *
 */
package org.kawanfw.test.util;

import java.util.List;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;

/**
 * @author Nicolas de Pomereu
 *
 */
public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	//System.out.println("Default Charset: " + Charset.defaultCharset());
	TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
	String statement = "SELECT _fulltext_ FROM _fulltext_";
	Statement parsedStatement = CCJSqlParserUtil.parse(statement); // Throws the Exception 
	List<String> tables = tablesNamesFinder.getTableList(parsedStatement);
	System.out.println(tables);
    }

}
