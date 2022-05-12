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
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kawanfw.sql.api.server.DefaultDatabaseConfigurator;
import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.StatementNormalizer;
import org.kawanfw.sql.api.util.firewall.TextStatementsListLoader;
import org.kawanfw.sql.servlet.injection.properties.PropertiesFileStore;
import org.kawanfw.sql.util.FrameworkDebug;
import org.kawanfw.sql.util.SqlTag;
import org.kawanfw.sql.util.TimestampUtil;

/**
 * Firewall manager that denies incoming statements which are also sequentially stored in a text file.
 * 
 * The name of the text file that will be used by a database is:
 * <code>&lt;database&gt;_deny_statements.txt</code>, where database is the name
 * of the database declared in the {@code aceql.properties} files.<br>
 * The file must be located in the same directory as the
 * {@code aceql.properties} file used when starting the AceQL server.<br>
 * <br>
 * Each line of the text file must contain one statement, without quotes (") or
 * ending semicolon (;). <br>
 * <br>
 * Note that all statements will be "normalized" using
 * {@link StatementNormalizer} before comparison between the text file version and
 * the incoming one from client side.
 *
 * @author Nicolas de Pomereu
 * @since 11
 */
public class DenyStatementsOnBlackList extends DefaultSqlFirewallManager implements SqlFirewallManager {

    private static boolean DEBUG = FrameworkDebug.isSet(DenyStatementsOnBlackList.class);

    /** The denied statements Set per database */
    private Map<String, Set<String>> deniedStatementMap = new HashMap<>();
    
    private FileTime storedFileTime = null;

    /** Default behavior is to allow reload of statements list if text file is updated */
    protected boolean allowReload = true;

    /**
     * Allows the execution of the statement if it exists in
     * the:&nbsp; <code>&lt;database&gt;_deny_statements.txt</code> file.
     */
    @Override
    public boolean allowSqlRunAfterAnalysis(SqlEvent sqlEvent, Connection connection) throws IOException, SQLException {
	String database = sqlEvent.getDatabase();
	String sql = sqlEvent.getSql();
	// Normalize the statement
	sql = StatementNormalizer.getNormalized(sql);
	
	// Load all statements for database,  if not already done:
	loadStatementsToDeny(database);
	
	Set<String> deniedStatementsForDb = deniedStatementMap.get(database);
	if (deniedStatementsForDb == null || deniedStatementsForDb.isEmpty()) {
	    return true;
	}
	return ! deniedStatementsForDb.contains(sql);
    }


    /**
     * Load all statements for a database, once per server life. Can be dynamically reloaded if file is modified.
     * @param database	the database name
     * @throws FileNotFoundException
     * @throws SQLException
     * @throws IOException
     */
    private void loadStatementsToDeny(String database)
	    throws FileNotFoundException, SQLException, IOException {

	File textFile = getTextFile(database);
	BasicFileAttributes basicFileAttributes = Files.readAttributes(textFile.toPath(), BasicFileAttributes.class);
	FileTime currentFileTime = basicFileAttributes.lastModifiedTime();

	debug("storedFileTime : " + storedFileTime);
	debug("currentFileTime: " + currentFileTime);

	if (storedFileTime != null && !currentFileTime.equals(storedFileTime) && allowReload) {
	    deniedStatementMap = null;
	    String logInfo = TimestampUtil.getHumanTimestampNow() + " " + SqlTag.USER_CONFIGURATION
		    + " Reloading CsvRulesManager configuration file: " + textFile;
	    System.err.println(logInfo);
	    DefaultDatabaseConfigurator defaultDatabaseConfigurator = new DefaultDatabaseConfigurator();
	    Logger logger = defaultDatabaseConfigurator.getLogger();
	    logger.log(Level.WARNING, logInfo);
	    storedFileTime = currentFileTime;
	}

	if (deniedStatementMap == null || ! deniedStatementMap.containsKey(database)) {

	    if (deniedStatementMap == null) {
		deniedStatementMap = new HashMap<>();
	    }
	    
	    TextStatementsListLoader textStatementsListLoader = new TextStatementsListLoader(textFile);
	    textStatementsListLoader.load();
	    
	    deniedStatementMap.put(database, textStatementsListLoader.getNormalizedStatementSet());
	    storedFileTime = currentFileTime;
	}
    }

    /**
     * Returns the &lt;database&gt;_deny_statements.txt for
     * the passed database
     *
     * @param database
     * @throws FileNotFoundException
     */
    private static File getTextFile(String database) throws FileNotFoundException {
	File file = PropertiesFileStore.get();

	Objects.requireNonNull(file, "file cannot be null!");

	if (!file.exists()) {
	    throw new FileNotFoundException("The properties file does not exist: " + file);
	}
	File dir = PropertiesFileStore.get().getParentFile();
	File textFile = new File(dir + File.separator + database + "_deny_statements.txt");

	if (!textFile.exists()) {
	    throw new FileNotFoundException("The text files does not exist: " + textFile);
	}

	return textFile;

    }

    private void debug(String string) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + string);
	}
    }
}
