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
import org.kawanfw.sql.util.log.FlattenLogger;

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
     * Logs using JSON format the {@code ClientEvent} into a {@code Logger} with
     * parameters:
     * <ul>
     * <li>Output file pattern:
     * {@code user.home/.kawansoft/log/JsonLoggerSqlFirewallTrigger.log}.</li>
     * <li>Limit: 1Gb.</li>
     * <li>Count (number of files to use): 3.</li>
     * </ul>
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
