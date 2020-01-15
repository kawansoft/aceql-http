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

package org.kawanfw.test.sql.metadata.aceql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nicolas de Pomereu
 *
 */
public class MetaDataJavaPrinter {

    private Connection connection = null;
    private String catalog = null;


    public MetaDataJavaPrinter(Connection connection) {
	this.connection = connection;
    }

    public MetaDataJavaPrinter(Connection connection, String catalog) {
	this.connection = connection;
	this.catalog = catalog;
    }

    private void printGetIndexInfo(String tableName) throws SQLException {

	if (tableName == null) {
	    throw new NullPointerException("tableName is null!");
	}
	/**
	 *<pre><code>

            TABLE_CAT String => table catalog (may be null)
            2.TABLE_SCHEM String => table schema (may be null)
            3.TABLE_NAME String => table name
            4.NON_UNIQUE boolean => Can index values be non-unique.false when TYPE is tableIndexStatistic
            5.INDEX_QUALIFIER String => index catalog (may be null); null when TYPE is tableIndexStatistic
            6.INDEX_NAME String => index name; null when TYPE istableIndexStatistic
            7.TYPE short => index type: ◦ tableIndexStatistic - this identifies table statistics that arereturned in conjuction with a table's index descriptions
            ◦ tableIndexClustered - this is a clustered index
            ◦ tableIndexHashed - this is a hashed index
            ◦ tableIndexOther - this is some other style of index

            8.ORDINAL_POSITION short => Column sequence numberwithin index; zero when TYPE is tableIndexStatistic
            9.COLUMN_NAME String => Column name; null when TYPE istableIndexStatistic
            10.ASC_OR_DESC String => Column sort sequence, "A" => ascending,"D" => descending, may be null if sort sequence is not supported; null when TYPE is tableIndexStatistic
            11.CARDINALITY long => When TYPE is tableIndexStatistic, thenthis is the number of rows in the table; otherwise, it is thenumber of unique values in the index.
            12.PAGES long => When TYPE is tableIndexStatisic thenthis is the number of pages used for the table, otherwise itis the number of pages used for the current index.
            13.FILTER_CONDITION String => Filter condition, if any.(may be null)
	</code></pre>
	 */

	DatabaseMetaData databaseMetaData = connection.getMetaData();
	ResultSet rs = databaseMetaData.getIndexInfo(catalog, null, tableName, false, true);

	while (rs.next()) {
	    System.out.println();
	    for (int i = 1; i < 14; i++) {
		System.out.println("databaseMetaData.getIndexInfo( " + tableName + ") " + i + ": " + rs.getString(i));
	    }
	}

	printCatalogsAndSchemas();

    }

    private void printCatalogsAndSchemas() throws SQLException {

	System.out.println();
	System.out.println("printCatalogs: ");
	DatabaseMetaData databaseMetaData = connection.getMetaData();
	ResultSet rs = databaseMetaData.getCatalogs();
	while(rs.next()) {
	    System.out.println("databaseMetaData.getCatalogs(): " + rs.getString(1));
	}

	System.out.println();
	System.out.println("printSchemas: ");
	ResultSet rs2 = databaseMetaData.getSchemas();
	while(rs2.next()) {
	    System.out.println("databaseMetaData.getSchemas(): " + rs2.getString(1) + " " + rs2.getString(2));
	}


    }

    private void printGetPrimaryKeys(String tableName) throws SQLException {

	if (tableName == null) {
	    throw new NullPointerException("tableName is null!");
	}
	/**
	<pre><code>
            1.TABLE_CAT String => table catalog (may be null)
            2.TABLE_SCHEM String => table schema (may be null)
            3.TABLE_NAME String => table name
            4.COLUMN_NAME String => Column name
            5.KEY_SEQ short => sequence number within primary key( a valueof 1 represents the first Column of the primary key, a value of 2 wouldrepresent the second Column within the primary key).
            6.PK_NAME String => primary key name (may be null)
	</code></pre>
	 */

	DatabaseMetaData databaseMetaData = connection.getMetaData();
	ResultSet rs = databaseMetaData.getPrimaryKeys(catalog, null, tableName);

	while (rs.next()) {
	    System.out.println();
	    for (int i = 1; i < 7; i++) {
		System.out.println("databaseMetaData.getPrimaryKeys( " + tableName + ") " + i + ": " + rs.getString(i));
	    }
	}

    }

    private void printGetImportedKeys(String tableName) throws SQLException {

	if (tableName == null) {
	    throw new NullPointerException("tableName is null!");
	}
	/**

        1.PKTABLE_CAT String => primary key table catalogbeing imported (may be null)
        2.PKTABLE_SCHEM String => primary key table schemabeing imported (may be null)
        3.PKTABLE_NAME String => primary key table namebeing imported
        4.PKCOLUMN_NAME String => primary key Column namebeing imported
        5.FKTABLE_CAT String => foreign key table catalog (may be null)
        6.FKTABLE_SCHEM String => foreign key table schema (may be null)
        7.FKTABLE_NAME String => foreign key table name
        8.FKCOLUMN_NAME String => foreign key Column name
        9.KEY_SEQ short => sequence number within a foreign key( a valueof 1 represents the first Column of the foreign key, a value of 2 wouldrepresent the second Column within the foreign key).
        10.UPDATE_RULE short => What happens to aforeign key when the primary key is updated: ◦ importedNoAction - do not allow update of primarykey if it has been imported
        ◦ importedKeyCascade - change imported key to agreewith primary key update
        ◦ importedKeySetNull - change imported key to NULLif its primary key has been updated
        ◦ importedKeySetDefault - change imported key to default valuesif its primary key has been updated
        ◦ importedKeyRestrict - same as importedKeyNoAction(for ODBC 2.x compatibility)

        11.DELETE_RULE short => What happens tothe foreign key when primary is deleted. ◦ importedKeyNoAction - do not allow delete of primarykey if it has been imported
        ◦ importedKeyCascade - delete rows that import a deleted key
        ◦ importedKeySetNull - change imported key to NULL ifits primary key has been deleted
        ◦ importedKeyRestrict - same as importedKeyNoAction(for ODBC 2.x compatibility)
        ◦ importedKeySetDefault - change imported key to default ifits primary key has been deleted

        12.FK_NAME String => foreign key name (may be null)
        13.PK_NAME String => primary key name (may be null)
        14.DEFERRABILITY short => can the evaluation of foreign keyconstraints be deferred until commit ◦ importedKeyInitiallyDeferred - see SQL92 for definition
        ◦ importedKeyInitiallyImmediate - see SQL92 for definition
        ◦ importedKeyNotDeferrable - see SQL92 for definition

	 */

	DatabaseMetaData databaseMetaData = connection.getMetaData();
	ResultSet rs = databaseMetaData.getImportedKeys(catalog, null, tableName);

	while (rs.next()) {
	    System.out.println();
	    for (int i = 1; i < 15; i++) {
		System.out.println("databaseMetaData.getImportedKeys( " + tableName + ") " + i + ": " + rs.getString(i));
	    }
	}

    }

    private void printGetExportedKeys(String tableName) throws SQLException {

	if (tableName == null) {
	    throw new NullPointerException("tableName is null!");
	}
	/**
	<pre><code>
        1.PKTABLE_CAT String => primary key table catalog (may be null)
        2.PKTABLE_SCHEM String => primary key table schema (may be null)
        3.PKTABLE_NAME String => primary key table name
        4.PKCOLUMN_NAME String => primary key Column name
        5.FKTABLE_CAT String => foreign key table catalog (may be null)being exported (may be null)
        6.FKTABLE_SCHEM String => foreign key table schema (may be null)being exported (may be null)
        7.FKTABLE_NAME String => foreign key table namebeing exported
        8.FKCOLUMN_NAME String => foreign key Column namebeing exported
        9.KEY_SEQ short => sequence number within foreign key( a valueof 1 represents the first Column of the foreign key, a value of 2 wouldrepresent the second Column within the foreign key).
        10.UPDATE_RULE short => What happens toforeign key when primary is updated: ◦ importedNoAction - do not allow update of primarykey if it has been imported
        ◦ importedKeyCascade - change imported key to agreewith primary key update
        ◦ importedKeySetNull - change imported key to NULL ifits primary key has been updated
        ◦ importedKeySetDefault - change imported key to default valuesif its primary key has been updated
        ◦ importedKeyRestrict - same as importedKeyNoAction(for ODBC 2.x compatibility)

        11.DELETE_RULE short => What happens tothe foreign key when primary is deleted. ◦ importedKeyNoAction - do not allow delete of primarykey if it has been imported
        ◦ importedKeyCascade - delete rows that import a deleted key
        ◦ importedKeySetNull - change imported key to NULL ifits primary key has been deleted
        ◦ importedKeyRestrict - same as importedKeyNoAction(for ODBC 2.x compatibility)
        ◦ importedKeySetDefault - change imported key to default ifits primary key has been deleted

        12.FK_NAME String => foreign key name (may be null)
        13.PK_NAME String => primary key name (may be null)
        14.DEFERRABILITY short => can the evaluation of foreign keyconstraints be deferred until commit ◦ importedKeyInitiallyDeferred - see SQL92 for definition
        ◦ importedKeyInitiallyImmediate - see SQL92 for definition
        ◦ importedKeyNotDeferrable - see SQL92 for definition

	</code></pre>
	 */

	DatabaseMetaData databaseMetaData = connection.getMetaData();
	ResultSet rs = databaseMetaData.getExportedKeys(catalog, null, tableName);

	while (rs.next()) {
	    System.out.println();
	    for (int i = 1; i < 15; i++) {
		System.out.println("databaseMetaData.getExportedKeys( " + tableName + ") " + i + ": " + rs.getString(i));
	    }
	}

    }

    private void printTablesInfo() throws SQLException {

	//String [] types = {"TABLE","VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY","LOCAL TEMPORARY", "ALIAS", "SYNONYM"};
	String [] types = {"TABLE","VIEW", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"};

	DatabaseMetaData databaseMetaData = connection.getMetaData();
	ResultSet rs = databaseMetaData.getTables(catalog, null, null, types);

	/**
	 * <pre><code>
            Each table description has the following columns:
            1.TABLE_CAT String => table catalog (may be null)
            2.TABLE_SCHEM String => table schema (may be null)
            3.TABLE_NAME String => table name
            4.TABLE_TYPE String => table type. Typical types are "TABLE","VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY","LOCAL TEMPORARY", "ALIAS", "SYNONYM".
            5.REMARKS String => explanatory comment on the table
            6.TYPE_CAT String => the types catalog (may be null)
            7.TYPE_SCHEM String => the types schema (may be null)
            8.TYPE_NAME String => type name (may be null)
            9.SELF_REFERENCING_COL_NAME String => name of the designated"identifier" Column of a typed table (may be null)
            10.REF_GENERATION String => specifies how values inSELF_REFERENCING_COL_NAME are created. Values are"SYSTEM", "USER", "DERIVED". (may be null)
            </code></pre>
	*/

	while (rs.next()) {
	    System.out.println();
	    for (int i = 1; i < 6; i++) {
		System.out.println("databaseMetaData.getTables() " + i + ": " + rs.getString(i));
	    }
	}
    }

    public void printGetColumns(String tableName) throws SQLException {
	DatabaseMetaData databaseMetaData = connection.getMetaData();
	ResultSet rs = databaseMetaData.getColumns(catalog, null, tableName, null);

	/**
	 * <pre><code>
            Each Column description has the following columns:
            1.TABLE_CAT String => table catalog (may be null)
            2.TABLE_SCHEM String => table schema (may be null)
            3.TABLE_NAME String => table name
            4.COLUMN_NAME String => Column name
            5.DATA_TYPE int => SQL type from java.sql.Types
            6.TYPE_NAME String => Data source dependent type name,for a UDT the type name is fully qualified
            7.COLUMN_SIZE int => Column size.
            8.BUFFER_LENGTH is not used.
            9.DECIMAL_DIGITS int => the number of fractional digits. Null is returned for data types whereDECIMAL_DIGITS is not applicable.
            10.NUM_PREC_RADIX int => Radix (typically either 10 or 2)
            11.NULLABLE int => is NULL allowed. ◦ columnNoNulls - might not allow NULL values
            ◦ columnNullable - definitely allows NULL values
            ◦ columnNullableUnknown - nullability unknown

            12.REMARKS String => comment describing Column (may be null)
            13.COLUMN_DEF String => default value for the Column, which should be interpreted as a string when the value is enclosed in single quotes (may be null)
            14.SQL_DATA_TYPE int => unused
            15.SQL_DATETIME_SUB int => unused
            16.CHAR_OCTET_LENGTH int => for char types themaximum number of bytes in the Column
            17.ORDINAL_POSITION int => index of Column in table(starting at 1)
            18.IS_NULLABLE String => ISO rules are used to determine the nullability for a Column. ◦ YES --- if the Column can include NULLs
            ◦ NO --- if the Column cannot include NULLs
            ◦ empty string --- if the nullability for thecolumn is unknown

            19.SCOPE_CATALOG String => catalog of table that is the scopeof a reference attribute (null if DATA_TYPE isn't REF)
            20.SCOPE_SCHEMA String => schema of table that is the scopeof a reference attribute (null if the DATA_TYPE isn't REF)
            21.SCOPE_TABLE String => table name that this the scopeof a reference attribute (null if the DATA_TYPE isn't REF)
            22.SOURCE_DATA_TYPE short => source type of a distinct type or user-generatedRef type, SQL type from java.sql.Types (null if DATA_TYPEisn't DISTINCT or user-generated REF)
            23.IS_AUTOINCREMENT String => Indicates whether this Column is auto incremented ◦ YES --- if the Column is auto incremented
            ◦ NO --- if the Column is not auto incremented
            ◦ empty string --- if it cannot be determined whether the Column is auto incremented

            24.IS_GENERATEDCOLUMN String => Indicates whether this is a generated Column ◦ YES --- if this a generated Column
            ◦ NO --- if this not a generated Column
            ◦ empty string --- if it cannot be determined whether this is a generated Column

	</code></pre>
	*/

	while (rs.next()) {
	    System.out.println();
	    for (int i = 1; i < 24; i++) {
		System.out.println("databaseMetaData.getColumns(" + tableName + ") " + i + ": " + rs.getString(i));
	    }
	}

    }

    public List<String> getTableTypes() throws SQLException {
	DatabaseMetaData databaseMetaData = connection.getMetaData();
	ResultSet rs = databaseMetaData.getTableTypes();
	List<String> tableTypes = new ArrayList<>();
	while (rs.next()) {
	    tableTypes.add(rs.getString(1));
	}
	return tableTypes;
    }

    public List<String> getTableNames() throws SQLException {
	DatabaseMetaData databaseMetaData = connection.getMetaData();

	//String [] types = {"TABLE","VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY","LOCAL TEMPORARY", "ALIAS", "SYNONYM"};
	String [] types = {"TABLE","VIEW", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"};

	ResultSet rs = databaseMetaData.getTables(null, null, null, types);

	List<String> tableNames = new ArrayList<>();
	while (rs.next()) {
	    String tableName = rs.getString(3);
	    String tableType = rs.getString(4);
	    if (isTable(tableType)) {
		 tableNames.add(tableName);
	    }
	}
	return tableNames;

    }

    private boolean isTable(String tableType) {
	if (tableType == null) {
	    System.err.println("tableType is null!");
	    return false;
	}
	// TODO Auto-generated method stub
	return tableType.equalsIgnoreCase("TABLE") ? true: false;
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	Connection connection = ConnectionParms.getConnection();

	MetaDataJavaPrinter metaDataJava = new MetaDataJavaPrinter(connection, "kawansoft_example");

	DatabaseMetaData databaseMetaData = connection.getMetaData();
	String databaseProductName = databaseMetaData.getDatabaseProductName();
	System.out.println("databaseMetaData.databaseProductName: " + databaseProductName);

	metaDataJava.printCatalogsAndSchemas();

	System.out.println();

	List<String> tableTypes = metaDataJava.getTableTypes();
	System.out.println("tableTypes: " + tableTypes);

	List<String> tables = metaDataJava.getTableNames();
	System.out.println("getTableNames: " + tables);

	metaDataJava.printTablesInfo();

	System.out.println();

	for (String table : tables) {
	    System.out.println();
	    metaDataJava.printGetColumns(table);
	}

	for (String table : tables) {
	    System.out.println();
	    metaDataJava.printGetIndexInfo(table);
	}

	for (String table : tables) {
	    System.out.println();
	    metaDataJava.printGetPrimaryKeys(table);
	}

	for (String table : tables) {
	    System.out.println();
	    metaDataJava.printGetImportedKeys(table);
	}

	for (String table : tables) {
	    System.out.println();
	    metaDataJava.printGetExportedKeys(table);
	}

	System.out.println();

    }
}
