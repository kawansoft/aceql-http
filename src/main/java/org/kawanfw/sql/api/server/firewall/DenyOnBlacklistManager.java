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
package org.kawanfw.sql.api.server.firewall;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kawanfw.sql.api.server.DefaultDatabaseConfigurator;
import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.StatementNormalizer;
import org.kawanfw.sql.api.util.firewall.TextStatementsListLoader;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.SqlTag;
import org.kawanfw.sql.util.TimestampUtil;

/**
 * Firewall manager that denies incoming SQL statements which are also
 * sequentially stored in a text file.
 * 
 * The name of the text file that will be used by a database is: &nbsp;
 * <code>&lt;database&gt;_deny_blacklist.txt</code>, where {@code database} is
 * the name of the database declared in the {@code aceql-server.properties} files.<br>
 * The file must be located in the same directory as the
 * {@code aceql-server.properties} file used when starting the AceQL server.<br>
 * <br>
 * Each line of the text file must contain one statement, without quotes (") or
 * ending semicolon (;). <br>
 * <br>
 * Note that all statements will be "normalized" using
 * {@link StatementNormalizer} before comparison between the statement in the
 * text file and the incoming one from client side.
 *
 * @author Nicolas de Pomereu
 * @since 11.0
 */
public class DenyOnBlacklistManager implements SqlFirewallManager {

    private static boolean DEBUG = FrameworkDebug.isSet(DenyOnBlacklistManager.class);

    /** The denied statements Set per database */
    private Map<String, Set<String>> statementMap = new HashMap<>();

    private FileTime storedFileTime = null;

    /**
     * Default behavior is to allow reload of statements list if text file is
     * updated
     */
    protected boolean allowReload = true;

    /**
     * Allows the execution of the statement if it does *not* exist in the:&nbsp;
     * <code>&lt;database&gt;_deny_blacklist.txt</code> file. <br>
     * The {@code database} prefix is the value of {@link SqlEvent#getDatabase()}.
     */
    @Override
    public boolean allowSqlRunAfterAnalysis(SqlEvent sqlEvent, Connection connection) throws IOException, SQLException {

	String database = sqlEvent.getDatabase();
	String sql = sqlEvent.getSql();

	// Normalize the statement
	sql = StatementNormalizer.getNormalized(sql);

	// Load all statements for database, if not already done:
	loadStatements(database, "_deny_blacklist.txt");

	Set<String> deniedStatementsForDb = statementMap.get(database);
	if (deniedStatementsForDb == null || deniedStatementsForDb.isEmpty()) {
	    return true;
	}
	return !deniedStatementsForDb.contains(sql);
    }

	
    /**
     * @return <code><b>true</b></code>. (Client programs will be allowed to create
     *         raw <code>Statement</code>, i.e. call statements without parameters.)
     */
    @Override
    public boolean allowStatementClass(String username, String database, Connection connection)
	    throws IOException, SQLException {
	return true;
    }

    /**
     * @return <code><b>true</b></code>. (Client programs will be allowed to call
     *         the Metadata Query API).
     */
    @Override
    public boolean allowMetadataQuery(String username, String database, Connection connection)
	    throws IOException, SQLException {
	return true;
    }

    /**
     * Load all statements for a database, once per server life. Can be dynamically
     * reloaded if file is modified.
     * 
     * @param database   the database name
     * @param fileSuffix the part of the file name after database
     * @throws FileNotFoundException
     * @throws SQLException
     * @throws IOException
     */
    private void loadStatements(String database, String fileSuffix)
	    throws FileNotFoundException, SQLException, IOException {

	File textFile = DenyExceptOnWhitelistManager.getTextFile(database, fileSuffix);
	BasicFileAttributes basicFileAttributes = Files.readAttributes(textFile.toPath(), BasicFileAttributes.class);
	FileTime currentFileTime = basicFileAttributes.lastModifiedTime();

	debug("");
	debug("textFile       : " + textFile);
	debug("allowReload    : " + allowReload);
	debug("storedFileTime : " + storedFileTime);
	debug("currentFileTime: " + currentFileTime);

	if (storedFileTime != null && !currentFileTime.equals(storedFileTime) && allowReload) {
	    
	    // Reset statements Map
	    statementMap = new HashMap<>();
	    
	    String logInfo = TimestampUtil.getHumanTimestampNow() + " " + SqlTag.USER_CONFIGURATION + " Reloading "
		    + this.getClass().getSimpleName() + " configuration file: " + textFile;
	    System.err.println(logInfo);
	    DefaultDatabaseConfigurator defaultDatabaseConfigurator = new DefaultDatabaseConfigurator();
	    Logger logger = defaultDatabaseConfigurator.getLogger();
	    logger.log(Level.WARNING, logInfo);
	    storedFileTime = currentFileTime;
	}

	if (!statementMap.containsKey(database)) {

	    if (!textFile.exists()) {
		throw new FileNotFoundException(
			"The file that contains the statements to blacklist does not exist: " + textFile);
	    }

	    TextStatementsListLoader textStatementsListLoader = new TextStatementsListLoader(textFile);
	    textStatementsListLoader.load();

	    statementMap.put(database, textStatementsListLoader.getNormalizedStatementSet());
	    storedFileTime = currentFileTime;
	}
    }


    private void debug(String string) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + this.getClass().getSimpleName() + " " + string);
	}
    }
}
