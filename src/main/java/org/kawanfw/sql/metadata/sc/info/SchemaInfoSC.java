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
import java.util.Set;
import java.util.regex.Pattern;

import org.kawanfw.sql.api.util.SqlUtil;
import org.kawanfw.sql.metadata.AceQLMetaData;
import org.kawanfw.sql.metadata.util.FileWordReplacer;
import org.kawanfw.sql.version.VersionValues;

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

    private String SC_NAME= "SchemaCrawler";
    private String SC_VERSION = Version.getVersion();

    private String ACEQL_NAME = "AceQL";
    private String ACEQL_VERSION = VersionValues.VERSION;
    private Set<String> tableSet = new HashSet<>();

    /**
     * Constructor.
     * @param connection
     * @throws SQLException
     */
    public SchemaInfoSC(Connection connection) throws SQLException {
	if (connection == null) {
	    throw new NullPointerException("connection is null!");
	}

	this.connection = connection;
	AceQLMetaData aceQLMetaData = new AceQLMetaData(connection);
	List<String> tables = aceQLMetaData.getTableNames();
	for (String tableName : tables) {
	    tableSet.add(tableName);
	}
    }

    public SchemaInfoSC(Connection connection, SchemaInfoLevel schemaInfoLevel) throws SQLException {
	this(connection);
	if (schemaInfoLevel == null) {
	    throw new NullPointerException("schemaInfoLevel is null!");
	}

	this.connection = connection;
	this.schemaInfoLevel = schemaInfoLevel;


    }


    /***
    * Builds file with the chosen HTML/Text formatted schema, and for all tables or a single one.
    * @param file		the file to write the schema on.
    * @param outputFormat	AceQLOutputFormat.html or AceQLOutputFormat.text. Defaults to html if null.
    * @param table		the table to select. null for all.
     * @throws SQLException
     * @throws IOException
    */
    public void buildOnFile(File file, AceQLOutputFormat outputFormat, String table) throws SQLException, IOException  {

	if (file == null) {
	    throw new NullPointerException("file is null!");
	}

	if (table != null) {
	    if (! tableSet.contains(table.toLowerCase()) && ! tableSet.contains(table.toUpperCase())) {
		throw new SQLException("table does not exist:" + table);
	    }
	}

	File temp = new File(file.toString()+".tmp");

	if (outputFormat == null) {
	    outputFormat = AceQLOutputFormat.html;
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

	if (writer == null) {
	    throw new NullPointerException("file is null!");
	}

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
	    AceQLMetaData aceQLMetaData = new AceQLMetaData(connection);
	    String catalog = aceQLMetaData.getCatalogs().get(0);
	    Pattern pattern = Pattern.compile(catalog + ".dbo", Pattern.CASE_INSENSITIVE);
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
	} catch (Exception exception) {
	    if (exception instanceof IOException) {
		IOException ioexception = (IOException)exception;
		throw ioexception;
	    }
	    else {
		throw new SQLException(exception);
	    }
	}
    }

    private static OutputFormat getOutputFormatFromAceQL(AceQLOutputFormat outputFormat) {
	if (outputFormat.equals(AceQLOutputFormat.html)) {
	    return TextOutputFormat.html;
	}
	else {
	    return TextOutputFormat.text;
	}
    }

}
