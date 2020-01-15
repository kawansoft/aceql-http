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

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * A holder for a DatabaseMetaData that we want transport.
 *
 * @author Nicolas de Pomereu
 */

public class JdbcDatabaseMetaData {

    public static boolean DEBUG = false;

    private String getURL;
    private boolean isReadOnly;
    private boolean allProceduresAreCallable;
    private boolean allTablesAreSelectable;
    private String getUserName;
    private boolean nullsAreSortedHigh;
    private boolean nullsAreSortedLow;
    private boolean nullsAreSortedAtStart;
    private boolean nullsAreSortedAtEnd;
    private String getDatabaseProductName;
    private String getDatabaseProductVersion;
    private String getDriverName;
    private String getDriverVersion;
    private int getDriverMajorVersion;
    private int getDriverMinorVersion;
    private boolean usesLocalFiles;
    private boolean usesLocalFilePerTable;
    private boolean supportsMixedCaseIdentifiers;
    private boolean storesUpperCaseIdentifiers;
    private boolean storesLowerCaseIdentifiers;
    private boolean storesMixedCaseIdentifiers;
    private boolean supportsMixedCaseQuotedIdentifiers;
    private boolean storesUpperCaseQuotedIdentifiers;
    private boolean storesLowerCaseQuotedIdentifiers;
    private boolean storesMixedCaseQuotedIdentifiers;
    private String getIdentifierQuoteString;
    private String getSQLKeywords;
    private String getNumericFunctions;
    private String getStringFunctions;
    private String getSystemFunctions;
    private String getTimeDateFunctions;
    private String getSearchStringEscape;
    private String getExtraNameCharacters;
    private boolean supportsAlterTableWithAddColumn;
    private boolean supportsAlterTableWithDropColumn;
    private boolean supportsColumnAliasing;
    private boolean nullPlusNonNullIsNull;
    private boolean supportsConvert;
    private boolean supportsTableCorrelationNames;
    private boolean supportsDifferentTableCorrelationNames;
    private boolean supportsExpressionsInOrderBy;
    private boolean supportsOrderByUnrelated;
    private boolean supportsGroupBy;
    private boolean supportsGroupByUnrelated;
    private boolean supportsGroupByBeyondSelect;
    private boolean supportsLikeEscapeClause;
    private boolean supportsMultipleResultSets;
    private boolean supportsMultipleTransactions;
    private boolean supportsNonNullableColumns;
    private boolean supportsMinimumSQLGrammar;
    private boolean supportsCoreSQLGrammar;
    private boolean supportsExtendedSQLGrammar;
    private boolean supportsANSI92EntryLevelSQL;
    private boolean supportsANSI92IntermediateSQL;
    private boolean supportsANSI92FullSQL;
    private boolean supportsIntegrityEnhancementFacility;
    private boolean supportsOuterJoins;
    private boolean supportsFullOuterJoins;
    private boolean supportsLimitedOuterJoins;
    private String getSchemaTerm;
    private String getProcedureTerm;
    private String getCatalogTerm;
    private boolean isCatalogAtStart;
    private String getCatalogSeparator;
    private boolean supportsSchemasInDataManipulation;
    private boolean supportsSchemasInProcedureCalls;
    private boolean supportsSchemasInTableDefinitions;
    private boolean supportsSchemasInIndexDefinitions;
    private boolean supportsSchemasInPrivilegeDefinitions;
    private boolean supportsCatalogsInDataManipulation;
    private boolean supportsCatalogsInProcedureCalls;
    private boolean supportsCatalogsInTableDefinitions;
    private boolean supportsCatalogsInIndexDefinitions;
    private boolean supportsCatalogsInPrivilegeDefinitions;
    private boolean supportsPositionedDelete;
    private boolean supportsPositionedUpdate;
    private boolean supportsSelectForUpdate;
    private boolean supportsStoredProcedures;
    private boolean supportsSubqueriesInComparisons;
    private boolean supportsSubqueriesInExists;
    private boolean supportsSubqueriesInIns;
    private boolean supportsSubqueriesInQuantifieds;
    private boolean supportsCorrelatedSubqueries;
    private boolean supportsUnion;
    private boolean supportsUnionAll;
    private boolean supportsOpenCursorsAcrossCommit;
    private boolean supportsOpenCursorsAcrossRollback;
    private boolean supportsOpenStatementsAcrossCommit;
    private boolean supportsOpenStatementsAcrossRollback;
    private int getMaxBinaryLiteralLength;
    private int getMaxCharLiteralLength;
    private int getMaxColumnNameLength;
    private int getMaxColumnsInGroupBy;
    private int getMaxColumnsInIndex;
    private int getMaxColumnsInOrderBy;
    private int getMaxColumnsInSelect;
    private int getMaxColumnsInTable;
    private int getMaxConnections;
    private int getMaxCursorNameLength;
    private int getMaxIndexLength;
    private int getMaxSchemaNameLength;
    private int getMaxProcedureNameLength;
    private int getMaxCatalogNameLength;
    private int getMaxRowSize;
    private boolean doesMaxRowSizeIncludeBlobs;
    private int getMaxStatementLength;
    private int getMaxStatements;
    private int getMaxTableNameLength;
    private int getMaxTablesInSelect;
    private int getMaxUserNameLength;
    private int getDefaultTransactionIsolation;
    private boolean supportsTransactions;
    private boolean supportsDataDefinitionAndDataManipulationTransactions;
    private boolean supportsDataManipulationTransactionsOnly;
    private boolean dataDefinitionCausesTransactionCommit;
    private boolean dataDefinitionIgnoredInTransactions;
    private boolean supportsBatchUpdates;
    private boolean supportsSavepoints;
    private boolean supportsNamedParameters;
    private boolean supportsMultipleOpenResults;
    private boolean supportsGetGeneratedKeys;
    private int getDatabaseMajorVersion;
    private int getDatabaseMinorVersion;
    private int getJDBCMajorVersion;
    private int getJDBCMinorVersion;
    private int getSQLStateType;
    private boolean locatorsUpdateCopy;
    private boolean supportsStatementPooling;
    private boolean supportsStoredFunctionsUsingCallSyntax;
    private boolean autoCommitFailureClosesAllResultSets;
    private int getResultSetHoldability;

    /**
     * Constructor
     */
    public JdbcDatabaseMetaData() {
	super();
    }

    public void setDatabaseMetaDataHolder(DatabaseMetaData wrapper) {

	try {
	    getURL = wrapper.getURL();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    isReadOnly = wrapper.isReadOnly();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    allProceduresAreCallable = wrapper.allProceduresAreCallable();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    allTablesAreSelectable = wrapper.allTablesAreSelectable();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getUserName = wrapper.getUserName();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    nullsAreSortedHigh = wrapper.nullsAreSortedHigh();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    nullsAreSortedLow = wrapper.nullsAreSortedLow();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    nullsAreSortedAtStart = wrapper.nullsAreSortedAtStart();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    nullsAreSortedAtEnd = wrapper.nullsAreSortedAtEnd();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getDatabaseProductName = wrapper.getDatabaseProductName();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getDatabaseProductVersion = wrapper.getDatabaseProductVersion();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getDriverName = wrapper.getDriverName();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getDriverVersion = wrapper.getDriverVersion();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getDriverMajorVersion = wrapper.getDriverMajorVersion();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getDriverMinorVersion = wrapper.getDriverMinorVersion();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    usesLocalFiles = wrapper.usesLocalFiles();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    usesLocalFilePerTable = wrapper.usesLocalFilePerTable();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsMixedCaseIdentifiers = wrapper.supportsMixedCaseIdentifiers();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    storesUpperCaseIdentifiers = wrapper.storesUpperCaseIdentifiers();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    storesLowerCaseIdentifiers = wrapper.storesLowerCaseIdentifiers();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    storesMixedCaseIdentifiers = wrapper.storesMixedCaseIdentifiers();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsMixedCaseQuotedIdentifiers = wrapper.supportsMixedCaseQuotedIdentifiers();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    storesUpperCaseQuotedIdentifiers = wrapper.storesUpperCaseQuotedIdentifiers();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    storesLowerCaseQuotedIdentifiers = wrapper.storesLowerCaseQuotedIdentifiers();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    storesMixedCaseQuotedIdentifiers = wrapper.storesMixedCaseQuotedIdentifiers();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getIdentifierQuoteString = wrapper.getIdentifierQuoteString();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getSQLKeywords = wrapper.getSQLKeywords();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getNumericFunctions = wrapper.getNumericFunctions();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getStringFunctions = wrapper.getStringFunctions();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getSystemFunctions = wrapper.getSystemFunctions();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getTimeDateFunctions = wrapper.getTimeDateFunctions();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getSearchStringEscape = wrapper.getSearchStringEscape();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getExtraNameCharacters = wrapper.getExtraNameCharacters();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsAlterTableWithAddColumn = wrapper.supportsAlterTableWithAddColumn();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsAlterTableWithDropColumn = wrapper.supportsAlterTableWithDropColumn();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsColumnAliasing = wrapper.supportsColumnAliasing();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    nullPlusNonNullIsNull = wrapper.nullPlusNonNullIsNull();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsConvert = wrapper.supportsConvert();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsTableCorrelationNames = wrapper.supportsTableCorrelationNames();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsDifferentTableCorrelationNames = wrapper.supportsDifferentTableCorrelationNames();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsExpressionsInOrderBy = wrapper.supportsExpressionsInOrderBy();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsOrderByUnrelated = wrapper.supportsOrderByUnrelated();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsGroupBy = wrapper.supportsGroupBy();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsGroupByUnrelated = wrapper.supportsGroupByUnrelated();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsGroupByBeyondSelect = wrapper.supportsGroupByBeyondSelect();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsLikeEscapeClause = wrapper.supportsLikeEscapeClause();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsMultipleResultSets = wrapper.supportsMultipleResultSets();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsMultipleTransactions = wrapper.supportsMultipleTransactions();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsNonNullableColumns = wrapper.supportsNonNullableColumns();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsMinimumSQLGrammar = wrapper.supportsMinimumSQLGrammar();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsCoreSQLGrammar = wrapper.supportsCoreSQLGrammar();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsExtendedSQLGrammar = wrapper.supportsExtendedSQLGrammar();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsANSI92EntryLevelSQL = wrapper.supportsANSI92EntryLevelSQL();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsANSI92IntermediateSQL = wrapper.supportsANSI92IntermediateSQL();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsANSI92FullSQL = wrapper.supportsANSI92FullSQL();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsIntegrityEnhancementFacility = wrapper.supportsIntegrityEnhancementFacility();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsOuterJoins = wrapper.supportsOuterJoins();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsFullOuterJoins = wrapper.supportsFullOuterJoins();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsLimitedOuterJoins = wrapper.supportsLimitedOuterJoins();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getSchemaTerm = wrapper.getSchemaTerm();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getProcedureTerm = wrapper.getProcedureTerm();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getCatalogTerm = wrapper.getCatalogTerm();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    isCatalogAtStart = wrapper.isCatalogAtStart();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getCatalogSeparator = wrapper.getCatalogSeparator();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsSchemasInDataManipulation = wrapper.supportsSchemasInDataManipulation();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsSchemasInProcedureCalls = wrapper.supportsSchemasInProcedureCalls();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsSchemasInTableDefinitions = wrapper.supportsSchemasInTableDefinitions();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsSchemasInIndexDefinitions = wrapper.supportsSchemasInIndexDefinitions();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsSchemasInPrivilegeDefinitions = wrapper.supportsSchemasInPrivilegeDefinitions();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsCatalogsInDataManipulation = wrapper.supportsCatalogsInDataManipulation();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsCatalogsInProcedureCalls = wrapper.supportsCatalogsInProcedureCalls();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsCatalogsInTableDefinitions = wrapper.supportsCatalogsInTableDefinitions();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsCatalogsInIndexDefinitions = wrapper.supportsCatalogsInIndexDefinitions();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsCatalogsInPrivilegeDefinitions = wrapper.supportsCatalogsInPrivilegeDefinitions();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsPositionedDelete = wrapper.supportsPositionedDelete();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsPositionedUpdate = wrapper.supportsPositionedUpdate();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsSelectForUpdate = wrapper.supportsSelectForUpdate();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsStoredProcedures = wrapper.supportsStoredProcedures();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsSubqueriesInComparisons = wrapper.supportsSubqueriesInComparisons();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsSubqueriesInExists = wrapper.supportsSubqueriesInExists();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsSubqueriesInIns = wrapper.supportsSubqueriesInIns();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsSubqueriesInQuantifieds = wrapper.supportsSubqueriesInQuantifieds();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsCorrelatedSubqueries = wrapper.supportsCorrelatedSubqueries();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsUnion = wrapper.supportsUnion();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsUnionAll = wrapper.supportsUnionAll();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsOpenCursorsAcrossCommit = wrapper.supportsOpenCursorsAcrossCommit();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsOpenCursorsAcrossRollback = wrapper.supportsOpenCursorsAcrossRollback();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsOpenStatementsAcrossCommit = wrapper.supportsOpenStatementsAcrossCommit();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsOpenStatementsAcrossRollback = wrapper.supportsOpenStatementsAcrossRollback();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getMaxBinaryLiteralLength = wrapper.getMaxBinaryLiteralLength();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getMaxCharLiteralLength = wrapper.getMaxCharLiteralLength();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getMaxColumnNameLength = wrapper.getMaxColumnNameLength();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getMaxColumnsInGroupBy = wrapper.getMaxColumnsInGroupBy();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getMaxColumnsInIndex = wrapper.getMaxColumnsInIndex();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getMaxColumnsInOrderBy = wrapper.getMaxColumnsInOrderBy();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getMaxColumnsInSelect = wrapper.getMaxColumnsInSelect();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getMaxColumnsInTable = wrapper.getMaxColumnsInTable();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getMaxConnections = wrapper.getMaxConnections();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getMaxCursorNameLength = wrapper.getMaxCursorNameLength();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getMaxIndexLength = wrapper.getMaxIndexLength();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getMaxSchemaNameLength = wrapper.getMaxSchemaNameLength();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getMaxProcedureNameLength = wrapper.getMaxProcedureNameLength();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getMaxCatalogNameLength = wrapper.getMaxCatalogNameLength();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getMaxRowSize = wrapper.getMaxRowSize();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    doesMaxRowSizeIncludeBlobs = wrapper.doesMaxRowSizeIncludeBlobs();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getMaxStatementLength = wrapper.getMaxStatementLength();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getMaxStatements = wrapper.getMaxStatements();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getMaxTableNameLength = wrapper.getMaxTableNameLength();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getMaxTablesInSelect = wrapper.getMaxTablesInSelect();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getMaxUserNameLength = wrapper.getMaxUserNameLength();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getDefaultTransactionIsolation = wrapper.getDefaultTransactionIsolation();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsTransactions = wrapper.supportsTransactions();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsDataDefinitionAndDataManipulationTransactions = wrapper
		    .supportsDataDefinitionAndDataManipulationTransactions();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsDataManipulationTransactionsOnly = wrapper.supportsDataManipulationTransactionsOnly();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    dataDefinitionCausesTransactionCommit = wrapper.dataDefinitionCausesTransactionCommit();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    dataDefinitionIgnoredInTransactions = wrapper.dataDefinitionIgnoredInTransactions();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsBatchUpdates = wrapper.supportsBatchUpdates();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsSavepoints = wrapper.supportsSavepoints();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsNamedParameters = wrapper.supportsNamedParameters();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsMultipleOpenResults = wrapper.supportsMultipleOpenResults();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsGetGeneratedKeys = wrapper.supportsGetGeneratedKeys();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getDatabaseMajorVersion = wrapper.getDatabaseMajorVersion();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getDatabaseMinorVersion = wrapper.getDatabaseMinorVersion();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getJDBCMajorVersion = wrapper.getJDBCMajorVersion();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getJDBCMinorVersion = wrapper.getJDBCMinorVersion();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getSQLStateType = wrapper.getSQLStateType();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    locatorsUpdateCopy = wrapper.locatorsUpdateCopy();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsStatementPooling = wrapper.supportsStatementPooling();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    supportsStoredFunctionsUsingCallSyntax = wrapper.supportsStoredFunctionsUsingCallSyntax();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    autoCommitFailureClosesAllResultSets = wrapper.autoCommitFailureClosesAllResultSets();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}

	try {
	    getResultSetHoldability = wrapper.getResultSetHoldability();
	} catch (Throwable t) {
	    debug("Remote Driver Thrown Exception: " + t.toString());
	}
    }

    public String getURL() throws SQLException {
	return getURL;
    }

    public boolean isReadOnly() throws SQLException {
	return isReadOnly;
    }

    public boolean allProceduresAreCallable() throws SQLException {
	return allProceduresAreCallable;
    }

    public boolean allTablesAreSelectable() throws SQLException {
	return allTablesAreSelectable;
    }

    public String getUserName() throws SQLException {
	return getUserName;
    }

    public boolean nullsAreSortedHigh() throws SQLException {
	return nullsAreSortedHigh;
    }

    public boolean nullsAreSortedLow() throws SQLException {
	return nullsAreSortedLow;
    }

    public boolean nullsAreSortedAtStart() throws SQLException {
	return nullsAreSortedAtStart;
    }

    public boolean nullsAreSortedAtEnd() throws SQLException {
	return nullsAreSortedAtEnd;
    }

    public String getDatabaseProductName() throws SQLException {
	return getDatabaseProductName;
    }

    public String getDatabaseProductVersion() throws SQLException {
	return getDatabaseProductVersion;
    }

    public String getDriverName() throws SQLException {
	return getDriverName;
    }

    public String getDriverVersion() throws SQLException {
	return getDriverVersion;
    }

    public int getDriverMajorVersion() {
	return getDriverMajorVersion;
    }

    public int getDriverMinorVersion() {
	return getDriverMinorVersion;
    }

    public boolean usesLocalFiles() throws SQLException {
	return usesLocalFiles;
    }

    public boolean usesLocalFilePerTable() throws SQLException {
	return usesLocalFilePerTable;
    }

    public boolean supportsMixedCaseIdentifiers() throws SQLException {
	return supportsMixedCaseIdentifiers;
    }

    public boolean storesUpperCaseIdentifiers() throws SQLException {
	return storesUpperCaseIdentifiers;
    }

    public boolean storesLowerCaseIdentifiers() throws SQLException {
	return storesLowerCaseIdentifiers;
    }

    public boolean storesMixedCaseIdentifiers() throws SQLException {
	return storesMixedCaseIdentifiers;
    }

    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
	return supportsMixedCaseQuotedIdentifiers;
    }

    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
	return storesUpperCaseQuotedIdentifiers;
    }

    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
	return storesLowerCaseQuotedIdentifiers;
    }

    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
	return storesMixedCaseQuotedIdentifiers;
    }

    public String getIdentifierQuoteString() throws SQLException {
	return getIdentifierQuoteString;
    }

    public String getSQLKeywords() throws SQLException {
	return getSQLKeywords;
    }

    public String getNumericFunctions() throws SQLException {
	return getNumericFunctions;
    }

    public String getStringFunctions() throws SQLException {
	return getStringFunctions;
    }

    public String getSystemFunctions() throws SQLException {
	return getSystemFunctions;
    }

    public String getTimeDateFunctions() throws SQLException {
	return getTimeDateFunctions;
    }

    public String getSearchStringEscape() throws SQLException {
	return getSearchStringEscape;
    }

    public String getExtraNameCharacters() throws SQLException {
	return getExtraNameCharacters;
    }

    public boolean supportsAlterTableWithAddColumn() throws SQLException {
	return supportsAlterTableWithAddColumn;
    }

    public boolean supportsAlterTableWithDropColumn() throws SQLException {
	return supportsAlterTableWithDropColumn;
    }

    public boolean supportsColumnAliasing() throws SQLException {
	return supportsColumnAliasing;
    }

    public boolean nullPlusNonNullIsNull() throws SQLException {
	return nullPlusNonNullIsNull;
    }

    public boolean supportsConvert() throws SQLException {
	return supportsConvert;
    }

    public boolean supportsTableCorrelationNames() throws SQLException {
	return supportsTableCorrelationNames;
    }

    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
	return supportsDifferentTableCorrelationNames;
    }

    public boolean supportsExpressionsInOrderBy() throws SQLException {
	return supportsExpressionsInOrderBy;
    }

    public boolean supportsOrderByUnrelated() throws SQLException {
	return supportsOrderByUnrelated;
    }

    public boolean supportsGroupBy() throws SQLException {
	return supportsGroupBy;
    }

    public boolean supportsGroupByUnrelated() throws SQLException {
	return supportsGroupByUnrelated;
    }

    public boolean supportsGroupByBeyondSelect() throws SQLException {
	return supportsGroupByBeyondSelect;
    }

    public boolean supportsLikeEscapeClause() throws SQLException {
	return supportsLikeEscapeClause;
    }

    public boolean supportsMultipleResultSets() throws SQLException {
	return supportsMultipleResultSets;
    }

    public boolean supportsMultipleTransactions() throws SQLException {
	return supportsMultipleTransactions;
    }

    public boolean supportsNonNullableColumns() throws SQLException {
	return supportsNonNullableColumns;
    }

    public boolean supportsMinimumSQLGrammar() throws SQLException {
	return supportsMinimumSQLGrammar;
    }

    public boolean supportsCoreSQLGrammar() throws SQLException {
	return supportsCoreSQLGrammar;
    }

    public boolean supportsExtendedSQLGrammar() throws SQLException {
	return supportsExtendedSQLGrammar;
    }

    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
	return supportsANSI92EntryLevelSQL;
    }

    public boolean supportsANSI92IntermediateSQL() throws SQLException {
	return supportsANSI92IntermediateSQL;
    }

    public boolean supportsANSI92FullSQL() throws SQLException {
	return supportsANSI92FullSQL;
    }

    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
	return supportsIntegrityEnhancementFacility;
    }

    public boolean supportsOuterJoins() throws SQLException {
	return supportsOuterJoins;
    }

    public boolean supportsFullOuterJoins() throws SQLException {
	return supportsFullOuterJoins;
    }

    public boolean supportsLimitedOuterJoins() throws SQLException {
	return supportsLimitedOuterJoins;
    }

    public String getSchemaTerm() throws SQLException {
	return getSchemaTerm;
    }

    public String getProcedureTerm() throws SQLException {
	return getProcedureTerm;
    }

    public String getCatalogTerm() throws SQLException {
	return getCatalogTerm;
    }

    public boolean isCatalogAtStart() throws SQLException {
	return isCatalogAtStart;
    }

    public String getCatalogSeparator() throws SQLException {
	return getCatalogSeparator;
    }

    public boolean supportsSchemasInDataManipulation() throws SQLException {
	return supportsSchemasInDataManipulation;
    }

    public boolean supportsSchemasInProcedureCalls() throws SQLException {
	return supportsSchemasInProcedureCalls;
    }

    public boolean supportsSchemasInTableDefinitions() throws SQLException {
	return supportsSchemasInTableDefinitions;
    }

    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
	return supportsSchemasInIndexDefinitions;
    }

    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
	return supportsSchemasInPrivilegeDefinitions;
    }

    public boolean supportsCatalogsInDataManipulation() throws SQLException {
	return supportsCatalogsInDataManipulation;
    }

    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
	return supportsCatalogsInProcedureCalls;
    }

    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
	return supportsCatalogsInTableDefinitions;
    }

    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
	return supportsCatalogsInIndexDefinitions;
    }

    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
	return supportsCatalogsInPrivilegeDefinitions;
    }

    public boolean supportsPositionedDelete() throws SQLException {
	return supportsPositionedDelete;
    }

    public boolean supportsPositionedUpdate() throws SQLException {
	return supportsPositionedUpdate;
    }

    public boolean supportsSelectForUpdate() throws SQLException {
	return supportsSelectForUpdate;
    }

    public boolean supportsStoredProcedures() throws SQLException {
	return supportsStoredProcedures;
    }

    public boolean supportsSubqueriesInComparisons() throws SQLException {
	return supportsSubqueriesInComparisons;
    }

    public boolean supportsSubqueriesInExists() throws SQLException {
	return supportsSubqueriesInExists;
    }

    public boolean supportsSubqueriesInIns() throws SQLException {
	return supportsSubqueriesInIns;
    }

    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
	return supportsSubqueriesInQuantifieds;
    }

    public boolean supportsCorrelatedSubqueries() throws SQLException {
	return supportsCorrelatedSubqueries;
    }

    public boolean supportsUnion() throws SQLException {
	return supportsUnion;
    }

    public boolean supportsUnionAll() throws SQLException {
	return supportsUnionAll;
    }

    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
	return supportsOpenCursorsAcrossCommit;
    }

    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
	return supportsOpenCursorsAcrossRollback;
    }

    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
	return supportsOpenStatementsAcrossCommit;
    }

    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
	return supportsOpenStatementsAcrossRollback;
    }

    public int getMaxBinaryLiteralLength() throws SQLException {
	return getMaxBinaryLiteralLength;
    }

    public int getMaxCharLiteralLength() throws SQLException {
	return getMaxCharLiteralLength;
    }

    public int getMaxColumnNameLength() throws SQLException {
	return getMaxColumnNameLength;
    }

    public int getMaxColumnsInGroupBy() throws SQLException {
	return getMaxColumnsInGroupBy;
    }

    public int getMaxColumnsInIndex() throws SQLException {
	return getMaxColumnsInIndex;
    }

    public int getMaxColumnsInOrderBy() throws SQLException {
	return getMaxColumnsInOrderBy;
    }

    public int getMaxColumnsInSelect() throws SQLException {
	return getMaxColumnsInSelect;
    }

    public int getMaxColumnsInTable() throws SQLException {
	return getMaxColumnsInTable;
    }

    public int getMaxConnections() throws SQLException {
	return getMaxConnections;
    }

    public int getMaxCursorNameLength() throws SQLException {
	return getMaxCursorNameLength;
    }

    public int getMaxIndexLength() throws SQLException {
	return getMaxIndexLength;
    }

    public int getMaxSchemaNameLength() throws SQLException {
	return getMaxSchemaNameLength;
    }

    public int getMaxProcedureNameLength() throws SQLException {
	return getMaxProcedureNameLength;
    }

    public int getMaxCatalogNameLength() throws SQLException {
	return getMaxCatalogNameLength;
    }

    public int getMaxRowSize() throws SQLException {
	return getMaxRowSize;
    }

    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
	return doesMaxRowSizeIncludeBlobs;
    }

    public int getMaxStatementLength() throws SQLException {
	return getMaxStatementLength;
    }

    public int getMaxStatements() throws SQLException {
	return getMaxStatements;
    }

    public int getMaxTableNameLength() throws SQLException {
	return getMaxTableNameLength;
    }

    public int getMaxTablesInSelect() throws SQLException {
	return getMaxTablesInSelect;
    }

    public int getMaxUserNameLength() throws SQLException {
	return getMaxUserNameLength;
    }

    public int getDefaultTransactionIsolation() throws SQLException {
	return getDefaultTransactionIsolation;
    }

    public boolean supportsTransactions() throws SQLException {
	return supportsTransactions;
    }

    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
	return supportsDataDefinitionAndDataManipulationTransactions;
    }

    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
	return supportsDataManipulationTransactionsOnly;
    }

    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
	return dataDefinitionCausesTransactionCommit;
    }

    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
	return dataDefinitionIgnoredInTransactions;
    }

    public boolean supportsBatchUpdates() throws SQLException {
	return supportsBatchUpdates;
    }

    public boolean supportsSavepoints() throws SQLException {
	return supportsSavepoints;
    }

    public boolean supportsNamedParameters() throws SQLException {
	return supportsNamedParameters;
    }

    public boolean supportsMultipleOpenResults() throws SQLException {
	return supportsMultipleOpenResults;
    }

    public boolean supportsGetGeneratedKeys() throws SQLException {
	return supportsGetGeneratedKeys;
    }

    public int getDatabaseMajorVersion() throws SQLException {
	return getDatabaseMajorVersion;
    }

    public int getDatabaseMinorVersion() throws SQLException {
	return getDatabaseMinorVersion;
    }

    public int getJDBCMajorVersion() throws SQLException {
	return getJDBCMajorVersion;
    }

    public int getJDBCMinorVersion() throws SQLException {
	return getJDBCMinorVersion;
    }

    public int getSQLStateType() throws SQLException {
	return getSQLStateType;
    }

    public boolean locatorsUpdateCopy() throws SQLException {
	return locatorsUpdateCopy;
    }

    public boolean supportsStatementPooling() throws SQLException {
	return supportsStatementPooling;
    }

    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
	return supportsStoredFunctionsUsingCallSyntax;
    }

    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
	return autoCommitFailureClosesAllResultSets;
    }

    public int getResultSetHoldability() throws SQLException {
	return getResultSetHoldability;
    }

    @Override
    public String toString() {
	return "JdbcDatabaseMetaData [getURL=" + getURL + ", isReadOnly=" + isReadOnly
		+ ", allProceduresAreCallable=" + allProceduresAreCallable + ", allTablesAreSelectable="
		+ allTablesAreSelectable + ", getUserName=" + getUserName + ", nullsAreSortedHigh=" + nullsAreSortedHigh
		+ ", nullsAreSortedLow=" + nullsAreSortedLow + ", nullsAreSortedAtStart=" + nullsAreSortedAtStart
		+ ", nullsAreSortedAtEnd=" + nullsAreSortedAtEnd + ", getDatabaseProductName=" + getDatabaseProductName
		+ ", getDatabaseProductVersion=" + getDatabaseProductVersion + ", getDriverName=" + getDriverName
		+ ", getDriverVersion=" + getDriverVersion + ", getDriverMajorVersion=" + getDriverMajorVersion
		+ ", getDriverMinorVersion=" + getDriverMinorVersion + ", usesLocalFiles=" + usesLocalFiles
		+ ", usesLocalFilePerTable=" + usesLocalFilePerTable + ", supportsMixedCaseIdentifiers="
		+ supportsMixedCaseIdentifiers + ", storesUpperCaseIdentifiers=" + storesUpperCaseIdentifiers
		+ ", storesLowerCaseIdentifiers=" + storesLowerCaseIdentifiers + ", storesMixedCaseIdentifiers="
		+ storesMixedCaseIdentifiers + ", supportsMixedCaseQuotedIdentifiers="
		+ supportsMixedCaseQuotedIdentifiers + ", storesUpperCaseQuotedIdentifiers="
		+ storesUpperCaseQuotedIdentifiers + ", storesLowerCaseQuotedIdentifiers="
		+ storesLowerCaseQuotedIdentifiers + ", storesMixedCaseQuotedIdentifiers="
		+ storesMixedCaseQuotedIdentifiers + ", getIdentifierQuoteString=" + getIdentifierQuoteString
		+ ", getSQLKeywords=" + getSQLKeywords + ", getNumericFunctions=" + getNumericFunctions
		+ ", getStringFunctions=" + getStringFunctions + ", getSystemFunctions=" + getSystemFunctions
		+ ", getTimeDateFunctions=" + getTimeDateFunctions + ", getSearchStringEscape=" + getSearchStringEscape
		+ ", getExtraNameCharacters=" + getExtraNameCharacters + ", supportsAlterTableWithAddColumn="
		+ supportsAlterTableWithAddColumn + ", supportsAlterTableWithDropColumn="
		+ supportsAlterTableWithDropColumn + ", supportsColumnAliasing=" + supportsColumnAliasing
		+ ", nullPlusNonNullIsNull=" + nullPlusNonNullIsNull + ", supportsConvert=" + supportsConvert
		+ ", supportsTableCorrelationNames=" + supportsTableCorrelationNames
		+ ", supportsDifferentTableCorrelationNames=" + supportsDifferentTableCorrelationNames
		+ ", supportsExpressionsInOrderBy=" + supportsExpressionsInOrderBy + ", supportsOrderByUnrelated="
		+ supportsOrderByUnrelated + ", supportsGroupBy=" + supportsGroupBy + ", supportsGroupByUnrelated="
		+ supportsGroupByUnrelated + ", supportsGroupByBeyondSelect=" + supportsGroupByBeyondSelect
		+ ", supportsLikeEscapeClause=" + supportsLikeEscapeClause + ", supportsMultipleResultSets="
		+ supportsMultipleResultSets + ", supportsMultipleTransactions=" + supportsMultipleTransactions
		+ ", supportsNonNullableColumns=" + supportsNonNullableColumns + ", supportsMinimumSQLGrammar="
		+ supportsMinimumSQLGrammar + ", supportsCoreSQLGrammar=" + supportsCoreSQLGrammar
		+ ", supportsExtendedSQLGrammar=" + supportsExtendedSQLGrammar + ", supportsANSI92EntryLevelSQL="
		+ supportsANSI92EntryLevelSQL + ", supportsANSI92IntermediateSQL=" + supportsANSI92IntermediateSQL
		+ ", supportsANSI92FullSQL=" + supportsANSI92FullSQL + ", supportsIntegrityEnhancementFacility="
		+ supportsIntegrityEnhancementFacility + ", supportsOuterJoins=" + supportsOuterJoins
		+ ", supportsFullOuterJoins=" + supportsFullOuterJoins + ", supportsLimitedOuterJoins="
		+ supportsLimitedOuterJoins + ", getSchemaTerm=" + getSchemaTerm + ", getProcedureTerm="
		+ getProcedureTerm + ", getCatalogTerm=" + getCatalogTerm + ", isCatalogAtStart=" + isCatalogAtStart
		+ ", getCatalogSeparator=" + getCatalogSeparator + ", supportsSchemasInDataManipulation="
		+ supportsSchemasInDataManipulation + ", supportsSchemasInProcedureCalls="
		+ supportsSchemasInProcedureCalls + ", supportsSchemasInTableDefinitions="
		+ supportsSchemasInTableDefinitions + ", supportsSchemasInIndexDefinitions="
		+ supportsSchemasInIndexDefinitions + ", supportsSchemasInPrivilegeDefinitions="
		+ supportsSchemasInPrivilegeDefinitions + ", supportsCatalogsInDataManipulation="
		+ supportsCatalogsInDataManipulation + ", supportsCatalogsInProcedureCalls="
		+ supportsCatalogsInProcedureCalls + ", supportsCatalogsInTableDefinitions="
		+ supportsCatalogsInTableDefinitions + ", supportsCatalogsInIndexDefinitions="
		+ supportsCatalogsInIndexDefinitions + ", supportsCatalogsInPrivilegeDefinitions="
		+ supportsCatalogsInPrivilegeDefinitions + ", supportsPositionedDelete=" + supportsPositionedDelete
		+ ", supportsPositionedUpdate=" + supportsPositionedUpdate + ", supportsSelectForUpdate="
		+ supportsSelectForUpdate + ", supportsStoredProcedures=" + supportsStoredProcedures
		+ ", supportsSubqueriesInComparisons=" + supportsSubqueriesInComparisons
		+ ", supportsSubqueriesInExists=" + supportsSubqueriesInExists + ", supportsSubqueriesInIns="
		+ supportsSubqueriesInIns + ", supportsSubqueriesInQuantifieds=" + supportsSubqueriesInQuantifieds
		+ ", supportsCorrelatedSubqueries=" + supportsCorrelatedSubqueries + ", supportsUnion=" + supportsUnion
		+ ", supportsUnionAll=" + supportsUnionAll + ", supportsOpenCursorsAcrossCommit="
		+ supportsOpenCursorsAcrossCommit + ", supportsOpenCursorsAcrossRollback="
		+ supportsOpenCursorsAcrossRollback + ", supportsOpenStatementsAcrossCommit="
		+ supportsOpenStatementsAcrossCommit + ", supportsOpenStatementsAcrossRollback="
		+ supportsOpenStatementsAcrossRollback + ", getMaxBinaryLiteralLength=" + getMaxBinaryLiteralLength
		+ ", getMaxCharLiteralLength=" + getMaxCharLiteralLength + ", getMaxColumnNameLength="
		+ getMaxColumnNameLength + ", getMaxColumnsInGroupBy=" + getMaxColumnsInGroupBy
		+ ", getMaxColumnsInIndex=" + getMaxColumnsInIndex + ", getMaxColumnsInOrderBy="
		+ getMaxColumnsInOrderBy + ", getMaxColumnsInSelect=" + getMaxColumnsInSelect
		+ ", getMaxColumnsInTable=" + getMaxColumnsInTable + ", getMaxConnections=" + getMaxConnections
		+ ", getMaxCursorNameLength=" + getMaxCursorNameLength + ", getMaxIndexLength=" + getMaxIndexLength
		+ ", getMaxSchemaNameLength=" + getMaxSchemaNameLength + ", getMaxProcedureNameLength="
		+ getMaxProcedureNameLength + ", getMaxCatalogNameLength=" + getMaxCatalogNameLength
		+ ", getMaxRowSize=" + getMaxRowSize + ", doesMaxRowSizeIncludeBlobs=" + doesMaxRowSizeIncludeBlobs
		+ ", getMaxStatementLength=" + getMaxStatementLength + ", getMaxStatements=" + getMaxStatements
		+ ", getMaxTableNameLength=" + getMaxTableNameLength + ", getMaxTablesInSelect=" + getMaxTablesInSelect
		+ ", getMaxUserNameLength=" + getMaxUserNameLength + ", getDefaultTransactionIsolation="
		+ getDefaultTransactionIsolation + ", supportsTransactions=" + supportsTransactions
		+ ", supportsDataDefinitionAndDataManipulationTransactions="
		+ supportsDataDefinitionAndDataManipulationTransactions + ", supportsDataManipulationTransactionsOnly="
		+ supportsDataManipulationTransactionsOnly + ", dataDefinitionCausesTransactionCommit="
		+ dataDefinitionCausesTransactionCommit + ", dataDefinitionIgnoredInTransactions="
		+ dataDefinitionIgnoredInTransactions + ", supportsBatchUpdates=" + supportsBatchUpdates
		+ ", supportsSavepoints=" + supportsSavepoints + ", supportsNamedParameters=" + supportsNamedParameters
		+ ", supportsMultipleOpenResults=" + supportsMultipleOpenResults + ", supportsGetGeneratedKeys="
		+ supportsGetGeneratedKeys + ", getDatabaseMajorVersion=" + getDatabaseMajorVersion
		+ ", getDatabaseMinorVersion=" + getDatabaseMinorVersion + ", getJDBCMajorVersion="
		+ getJDBCMajorVersion + ", getJDBCMinorVersion=" + getJDBCMinorVersion + ", getSQLStateType="
		+ getSQLStateType + ", locatorsUpdateCopy=" + locatorsUpdateCopy + ", supportsStatementPooling="
		+ supportsStatementPooling + ", supportsStoredFunctionsUsingCallSyntax="
		+ supportsStoredFunctionsUsingCallSyntax + ", autoCommitFailureClosesAllResultSets="
		+ autoCommitFailureClosesAllResultSets + ", getResultSetHoldability=" + getResultSetHoldability + "]";
    }

    private static void debug(String s) {
	// Do not use ServerLogger! This class is both used by client and server
	// side
	if (DEBUG) {
	    // ServerLogger.getLogger().log(Level.WARNING, s);
	    System.out.println(s);
	}
    }
}
