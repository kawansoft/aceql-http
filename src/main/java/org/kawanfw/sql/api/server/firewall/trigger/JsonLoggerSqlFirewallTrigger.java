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
package org.kawanfw.sql.api.server.firewall.trigger;

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
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.api.server.util.NoFormatter;
import org.kawanfw.sql.servlet.util.JsonLoggerUtil;
import org.kawanfw.sql.servlet.util.SqlFirewallTriggerUtil;
import org.kawanfw.sql.servlet.util.logging.FlattenLogger;

/**
 * A trigger that will log using JSON format the {@code SqlEvent} info
 * and the {@code sqlFirewallManager} class name.
 * 
 * @author Nicolas de Pomereu
 * @since 11.0
 */

public class JsonLoggerSqlFirewallTrigger implements SqlFirewallTrigger {

    private static Logger ACEQL_LOGGER = null;

    /**
     * Logs using JSON format the {@code ClientEvent} and the
     * {@code SqlFirewallManager} class name into a {@code Logger} with parameters:
     * <ul>
     * <li>Output file pattern:
     * {@code user.home/.kawansoft/log/JsonLoggerSqlFirewallTrigger.log}.</li>
     * <li>Limit: 1Gb.</li>
     * <li>Count (number of files to use): 3.</li>
     * </ul>
     */
    @Override
    public void runIfStatementRefused(SqlEvent sqlEvent, SqlFirewallManager sqlFirewallManager, Connection connection)
	    throws IOException, SQLException {
	String jsonString = SqlFirewallTriggerUtil.toJsonString(sqlEvent, sqlFirewallManager);
	getLogger().log(Level.WARNING, jsonString);
    }

    private Logger getLogger() throws IOException {
	if (ACEQL_LOGGER != null) {
	    return ACEQL_LOGGER;
	}

	File logDir = new File(SystemUtils.USER_HOME + File.separator + ".kawansoft" + File.separator + "log");
	logDir.mkdirs();

	String pattern = logDir.toString() + File.separator + JsonLoggerUtil.getSimpleName(this.getClass());

	Logger logger = Logger.getLogger(JsonLoggerSqlFirewallTrigger.class.getName());
	ACEQL_LOGGER = new FlattenLogger(logger.getName(), logger.getResourceBundleName());

	Handler fh = new FileHandler(pattern, 1000 * 1024 * 1024, 3, true);
	fh.setFormatter(new NoFormatter());
	ACEQL_LOGGER.addHandler(fh);
	return ACEQL_LOGGER;
    }

}
