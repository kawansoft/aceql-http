/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.sql;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.injection.classes.InjectedClassesStore;
import org.kawanfw.sql.util.FrameworkFileUtil;
import org.kawanfw.sql.util.HtmlConverter;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ClobColumnFormater {

    private static final String NULL_STREAM = "NULL_STREAM";
    public static String CR_LF = System.getProperty("line.separator");

    /** The html encoding to use for Clob downloads */
    private static  boolean HTML_ENCONDING_ON = true;

    private ResultSet resultSet;
    private int columnIndex;

    private String username;
    private String database;

    public ClobColumnFormater(HttpServletRequest request, ResultSet resultSet, int columnIndex) {
	username = request.getParameter(HttpParameter.USERNAME);
	database = request.getParameter(HttpParameter.DATABASE);
	this.resultSet = resultSet;
	this.columnIndex = columnIndex;
    }

    /**
     * the CLOB content is dumped in a server file that will be available for the
     * client the name of the file will be stored in the output stream ;
     *
     * @return the formated binary column id
     *
     * @throws SQLException
     */
    public String formatAndReturnId() throws SQLException, IOException {
	String columnValueStr;

	String fileName = FrameworkFileUtil.getUniqueId() + ".clob.txt";

	// Maybe null, we want to keep the info
	Reader reader = resultSet.getCharacterStream(columnIndex);

	DatabaseConfigurator databaseConfigurator = InjectedClassesStore.get().getDatabaseConfigurators().get(database);
	String hostFileName = databaseConfigurator.getBlobsDirectory(username) + File.separator + fileName;

	//debug("formatClobColumn:writer: " + hostFileName);

	if (reader == null) {
	    try (Writer writer = new BufferedWriter(new FileWriter(hostFileName));) {
		//debug("formatClobColumn.reader == null");
		writer.write(NULL_STREAM + CR_LF);
	    }
	} else {
	    BufferedReader br = new BufferedReader(reader);
	    writeClobFile(br, hostFileName);
	}

	// The column value is a file name with a tag for identification
	columnValueStr = fileName;
	return columnValueStr;
    }

    /**
     * Write a Clob file, html encoded or not
     *
     * @param br           the buffered reader on the clob
     * @param hostFileName the host file name to create in base 64
     */
    private void writeClobFile(BufferedReader br, String hostFileName) throws IOException {

	try (Writer writer = new BufferedWriter(new FileWriter(hostFileName));) {

	    String line = null;
	    while ((line = br.readLine()) != null) {

		if (HTML_ENCONDING_ON) {
		    line = HtmlConverter.fromHtml(line);
		}

		writer.write(line + CR_LF);
	    }
	}

    }


}
