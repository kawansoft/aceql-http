/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.injection.classes.creator;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.SqlEventWrapper;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.api.server.firewall.trigger.BanUserSqlFirewallTrigger;
import org.kawanfw.sql.api.server.firewall.trigger.BeeperSqlFirewallTrigger;
import org.kawanfw.sql.api.server.firewall.trigger.JdbcLoggerSqlFirewallTrigger;
import org.kawanfw.sql.api.server.firewall.trigger.JsonLoggerSqlFirewallTrigger;
import org.kawanfw.sql.api.server.firewall.trigger.SqlFirewallTrigger;

public class SqlFirewallTriggersCreator {

    private static final boolean TRACE_ON_START = false;

    private static String[] PREDEFINED_CLASS_NAMES = { BanUserSqlFirewallTrigger.class.getSimpleName(),
	   BeeperSqlFirewallTrigger.class.getSimpleName(),
	    JdbcLoggerSqlFirewallTrigger.class.getSimpleName(), JsonLoggerSqlFirewallTrigger.class.getSimpleName() };

    private Set<String> sqlFirewallTriggerClassNames = new LinkedHashSet<>();
    private Set<SqlFirewallTrigger> sqlFirewallTriggerManagers = new LinkedHashSet<>();

    public SqlFirewallTriggersCreator(Set<String> sqlFirewallTriggerClassNames, String database,
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
			SqlFirewallManager sqlFirewallManager = null;
			sqlFirewallTriggerManager.runIfStatementRefused(sqlEvent, sqlFirewallManager,
				connection);
		    }
		}

		sqlFirewallTriggerClassName = sqlFirewallTriggerManager.getClass().getName();

		this.sqlFirewallTriggerManagers.add(sqlFirewallTriggerManager);
		this.sqlFirewallTriggerClassNames.add(sqlFirewallTriggerClassName);
	    }

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

    public Set<SqlFirewallTrigger> getSqlFirewallTriggers() {
	return sqlFirewallTriggerManagers;
    }

    public Set<String> getSqlFirewallTriggerClassNames() {
	return sqlFirewallTriggerClassNames;
    }

}
