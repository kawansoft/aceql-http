/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2020,  KawanSoft SAS
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
package org.kawanfw.sql.metadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kawanfw.sql.api.util.SqlUtil;
import org.kawanfw.sql.metadata.util.MetaDataJavaUtil;
import org.kawanfw.sql.util.FrameworkDebug;

/**
 * Allows to build all DTO Objects using DatabaseMetaData wrapping.
 *
 * @author Nicolas de Pomereu
 */
public class AceQLMetaData {

    private static boolean DEBUG = FrameworkDebug
	    .isSet(AceQLMetaData.class);

    private Connection connection = null;
    private String catalog = null;
    private String schema = null;
    private Set<String> tableNamesSet = new HashSet<>();

    /**
     * Constructor.
     *
     * @param connection the SQL/JDBC constructor
     * @throws SQLException it any SQL Exception occurs
     */
    public AceQLMetaData(Connection connection) throws SQLException {
	this.connection = connection;

	List<String> tableNames = getTableNames();
	for (String tableName : tableNames) {
	    tableNamesSet.add(tableName);
	}
    }

    /**
     * Constructor.
     *
     * @param connection the SQL/JDBC constructor
     * @param catalog    a catalog name; must match the catalog name as it is stored
     *                   in this database; "" retrieves those without a catalog;
     *                   null means that the catalog name should not be used to
     *                   narrow the search
     * @param schema     a schema name; must match the schema name as it is stored
     *                   in the database; "" retrieves those without a schema; null
     *                   means that the schema name should not be used to narrow the
     *                   search
     * @throws SQLException it any SQL Exception occurs
     */
    public AceQLMetaData(Connection connection, String catalog, String schema) throws SQLException {
	this(connection);
	this.catalog = catalog;
	this.schema = schema;
    }

    /**
     * Returns the Schemas
     *
     * @return the Schemas
     * @throws SQLException
     */
    public List<String> getSchemas() throws SQLException {
	DatabaseMetaData databaseMetaData = connection.getMetaData();
	String columnLabel = "TABLE_SCHEM";
	ResultSet rs = databaseMetaData.getSchemas();
	List<String> schemas = new ArrayList<>();
	while (rs.next()) {

	    schemas.add(rs.getString(columnLabel));
	}

	return schemas;
    }

    /**
     * Returns a holder of JDBC DatabaseMetaData instance for single values.
     *
     * @return a holder of JDBC DatabaseMetaData instance for single values.
     * @throws SQLException
     */
    public JdbcDatabaseMetaData getJdbcDatabaseMetaData() throws SQLException {
	DatabaseMetaData databaseMetaData = connection.getMetaData();
	JdbcDatabaseMetaData jdbcDatabaseMetaData = new JdbcDatabaseMetaData();
	jdbcDatabaseMetaData.setDatabaseMetaDataHolder(databaseMetaData);
	return jdbcDatabaseMetaData;
    }

    /**
     * Returns the Catalogs
     *
     * @return the Catalogs
     * @throws SQLException
     */
    public List<String> getCatalogs() throws SQLException {
	DatabaseMetaData databaseMetaData = connection.getMetaData();
	ResultSet rs = databaseMetaData.getCatalogs();
	String columnLabel = "TABLE_CAT";
	List<String> schemas = new ArrayList<>();
	while (rs.next()) {

	    schemas.add(rs.getString(columnLabel));
	}
	return schemas;
    }

    /**
     * Returns the table types for the Connection
     *
     * @return the table types for the Connection
     * @throws SQLException it any SQL Exception occurs
     */
    public List<String> getTableTypes() throws SQLException {
	DatabaseMetaData databaseMetaData = connection.getMetaData();
	ResultSet rs = databaseMetaData.getTableTypes();
	List<String> tableTypes = new ArrayList<>();
	while (rs.next()) {
	    tableTypes.add(rs.getString(1));
	}
	return tableTypes;
    }

    /**
     * Returns the non-system table names for the Connection
     * @param filtertableType the table type to select VIEW, TABLE etc
     * @return the non-system table names for the Connection
     * @throws SQLException it any SQL Exception occurs
     */
    public List<String> getTableNames(String filterTableType) throws SQLException {
	DatabaseMetaData databaseMetaData = connection.getMetaData();

	// String [] types = {"TABLE","VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY","LOCAL
	// TEMPORARY", "ALIAS", "SYNONYM"};
	String[] types = { "TABLE", "VIEW", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM" };

	/**
	 * <pre>
	 * <code>
	Retrieves a description of the tables available in the given catalog.Only table descriptions matching the catalog, schema, tablename and type criteria are returned. They are ordered by TABLE_TYPE, TABLE_CAT, TABLE_SCHEM and TABLE_NAME.

	Each table description has the following columns:
	1.TABLE_CAT String => table catalog (may be null)
	2.TABLE_SCHEM String => table schema (may be null)
	3.TABLE_NAME String => table name
	4.TABLE_TYPE String => table type. Typical types are "TABLE","VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY","LOCAL TEMPORARY", "ALIAS", "SYNONYM".
	5.REMARKS String => explanatory comment on the table
	6.TYPE_CAT String => the types catalog (may be null)
	7.TYPE_SCHEM String => the types schema (may be null)
	8.TYPE_NAME String => type name (may be null)
	9.SELF_REFERENCING_COL_NAME String => name of the designated"identifier" column of a typed table (may be null)
	10.REF_GENERATION String => specifies how values in SELF_REFERENCING_COL_NAME are created. Values are"SYSTEM", "USER", "DERIVED". (may be null)
	</code>
	 * </pre>
	 */

	ResultSet rs = databaseMetaData.getTables(catalog, schema, null, types);

	List<String> tableNames = new ArrayList<>();
	while (rs.next()) {

	    String schema = rs.getString(2);
	    SqlUtil sqlUtil = new SqlUtil(connection);

	    if (sqlUtil.isPostgreSQL() && !schema.equalsIgnoreCase("public")) {
		continue;
	    }

	    if (sqlUtil.isMySQL() && schema != null) {
		continue;
	    }

	    if (sqlUtil.isSQLServer() && !schema.equalsIgnoreCase("dbo")) {
		continue;
	    }

	    if (sqlUtil.isDB2() && (schema == null || !schema.equalsIgnoreCase(databaseMetaData.getUserName()))) {
		continue;
	    }

	    if (sqlUtil.isOracle() && (schema == null || !schema.equalsIgnoreCase(databaseMetaData.getUserName()))) {
		continue;
	    }

	    String tableName = rs.getString(3);
	    String tableType = rs.getString(4);

	    if (filterTableType != null && ! filterTableType.equalsIgnoreCase(tableType)) {
		continue;
	    }

	    debug("catalog : " + rs.getString(1) + " schema : " + rs.getString(2) + " tableName: " + tableName
		    + " TABLE_TYPE: " + rs.getString(4));

	    tableName = tableName.toLowerCase();
	    tableNames.add(tableName);
	}
	return tableNames;
    }


    /**
     * Returns the non-system table names for the Connection
     *
     * @return the non-system table names for the Connection
     * @throws SQLException it any SQL Exception occurs
     */
    public List<String> getTableNames() throws SQLException {
	return getTableNames(null);

    }

    /**
     * Returns the foreign exported keys for the passed table name
     *
     * @param tableName the table name for the passed table name
     * @return the foreign exported keys
     * @throws SQLException it any SQL Exception occurs
     */
    public List<ExportedKey> getExportedKeys(String tableName) throws SQLException {
	if (tableName == null) {
	    throw new NullPointerException("tableName is null!");
	}

	if (!tableNamesSet.contains(tableName.toLowerCase())) {
	    throw new IllegalArgumentException("table does not exists: " + tableName);
	}

	/**
	 * <pre>
	<code>
	databaseMetaData.getExportedKeys( customer) 1: null                                       1.PKTABLE_CAT String => primary key table catalog (may be null)
	databaseMetaData.getExportedKeys( customer) 2: public                                     2.PKTABLE_SCHEM String => primary key table schema (may be null)
	databaseMetaData.getExportedKeys( customer) 3: customer                                   3.PKTABLE_NAME String => primary key table name
	databaseMetaData.getExportedKeys( customer) 4: customer_id                                4.PKCOLUMN_NAME String => primary key column name

	databaseMetaData.getExportedKeys( customer) 5: null                                       5.FKTABLE_CAT String => foreign key table catalog (may be null)being exported (may be null)
	databaseMetaData.getExportedKeys( customer) 6: public                                     6.FKTABLE_SCHEM String => foreign key table schema (may be null)being exported (may be null)
	databaseMetaData.getExportedKeys( customer) 7: orderlog2                                  7.FKTABLE_NAME String => foreign key table namebeing exported
	databaseMetaData.getExportedKeys( customer) 8: customer_id                                8.FKCOLUMN_NAME String => foreign key column namebeing exported

	databaseMetaData.getExportedKeys( customer) 9: 1                                          9.KEY_SEQ short => sequence number within foreign key( a valueof 1 represents the first column
	databaseMetaData.getExportedKeys( customer) 10: 3                                         10.UPDATE_RULE short => What happens toforeign key when primary is updated: ◦ importedNoAction
	databaseMetaData.getExportedKeys( customer) 11: 3					  11.DELETE_RULE short
	databaseMetaData.getExportedKeys( customer) 12: orderlog2_customer_id_fkey		  12.FK_NAME String => foreign key name (may be null)
	databaseMetaData.getExportedKeys( customer) 13: customer_pkey                             13.PK_NAME String => primary key name (may be null)

	databaseMetaData.getExportedKeys( customer) 14: 7                                         14.DEFERRABILITY short => can the evaluation of foreign keyconstraints be deferred until commit
	</code>
	 * </pre>
	 */

	DatabaseMetaData databaseMetaData = connection.getMetaData();
	ResultSet rs = databaseMetaData.getExportedKeys(catalog, schema, tableName);

	List<ExportedKey> exportedKeys = new ArrayList<>();
	while (rs.next()) {
	    ExportedKey exportedKey = new ExportedKey();
	    int i = 1;
	    exportedKey.setCatalog(rs.getString(i++));
	    exportedKey.setSchema(rs.getString(i++));
	    exportedKey.setPrimaryKeyTable(rs.getString(i++));
	    exportedKey.setPrimaryKeyColumn(rs.getString(i++));

	    exportedKey.setForeignKeyCatalog(rs.getString(i++));
	    exportedKey.setForeignKeySchema(rs.getString(i++));
	    exportedKey.setForeignKeyTable(rs.getString(i++));
	    exportedKey.setForeignKeyColumn(rs.getString(i++));

	    exportedKey.setKeySequence(rs.getInt(i++));
	    exportedKey.setUpdateRule(MetaDataJavaUtil.decodeRule(rs.getInt(i++)));
	    exportedKey.setDeleteRule(MetaDataJavaUtil.decodeRule(rs.getInt(i++)));
	    exportedKey.setForeignKeyName(rs.getString(i++));
	    exportedKey.setPrimaryKeyName(rs.getString(i++));

	    exportedKey.setDeferrability(rs.getInt(i++));
	    exportedKeys.add(exportedKey);
	}

	return exportedKeys;

    }

    /**
     * Returns the foreign imported keys for the passed table
     *
     * @param tableName the table name for the passed table
     * @return the foreign exported keys
     * @throws SQLException it any SQL Exception occurs
     */
    public List<ImportedKey> getImportedKeys(String tableName) throws SQLException {
	if (tableName == null) {
	    throw new NullPointerException("tableName is null!");
	}

	if (!tableNamesSet.contains(tableName.toLowerCase())) {
	    throw new IllegalArgumentException("table does not exists: " + tableName);
	}

	DatabaseMetaData databaseMetaData = connection.getMetaData();
	ResultSet rs = databaseMetaData.getImportedKeys(catalog, schema, tableName);

	List<ImportedKey> importedKeys = new ArrayList<>();
	while (rs.next()) {
	    ImportedKey importedKey = new ImportedKey();
	    int i = 1;
	    importedKey.setCatalog(rs.getString(i++));
	    importedKey.setSchema(rs.getString(i++));
	    importedKey.setPrimaryKeyTable(rs.getString(i++));
	    importedKey.setPrimaryKeyColumn(rs.getString(i++));

	    importedKey.setForeignKeyCatalog(rs.getString(i++));
	    importedKey.setForeignKeySchema(rs.getString(i++));
	    importedKey.setForeignKeyTable(rs.getString(i++));
	    importedKey.setForeignKeyColumn(rs.getString(i++));

	    importedKey.setKeySequence(rs.getInt(i++));
	    importedKey.setUpdateRule(MetaDataJavaUtil.decodeRule(rs.getInt(i++)));
	    importedKey.setDeleteRule(MetaDataJavaUtil.decodeRule(rs.getInt(i++)));
	    importedKey.setForeignKeyName(rs.getString(i++));
	    importedKey.setPrimaryKeyName(rs.getString(i++));

	    importedKey.setDeferrability(rs.getInt(i++));
	    importedKeys.add(importedKey);
	}

	return importedKeys;
    }

    /**
     * Returns the foreign imported keys for the passed table
     *
     * @param tableName the table name for the passed table
     * @return the foreign exported keys
     * @throws SQLException it any SQL Exception occurs
     */
    public List<PrimaryKey> getPrimaryKeys(String tableName) throws SQLException {
	if (tableName == null) {
	    throw new NullPointerException("tableName is null!");
	}

	if (!tableNamesSet.contains(tableName.toLowerCase())) {
	    throw new IllegalArgumentException("table does not exists: " + tableName);
	}

	/**
	 * <pre>
	 * <code>
	1.TABLE_CAT String => table catalog (may be null)
	2.TABLE_SCHEM String => table schema (may be null)
	3.TABLE_NAME String => table name
	4.COLUMN_NAME String => column name
	5.KEY_SEQ short => sequence number within primary key( a valueof 1 represents the first column of the primary key, a value of 2 wouldrepresent the second column within the primary key).
	6.PK_NAME String => primary key name (may be null)

	    databaseMetaData.getPrimaryKeys( user_login) 1: null
	    databaseMetaData.getPrimaryKeys( user_login) 2: public
	    databaseMetaData.getPrimaryKeys( user_login) 3: user_login		table
	    databaseMetaData.getPrimaryKeys( user_login) 4: username		column
	    databaseMetaData.getPrimaryKeys( user_login) 5: 1			key sequence
	    databaseMetaData.getPrimaryKeys( user_login) 6: user_login_pkey		primary key name
	  </code>
	 * </pre>
	 */

	DatabaseMetaData databaseMetaData = connection.getMetaData();
	ResultSet rs = databaseMetaData.getPrimaryKeys(catalog, schema, tableName);

	List<PrimaryKey> primaryKeys = new ArrayList<>();
	while (rs.next()) {
	    PrimaryKey primaryKey = new PrimaryKey();
	    int i = 1;
	    primaryKey.setCatalog(rs.getString(i++));
	    primaryKey.setSchema(rs.getString(i++));
	    primaryKey.setTableName(rs.getString(i++));
	    primaryKey.setColumnName(rs.getString(i++));
	    primaryKey.setKeySequence(rs.getInt(i++));
	    primaryKey.setPrimaryKeyName(rs.getString(i++));

	    primaryKeys.add(primaryKey);
	}

	return primaryKeys;

    }

    /**
     * Returns the indexes for the passed table
     *
     * @param tableName the table name for the passed table
     * @return the indexes for the passed table
     * @throws SQLException it any SQL Exception occurs
     */
    public List<Index> getIndexes(String tableName) throws SQLException {
	if (tableName == null) {
	    throw new NullPointerException("tableName is null!");
	}

	if (!tableNamesSet.contains(tableName.toLowerCase())) {
	    throw new IllegalArgumentException("table does not exists: " + tableName);
	}
	/**
	 * <pre>
	 * <code>
	1.TABLE_CAT String => tableName catalog (may be null)
	2.TABLE_SCHEM String => tableName schema (may be null)
	3.TABLE_NAME String => tableName name
	4.NON_UNIQUE boolean => Can index values be non-unique.false when TYPE is tableIndexStatistic
	5.INDEX_QUALIFIER String => index catalog (may be null); null when TYPE is tableIndexStatistic
	6.INDEX_NAME String => index name; null when TYPE istableIndexStatistic
	7.TYPE short => index type: ◦ tableIndexStatistic - this identifies tableName statistics that arereturned in conjuction with a tableName's index descriptions
	◦ tableIndexClustered - this is a clustered index
	◦ tableIndexHashed - this is a hashed index
	◦ tableIndexOther - this is some other style of index
	8.ORDINAL_POSITION short => column sequence numberwithin index; zero when TYPE is tableIndexStatistic
	9.COLUMN_NAME String => column name; null when TYPE istableIndexStatistic
	10.ASC_OR_DESC String => column sort sequence, "A" => ascending,"D" => descending, may be null if sort sequence is not supported; null when TYPE is tableIndexStatistic
	11.CARDINALITY long => When TYPE is tableIndexStatistic, thenthis is the number of rows in the tableName; otherwise, it is thenumber of unique values in the index.
	12.PAGES long => When TYPE is tableIndexStatisic thenthis is the number of pages used for the tableName, otherwise itis the number of pages used for the current index.
	13.FILTER_CONDITION String => Filter condition, if any.(may be null)

	    databaseMetaData.getIndexInfo( user_login) 1: null
	    databaseMetaData.getIndexInfo( user_login) 2: public
	    databaseMetaData.getIndexInfo( user_login) 3: user_login			3.TABLE_NAME String => tableName name
	    databaseMetaData.getIndexInfo( user_login) 4: f					4.NON_UNIQUE
	    databaseMetaData.getIndexInfo( user_login) 5: null				5.INDEX_QUALIFIER
	    databaseMetaData.getIndexInfo( user_login) 6: user_login_pkey			6.INDEX_NAME String =>
	    databaseMetaData.getIndexInfo( user_login) 7: 3					7.TYPE short => index type short tableIndexStatistic = 0; / short tableIndexClustered = 1; short tableIndexHashed    = 2; short tableIndexOther        = 3;
	    databaseMetaData.getIndexInfo( user_login) 8: 1					8.ORDINAL_POSITION
	    databaseMetaData.getIndexInfo( user_login) 9: username				9.COLUMN_NAME
	    databaseMetaData.getIndexInfo( user_login) 10: A				10.ASC_OR_DESC
	    databaseMetaData.getIndexInfo( user_login) 11: 2				11.CARDINALITY
	    databaseMetaData.getIndexInfo( user_login) 12: 2				12.PAGES
	    databaseMetaData.getIndexInfo( user_login) 13: null				13.FILTER_CONDITION
	 </code>
	 * </pre>
	 */

	DatabaseMetaData databaseMetaData = connection.getMetaData();
	ResultSet rs = databaseMetaData.getIndexInfo(catalog, null, tableName, false, true);

	List<Index> indexes = new ArrayList<>();
	while (rs.next()) {
	    Index index = new Index();
	    int i = 1;
	    index.setCatalog(rs.getString(i++));
	    index.setSchema(rs.getString(i++));
	    index.setTableName(rs.getString(i++));
	    index.setNonUnique(rs.getBoolean(i++));
	    index.setIndexQualifier(rs.getString(i++));

	    index.setIndexName(rs.getString(i++));
	    index.setType(MetaDataJavaUtil.decodeType(rs.getInt(i++)));
	    index.setOrdinalPosition(rs.getInt(i++));
	    index.setColumnName(rs.getString(i++));
	    index.setAscendingOrDescending(rs.getString(i++));
	    index.setCardinality(rs.getInt(i++));
	    index.setPages(rs.getInt(i++));
	    index.setFilterCondition(rs.getString(i++));
	    indexes.add(index);
	}

	return indexes;

    }

    /**
     * Returns the indexes for the passed table
     *
     * @param tableName the table name for the passed table
     * @return the indexes for the passed table
     * @throws SQLException it any SQL Exception occurs
     */
    public List<Column> getColumns(String tableName) throws SQLException {
	if (tableName == null) {
	    throw new NullPointerException("tableName is null!");
	}

	if (!tableNamesSet.contains(tableName.toLowerCase())) {
	    throw new IllegalArgumentException("table does not exists: " + tableName);
	}
	/**
	 * <pre>
	 * <code>
	databaseMetaData.getColumns(customer_auto) 1: null                                1.TABLE_CAT String => table catalog (may be null)
	databaseMetaData.getColumns(customer_auto) 2: public                              2.TABLE_SCHEM String => table schema (may be null)
	databaseMetaData.getColumns(customer_auto) 3: customer_auto                       3.TABLE_NAME String => table name
	databaseMetaData.getColumns(customer_auto) 4: phone                               4.COLUMN_NAME String => column name

	databaseMetaData.getColumns(customer_auto) 5: 12                                  5.DATA_TYPE int => SQL type from java.sql.Types
	databaseMetaData.getColumns(customer_auto) 6: varchar                             6.TYPE_NAME String => Data source dependent type name,for a UDT the type name is fu
	databaseMetaData.getColumns(customer_auto) 7: 32                                  7.COLUMN_SIZE int => column size.
	databaseMetaData.getColumns(customer_auto) 8: null                                8.BUFFER_LENGTH is not used.
	databaseMetaData.getColumns(customer_auto) 9: 0                                   9.DECIMAL_DIGITS int => the number of fractional digits. Null is returned for data
	databaseMetaData.getColumns(customer_auto) 10: 10                                 10.NUM_PREC_RADIX int => Radix (typically either 10 or 2)
	databaseMetaData.getColumns(customer_auto) 11: 1                                  11.NULLABLE int => is NULL allowed. ◦ columnNoNulls - might not allow NULL values
	databaseMetaData.getColumns(customer_auto) 12: null                               12.REMARKS String => comment describing column (may be null)
	databaseMetaData.getColumns(customer_auto) 13: null                               13.COLUMN_DEF String => default value for the column, which should be interpreted as a string when the value is enclosed in single quotes (may be null)
	databaseMetaData.getColumns(customer_auto) 14: null                               14.SQL_DATA_TYPE int => unused
	databaseMetaData.getColumns(customer_auto) 15: null                               15.SQL_DATETIME_SUB int => unused
	databaseMetaData.getColumns(customer_auto) 16: 32                                 16.CHAR_OCTET_LENGTH int => for char types themaximum number of bytes in the column
	databaseMetaData.getColumns(customer_auto) 17: 8                                  17.ORDINAL_POSITION int => index of column in table(starting at 1)
	databaseMetaData.getColumns(customer_auto) 18: YES                                18.IS_NULLABLE String => ISO rules are used to determine the nullability for a column. ◦ YES --- if the column can include NULLs
	databaseMetaData.getColumns(customer_auto) 19: null                               19.SCOPE_CATALOG String => catalog of table that is the scopeof a reference attribute (null if DATA_TYPE isn't REF)
	databaseMetaData.getColumns(customer_auto) 20: null                               20.SCOPE_SCHEMA String => schema of table that is the scopeof a reference attribute (null if the DATA_TYPE isn't REF)
	databaseMetaData.getColumns(customer_auto) 21: null                               21.SCOPE_TABLE String => table name that this the scopeof a reference attribute (null if the DATA_TYPE isn't REF)
	databaseMetaData.getColumns(customer_auto) 22: null                               22.SOURCE_DATA_TYPE short => source type of a distinct type or user-generatedRef type, SQL type from java.sql.Types (null if
	databaseMetaData.getColumns(customer_auto) 23: NO                                 23.IS_AUTOINCREMENT String => Indicates whether this column is auto incremented ◦ YES --- if the column is auto incremented
	 </code>
	 * </pre>
	 */

	DatabaseMetaData databaseMetaData = connection.getMetaData();
	ResultSet rs = databaseMetaData.getColumns(catalog, schema, tableName, null);

	List<Column> columns = new ArrayList<>();
	while (rs.next()) {
	    Column column = new Column();
	    int i = 1;
	    column.setCatalog(rs.getString(i++)); // 1
	    column.setSchema(rs.getString(i++)); // 2
	    column.setTableName(rs.getString(i++)); // 3
	    column.setColumnName(rs.getString(i++)); // 4
	    i++; // 5
	    column.setTypeName(rs.getString(i++)); // 6
	    column.setSize(rs.getInt(i++)); // 7
	    i++; // 8
	    column.setDecimalDigits(rs.getInt(i++)); // 9
	    column.setRadix(rs.getInt(i++)); // 10
	    column.setNullable(MetaDataJavaUtil.decodeNullable(rs.getInt(i++))); // 11
	    column.setRemarks(rs.getString(i++)); // 12
	    column.setDefaultValue(rs.getString(i++)); // 13
	    i++; // 14
	    i++; // 15
	    column.setCharOctetLength(rs.getInt(i++)); // 16
	    column.setOrdinalPosition(rs.getInt(i++)); // 17
	    column.setIsNullable(rs.getString(i++)); // 18

	    if (!new SqlUtil(connection).isOracle()) {
		column.setScopeCatalog(rs.getString(i++)); // 19
		column.setScopeSchema(rs.getString(i++)); // 20
		column.setScopeTable(rs.getString(i++)); // 21
	    }

	    /**
	     * NO: does not pass SQL Server
	     *
	     * <pre>
	     * <code>
	    column.setSourceDataType(rs.getShort(i++)); // 22
	    column.setIsAutoincrement(rs.getString(i++)); // 23
	     </code>
	     * </pre>
	     */

	    columns.add(column);
	}

	return columns;

    }

    /**
     * Returns the table detail of passed table name
     *
     * @param tableName
     * @return the table detail of passed table name
     * @throws SQLException it any SQL Exception occurs
     */
    public Table getTable(String tableName) throws SQLException {
	if (tableName == null) {
	    throw new NullPointerException("tableName is null!");
	}

	if (!tableNamesSet.contains(tableName.toLowerCase())) {
	    throw new IllegalArgumentException("table does not exists: " + tableName);
	}

	/**
	 * <pre>
	 * <code>
	// 	   1.TABLE_CAT String => table catalog (may be null)
	// 	   2.TABLE_SCHEM String => table schema (may be null)
	// 	   3.TABLE_NAME String => table name
	// 	   4.TABLE_TYPE String => table type. Typical types are "TABLE","VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY","LOCAL TEMPORARY", "ALIAS", "SYNONYM".
	// 	   5.REMARKS String => explanatory comment on the table
	// 	   6.TYPE_CAT String => the types catalog (may be null)
	// 	   7.TYPE_SCHEM String => the types schema (may be null)
	// 	   8.TYPE_NAME String => type name (may be null)
	// 	   9.SELF_REFERENCING_COL_NAME String => name of the designated "identifier" column of a typed table (may be null)
	// 	   10.REF_GENERATION String => specifies how values in SELF_REFERENCING_COL_NAME are created. Values are"SYSTEM", "USER", "DERIVED". (may be null)
	</code>
	 * </pre>
	 */

	DatabaseMetaData databaseMetaData = connection.getMetaData();
	ResultSet rs = databaseMetaData.getTables(catalog, schema, tableName, null);
	Table table = new Table();

	while (rs.next()) {
	    if (!rs.getString(3).equalsIgnoreCase(tableName)) {
		continue;
	    }
	    int i = 1;
	    table.setCatalog(rs.getString(i++));
	    table.setSchema(rs.getString(i++));
	    table.setTableName(rs.getString(i++));
	    table.setTableType(rs.getString(i++));
	    table.setRemarks(rs.getString(i++));

	    // table.setTypeCatalog(rs.getString(i++));
	    // table.setTypeSchema(rs.getString(i++));
	    // table.setTypeName(rs.getString(i++));
	    // table.setSelfReferencingColName(rs.getString(i++));
	    // table.setRefGeneration(rs.getString(i++));

	    List<Column> columns = getColumns(tableName);
	    List<PrimaryKey> primaryKeys = getPrimaryKeys(tableName);
	    List<Index> indexes = getIndexes(tableName);
	    List<ImportedKey> importedKeys = getImportedKeys(tableName);
	    List<ExportedKey> exportedKeys = getExportedKeys(tableName);

	    table.setColumns(columns);
	    table.setPrimaryKeys(primaryKeys);
	    table.setIndexes(indexes);
	    table.setImportedforeignKeys(importedKeys);
	    table.setExportedforeignKeys(exportedKeys);
	}
	return table;

    }

    @SuppressWarnings("unused")
    private void debug(String string) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + string);
	}

    }

}
