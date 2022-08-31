/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.injection.classes.creator;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kawanfw.sql.api.server.firewall.CsvRulesManager;
import org.kawanfw.sql.api.server.firewall.CsvRulesManagerNoReload;
import org.kawanfw.sql.api.server.firewall.DenyDatabaseWriteManager;
import org.kawanfw.sql.api.server.firewall.DenyDclManager;
import org.kawanfw.sql.api.server.firewall.DenyDdlManager;
import org.kawanfw.sql.api.server.firewall.DenyExceptOnWhitelistManager;
import org.kawanfw.sql.api.server.firewall.DenyMetadataQueryManager;
import org.kawanfw.sql.api.server.firewall.DenyOnBlacklistManager;
import org.kawanfw.sql.api.server.firewall.DenySqlInjectionManager;
import org.kawanfw.sql.api.server.firewall.DenySqlInjectionManagerAsync;
import org.kawanfw.sql.api.server.firewall.DenyStatementClassManager;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.util.FrameworkDebug;

public class SqlFirewallsCreator {

    private static boolean DEBUG = FrameworkDebug.isSet(SqlFirewallsCreator.class);

    private static String[] PREDEFINED_CLASS_NAMES = { CsvRulesManager.class.getSimpleName(),
	    CsvRulesManagerNoReload.class.getSimpleName(),
	    DenyDclManager.class.getSimpleName(), DenyDdlManager.class.getSimpleName(),
	    DenyDatabaseWriteManager.class.getSimpleName(), DenyExceptOnWhitelistManager.class.getSimpleName(),
	    DenyOnBlacklistManager.class.getSimpleName(), DenyMetadataQueryManager.class.getSimpleName(),
	    DenyStatementClassManager.class.getSimpleName(), 
	    DenySqlInjectionManager.class.getSimpleName(), DenySqlInjectionManagerAsync.class.getSimpleName(), 
    		};

    private List<String> sqlFirewallClassNames = new ArrayList<>();
    private List<SqlFirewallManager> sqlFirewallManagers = new ArrayList<>();

    public SqlFirewallsCreator(List<String> sqlFirewallClassNames)
	    throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, IOException {

	if (sqlFirewallClassNames != null && !sqlFirewallClassNames.isEmpty()) {

	    debug("sqlFirewallClassNames: " + sqlFirewallClassNames);

	    for (String sqlFirewallClassName : sqlFirewallClassNames) {

		sqlFirewallClassName = sqlFirewallClassName.trim();
		sqlFirewallClassName = getNameWithPackage(sqlFirewallClassName);

		debug("");
		debug("sqlFirewallClassName with Package to load: " + sqlFirewallClassName + ":");

		Class<?> c = Class.forName(sqlFirewallClassName);
		Constructor<?> constructor = c.getConstructor();
		SqlFirewallManager sqlFirewallManager = (SqlFirewallManager) constructor.newInstance();

		debug("sqlFirewallManager implementation loaded: " + sqlFirewallClassName);

		/**
		 * <pre>
		 * <code>
		if (TEST_FIREWALLS) {
		    try (Connection connection = databaseConfigurator.getConnection(database);) {
			List<Object> parameterValues = new ArrayList<>();
			// We call code just to verify it's OK:
			SqlEvent sqlEvent = SqlEventWrapper.sqlEventBuild("username", database, "127.0.0.1",
				"select * from table", false, parameterValues, false);
			sqlFirewallManager.allowSqlRunAfterAnalysis(sqlEvent, connection);
		    }
		}
		</code>
		 * </pre>
		 */

		sqlFirewallClassName = sqlFirewallManager.getClass().getName();

		this.sqlFirewallManagers.add(sqlFirewallManager);
		this.sqlFirewallClassNames.add(sqlFirewallClassName);
	    }

	    debug("End loop on sqlFirewallClassNames");

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
		String theClassNameNew = SqlFirewallManager.class.getPackage().getName() + "." + theClassName;
		return theClassNameNew;
	    }
	}

	return theClassName;
    }

    public List<SqlFirewallManager> getSqlFirewalls() {
	return sqlFirewallManagers;
    }

    public List<String> getSqlFirewallClassNames() {
	return sqlFirewallClassNames;
    }

    public void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
