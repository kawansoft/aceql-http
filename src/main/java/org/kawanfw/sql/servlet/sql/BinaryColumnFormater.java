/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2021,  KawanSoft SAS
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
package org.kawanfw.sql.servlet.sql;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.util.SqlUtil;
import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.injection.classes.InjectedClassesStore;
import org.kawanfw.sql.util.FrameworkFileUtil;

/**
 * @author Nicolas de Pomereu
 *
 */
public class BinaryColumnFormater {

    private static final String NULL_STREAM = "NULL_STREAM";

    private ResultSet resultSet;
    private String productName;
    private int columnType;
    private int columnIndex;
    private String columnName;
    private Set<String> typeBigIntColumnNames;

    private String database;
    private String username;

    /**
     * Constructor.
     * @param request
     * @param resultSet
     * @param productName
     * @param columnType
     * @param columnIndex
     * @param columnName
     */
    public BinaryColumnFormater(HttpServletRequest request, ResultSet resultSet, String productName, int columnType, int columnIndex,
	    String columnName) {
	username = request.getParameter(HttpParameter.USERNAME);
	database = request.getParameter(HttpParameter.DATABASE);

	this.resultSet = resultSet;
	this.productName = productName;
	this.columnType = columnType;
	this.columnIndex = columnIndex;
	this.columnName = columnName;
    }

    /**
     * return true if the column is a binary type
     * @return true if it's a binary type
     * @throws IOException
     * @throws SQLException
     */
    public boolean isBinaryColumn()
	    throws SQLException, IOException {
	if (columnType == Types.BINARY || columnType == Types.VARBINARY || columnType == Types.LONGVARBINARY
		|| columnType == Types.BLOB) {
	    return true;
	} else {

	    // Special treatment for PostgreSQL OID which Java long/BIGINT type
	    if (isPostgreSQL() && columnType == Types.BIGINT) {
		if (typeBigIntColumnNames == null) {
		    Connection connection = resultSet.getStatement().getConnection();
		    typeBigIntColumnNames = PostgreSqlUtil.getTypeBigIntColumnNames(connection);
		}

		if (typeBigIntColumnNames.contains(columnName.trim().toLowerCase())) {
		    return true;
		}
	    }

	    return false;
	}
    }

    /**
     * the binary content is dumped in a server file that will be available for the
     * client the name of the file will be stored in the output stream ;
     * @return the formated binary column
     * @throws SQLException
     */
    public String formatAndReturnId() throws SQLException, IOException {
	String columnValueStr;

	String fileName = FrameworkFileUtil.getUniqueId() + ".blob";

	// Maybe null, we want to keep the info
	InputStream in = null;
	if (isTerradata()) {
	    in = resultSet.getBlob(columnIndex).getBinaryStream();
	}
	// For PostgreSQL columns OID columns have the BIGINT type
	else if (isPostgreSQL() && columnType == Types.BIGINT) {
	    in = PostgreSqlUtil.getPostgreSqlnputStream(resultSet, columnIndex);
	} else {
	    in = resultSet.getBinaryStream(columnIndex);
	}

	String hostFileName = null;

	DatabaseConfigurator databaseConfigurator = InjectedClassesStore.get().getDatabaseConfigurators().get(database);
	hostFileName = databaseConfigurator.getBlobsDirectory(username) + File.separator + fileName;
	//debug("formatBinaryColumn:outStream: " + hostFileName);

	try (OutputStream outStream = new BufferedOutputStream(new FileOutputStream(hostFileName));) {
	    if (in == null) {
		//debug("formatBinaryColumn: in == null");

		// DO NOTHING: just closing will create an empty file
		outStream.write(NULL_STREAM.getBytes());

	    } else {
		IOUtils.copy(in, outStream);
	    }
	} catch (IOException e) {
	    throw new SQLException(e);
	} finally {
	    if (in != null) {
		try {
		    in.close();
		} catch (Exception e) {
		    // e.printStackTrace();
		}
	    }
	}

	// The column value is a file name with a tag for identification
	columnValueStr = fileName;

	return columnValueStr;
    }

    private boolean isTerradata() {
	return productName.equals(SqlUtil.TERADATA) ? true : false;
    }

    private boolean isPostgreSQL() {
	return productName.equals(SqlUtil.POSTGRESQL) ? true : false;
    }

}
