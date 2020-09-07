/**
 *
 */
package org.kawanfw.sql.servlet.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.kawanfw.sql.api.util.SqlUtil;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ColumnInfoCreator {

    private static boolean DEBUG = FrameworkDebug.isSet(ColumnInfoCreator.class);

    private ResultSetMetaData meta;
    private ResultSet resultSet = null;
    private boolean isPostgreSQL;

    private List<Integer> columnTypeList = new Vector<Integer>();
    private List<String> columnTypeNameList = new Vector<String>();
    private List<String> columnNameList = new Vector<String>();
    private List<String> columnTableList = new Vector<String>();

    private Map<String, Integer> mapColumnNames = new LinkedHashMap<String, Integer>();

    /**
     * Constructor.
     * @param resultSet
     * @throws SQLException
     */
    public ColumnInfoCreator(ResultSet resultSet) throws SQLException {
	this.resultSet = resultSet;
	this.meta = resultSet.getMetaData();

	String productName = ResultSetWriterUtil.getDatabaseProductName(resultSet);
	this.isPostgreSQL = productName.equals(SqlUtil.POSTGRESQL) ? true : false;

	create();
    }

    /**
     * Create all the necessary column info.
     * @throws SQLException
     */
    public void create() throws SQLException {
	int cols = meta.getColumnCount();
	// Loop on Columns
	for (int i = 1; i <= cols; i++) {
	    columnTypeList.add(meta.getColumnType(i));
	    columnNameList.add(meta.getColumnName(i));
	    columnTypeNameList.add(meta.getColumnTypeName(i));

	    if (isPostgreSQL) {
		columnTableList.add(PostgreSqlUtil.getTableName(resultSet, i));
	    } else {
		columnTableList.add(meta.getTableName(i));
	    }

	    debug("");
	    debug("meta.getColumnType(" + i + ")    : " + meta.getColumnType(i));
	    debug("meta.getColumnTypeName(" + i + "): " + meta.getColumnTypeName(i));
	    debug("meta.getColumnName(" + i + ")    : " + meta.getColumnName(i));
	    debug("meta.getTableName(" + i + ")     : " + meta.getTableName(i));
	}

	// Ok, dump the column Map<String, Integer> == (Column name, column
	// pos starting 9)
	Map<String, Integer> mapColumnNames = new LinkedHashMap<String, Integer>();

	for (int i = 0; i < columnNameList.size(); i++) {
	    mapColumnNames.put(columnNameList.get(i), i);
	}

    }

    public List<Integer> getColumnTypeList() {
        return columnTypeList;
    }

    public List<String> getColumnTypeNameList() {
        return columnTypeNameList;
    }

    public List<String> getColumnNameList() {
        return columnNameList;
    }

    public List<String> getColumnTableList() {
        return columnTableList;
    }

    public Map<String, Integer> getMapColumnNames() {
        return mapColumnNames;
    }

    /**
     * @param s
     */

    protected void debug(String s) {
	if (DEBUG) {
	    // System.out.println(new Date() + " " + s);
	    System.out.println(s);
	}
    }

}
