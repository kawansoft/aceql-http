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

package org.kawanfw.sql.servlet.injection.classes.creator;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.SqlEventWrapper;
import org.kawanfw.sql.api.server.firewall.DefaultSqlFirewallManager;
import org.kawanfw.sql.api.server.firewall.trigger.BanUserSqlFirewallTrigger;
import org.kawanfw.sql.api.server.firewall.trigger.BeeperSqlFirewallTrigger;
import org.kawanfw.sql.api.server.firewall.trigger.DefaultSqlFirewallTrigger;
import org.kawanfw.sql.api.server.firewall.trigger.JdbcLoggerSqlFirewallTrigger;
import org.kawanfw.sql.api.server.firewall.trigger.JsonLoggerSqlFirewallTrigger;
import org.kawanfw.sql.api.server.firewall.trigger.SqlFirewallTrigger;

public class SqlFirewallTriggersCreator {

    private static final boolean TRACE_ON_START = false;

    private static String[] PREDEFINED_CLASS_NAMES = { BanUserSqlFirewallTrigger.class.getSimpleName(),
	    DefaultSqlFirewallTrigger.class.getSimpleName(), BeeperSqlFirewallTrigger.class.getSimpleName(),
	    JdbcLoggerSqlFirewallTrigger.class.getSimpleName(), JsonLoggerSqlFirewallTrigger.class.getSimpleName() };

    private List<String> sqlFirewallTriggerClassNames = new ArrayList<>();
    private List<SqlFirewallTrigger> sqlFirewallTriggerManagers = new ArrayList<>();

    public SqlFirewallTriggersCreator(List<String> sqlFirewallTriggerClassNames, String database,
	    DatabaseConfigurator databaseConfigurator)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, IOException {

	if (sqlFirewallTriggerClassNames != null && !sqlFirewallTriggerClassNames.isEmpty()) {

	    for (String sqlFirewallTriggerClassName : sqlFirewallTriggerClassNames) {

		sqlFirewallTriggerClassName = sqlFirewallTriggerClassName.trim();
		sqlFirewallTriggerClassName = getNameWithPackage(sqlFirewallTriggerClassName);

		Class<?> c = Class.forName(sqlFirewallTriggerClassName);
		Constructor<?> constructor = c.getConstructor();
		SqlFirewallTrigger sqlFirewallTriggerManager = (SqlFirewallTrigger) constructor.newInstance();

		if (TRACE_ON_START) {
		    try (Connection connection = databaseConfigurator.getConnection(database);) {
			List<Object> parameterValues = new ArrayList<>();
			parameterValues.add("value1");
			parameterValues.add("value2");

			// We call code just to verify it's OK:
			SqlEvent sqlEvent = SqlEventWrapper.sqlEventBuild("username", database, "127.0.0.1",
				"select * from table", false, parameterValues, false);
			DefaultSqlFirewallManager defaultSqlFirewallManager = new DefaultSqlFirewallManager();
			sqlFirewallTriggerManager.runIfStatementRefused(sqlEvent, defaultSqlFirewallManager,
				connection);
		    }
		}

		sqlFirewallTriggerClassName = sqlFirewallTriggerManager.getClass().getName();

		this.sqlFirewallTriggerManagers.add(sqlFirewallTriggerManager);
		this.sqlFirewallTriggerClassNames.add(sqlFirewallTriggerClassName);
	    }

	} else {
	    SqlFirewallTrigger sqlFirewallTriggerManager = new DefaultSqlFirewallTrigger();
	    String sqlFirewallTriggerClassName = sqlFirewallTriggerManager.getClass().getName();

	    this.sqlFirewallTriggerManagers.add(sqlFirewallTriggerManager);
	    this.sqlFirewallTriggerClassNames.add(sqlFirewallTriggerClassName);
	}
    }

    /**
     * Allows to add automatically the package for predefined classes
     *
     * @param theClassName
     * @return
     */
    private static String getNameWithPackage(final String theClassName) {

	for (int i = 0; i < PREDEFINED_CLASS_NAMES.length; i++) {
	    if (PREDEFINED_CLASS_NAMES[i].equals(theClassName)) {
		// Add prefix package
		String theClassNameNew = SqlFirewallTrigger.class.getPackage().getName() + "." + theClassName;
		return theClassNameNew;
	    }
	}

	return theClassName;
    }

    public List<SqlFirewallTrigger> getSqlFirewallTriggers() {
	return sqlFirewallTriggerManagers;
    }

    public List<String> getSqlFirewallTriggerClassNames() {
	return sqlFirewallTriggerClassNames;
    }

}
