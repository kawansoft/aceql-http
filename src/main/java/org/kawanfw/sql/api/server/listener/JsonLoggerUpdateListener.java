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
package org.kawanfw.sql.api.server.listener;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.SystemUtils;
import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.util.NoFormatter;
import org.kawanfw.sql.servlet.util.JsonLoggerUtil;
import org.kawanfw.sql.servlet.util.UpdateListenerUtil;
import org.kawanfw.sql.servlet.util.logging.FlattenLogger;

/**
 * Concrete implementation of {@code UpdateListener}. The
 * {@code updateActionPerformed(ClientEvent, Connection)} logs using JSON format
 * the {@code SqlEvent}.
 * 
 * @author Nicolas de Pomereu
 * @since 9.0
 */

public class JsonLoggerUpdateListener implements UpdateListener {

    private static Logger ACEQL_LOGGER = null;

    /**
     * Logs using JSON format the {@code ClientEvent} and the
     * {@code SqlFirewallManager} class name into a {@code Logger} with parameters:
     * <ul>
     * <li>Output file pattern:
     * {@code user.home/.kawansoft/log/JsonLoggerSqlFirewallTrigger_%d.log.%i} (example of file
     * created: {@code JsonLoggerSqlFirewallTrigger_2022-07-01.log.1}.).</li>
     * <li>Maximum File Size: 300Mb</li>
     * <li>Total Size Cap: 30Gb</li>
     * </ul>
     * These default values may be superseded by creating a
     * {@code JsonLoggerSqlFirewallTrigger.properties} file in
     * {@code user.home/.kawansoft/conf}. <br>
     * <br>
     * 
     * See the <a href=
     * file:../../../../../../../../resources/JsonLoggerSqlFirewallTrigger.properties>JsonLoggerSqlFirewallTrigger.properties</a>
     * format.<br>
     * <br>
     * <br>
     */
    @Override
    public void updateActionPerformed(SqlEvent evt, Connection connection) throws IOException, SQLException {
	String jsonString = UpdateListenerUtil.toJsonString(evt);
	getLogger().log(Level.WARNING, jsonString);
    }

    private Logger getLogger() throws IOException {
	if (ACEQL_LOGGER != null) {
	    return ACEQL_LOGGER;
	}

	File logDir = new File(SystemUtils.USER_HOME + File.separator + ".kawansoft" + File.separator + "log");
	logDir.mkdirs();

	String pattern = logDir.toString() + File.separator + JsonLoggerUtil.getSimpleName(this.getClass());

	Logger logger = Logger.getLogger(JsonLoggerUpdateListener.class.getName());
	ACEQL_LOGGER = new FlattenLogger(logger.getName(), logger.getResourceBundleName());

	Handler fh = new FileHandler(pattern, 1000 * 1024 * 1024, 3, true);
	fh.setFormatter(new NoFormatter());
	ACEQL_LOGGER.addHandler(fh);
	return ACEQL_LOGGER;

    }

}
