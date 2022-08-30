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
package org.kawanfw.sql.metadata.sc.info;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.api.util.SqlUtil;
import org.kawanfw.sql.metadata.AceQLMetaData;
import org.kawanfw.sql.metadata.util.FileWordReplacer;
import org.kawanfw.sql.version.VersionWrapper;

import schemacrawler.Version;
import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.options.TextOutputFormat;

/**
 * Allows to get full Schema Info with Schema Crawler. RequiresJava 8.
 *
 * @author Nicolas de Pomereu
 *
 */
public class SchemaInfoSC {

    private Connection connection = null;
    private SchemaInfoLevel schemaInfoLevel = SchemaInfoLevelBuilder.standard();

    private String SC_NAME = "SchemaCrawler";
    private String SC_VERSION = Version.getVersion();

    private String ACEQL_NAME = "AceQL";
    private String ACEQL_VERSION = VersionWrapper.getVersionNumber();
    private Set<String> tableSet = new HashSet<>();


    /**
     * Constructor.
     *
     * @param connection
     * @throws SQLException
     */
    public SchemaInfoSC(Connection connection) throws SQLException {
	this.connection = Objects.requireNonNull(connection, "connection cannot be null!");

	AceQLMetaData aceQLMetaData = new AceQLMetaData(connection);
	List<String> tables = aceQLMetaData.getTableNames();
	for (String tableName : tables) {
	    tableSet.add(tableName);
	}
    }

    public SchemaInfoSC(Connection connection, SchemaInfoLevel schemaInfoLevel) throws SQLException {
	this(connection);

	this.connection = connection;
	this.schemaInfoLevel = Objects.requireNonNull(schemaInfoLevel, "schemaInfoLevel cannot be null!");

    }

    /***
     * Builds file with the chosen HTML/Text formatted schema, and for all tables or
     * a single one.
     *
     * @param file         the file to write the schema on.
     * @param outputFormat AceQLOutputFormat.html or AceQLOutputFormat.text.
     *                     Defaults to html if null.
     * @param table        the table to select. null for all.
     * @throws SQLException
     * @throws IOException
     */
    public void buildOnFile(File file, final AceQLOutputFormat outputFormat, String table)
	    throws SQLException, IOException {

	Objects.requireNonNull(file, "file cannot be null!");

	if (table != null && (!tableSet.contains(table.toLowerCase()) && !tableSet.contains(table.toUpperCase()))) {
	    throw new SQLException("table does not exist:" + table);
	}

	File temp = new File(file.toString() + ".tmp");

	AceQLOutputFormat outputFormatNew = outputFormat;

	if (outputFormatNew == null) {
	    outputFormatNew = AceQLOutputFormat.html;
	}

	try (BufferedWriter writer = new BufferedWriter(new FileWriter(temp));) {
	    buildOnWriter(writer, outputFormat, table);
	}

	List<String> oldWords = new ArrayList<>();
	List<String> newWords = new ArrayList<>();

	oldWords.add(SC_NAME);
	oldWords.add(SC_VERSION);

	newWords.add(ACEQL_NAME);
	newWords.add(ACEQL_VERSION);

	FileWordReplacer fileWordReplacer = new FileWordReplacer(temp, file, oldWords, newWords);
	fileWordReplacer.replaceAll();

	temp.delete();

    }

    /**
     * Builds on Writer the Schema.
     *
     * @param writer       the Writer to nuild the schema on
     * @param outputFormat AceQLOutputFormat.html or AceQLOutputFormat.text.
     *                     Defaults to html if null.
     * @param table        the table to select. null for all.
     * @throws SQLException
     * @throws IOException
     */
    public void buildOnWriter(BufferedWriter writer, final AceQLOutputFormat outputFormat, String table)
	    throws SQLException, IOException {

	Objects.requireNonNull(writer, "writer cannot be null!");

	// Create the options
	final SchemaCrawlerOptionsBuilder optionsBuilder = SchemaCrawlerOptionsBuilder.builder()
		// Set what details are required in the schema - this affects the
		// time taken to crawl the schema
		.withSchemaInfoLevel(schemaInfoLevel);

	if (table != null) {
	    optionsBuilder.tableNamePattern(table);
	}

	SqlUtil sqlUtil = new SqlUtil(connection);

	// Exception for MySQL. T to: check for other databases.
	if (sqlUtil.isMySQL()) {
	    Pattern pattern = Pattern.compile("mysql|performance_schema", Pattern.CASE_INSENSITIVE);
	    optionsBuilder.includeSchemas(new RegularExpressionExclusionRule(pattern));
	}

	if (sqlUtil.isSQLServer()) {

	    DatabaseMetaData databaseMetaData = connection.getMetaData();
	    String databaseName = getSqlServerDatabaseName(databaseMetaData);
	    Pattern pattern = Pattern.compile(databaseName + ".dbo", Pattern.CASE_INSENSITIVE);
	    optionsBuilder.includeSchemas(new RegularExpressionInclusionRule(pattern));

//	    Pattern pattern = Pattern.compile("db_accessadmin|db_backupoperator|db_datareader|db_datawriter|db_ddladmin|db_denydatareader|db_denydatawriter|db_owner|db_securityadmin|guest|INFORMATION_SCHEMA|sys", Pattern.CASE_INSENSITIVE);
//	    optionsBuilder.includeSchemas(new RegularExpressionExclusionRule(pattern));
	}

	if (sqlUtil.isDB2() || sqlUtil.isOracle()) {
	    DatabaseMetaData databaseMetaData = connection.getMetaData();
	    String schema = databaseMetaData.getUserName();

	    Pattern pattern = Pattern.compile(schema, Pattern.CASE_INSENSITIVE);
	    optionsBuilder.includeSchemas(new RegularExpressionInclusionRule(pattern));
	}

	final SchemaCrawlerOptions options = optionsBuilder.toOptions();

	OutputFormat outputFormatSC = getOutputFormatFromAceQL(outputFormat);

	OutputOptions outputOptions = OutputOptionsBuilder.builder().withOutputFormat(outputFormatSC)
		.withOutputWriter(writer).toOptions();

	final String command = "schema";
	final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
	executable.setSchemaCrawlerOptions(options);
	executable.setOutputOptions(outputOptions);
	executable.setConnection(connection);
	try {
	    executable.execute();
	} catch (IOException ioException) {
	    throw ioException;
	} catch (Exception exception) {
	    throw new SQLException(exception);
	}
    }

    /**
     * @param databaseMetaData
     * @return
     * @throws SQLException
     */
    private static String getSqlServerDatabaseName(DatabaseMetaData databaseMetaData) throws SQLException {

	Objects.requireNonNull(databaseMetaData, "databaseMetaData cannot be null!");

	String databaseName = null;

	String [] urlElements = databaseMetaData.getURL().split(";");


	for (String element : urlElements) {
	    if (element.contains("databaseName=")) {
		databaseName = StringUtils.substringAfter(element, "databaseName=");
		break;
	    }
	}
	return databaseName;

    }
    private static OutputFormat getOutputFormatFromAceQL(AceQLOutputFormat outputFormat) {
	if (outputFormat.equals(AceQLOutputFormat.html)) {
	    return TextOutputFormat.html;
	} else {
	    return TextOutputFormat.text;
	}
    }

}
