/*
 * Copyright (c)2023 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-02-21
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.api.util.firewall;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;

import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.api.server.firewall.trigger.SqlFirewallTrigger;
import org.kawanfw.sql.servlet.injection.classes.InjectedClassesStore;

/**
 * @author Nicolas de Pomereu
 *
 */
public class SqlFirewallTriggerWrapper {

    public static void runIfStatementRefused(SqlEvent sqlEvent, SqlFirewallManager sqlFirewallManager,
	    Connection connection) throws IOException, SQLException {
	Objects.requireNonNull(sqlEvent, "sqlEvent cannot be null!");
	Objects.requireNonNull(sqlFirewallManager, "sqlFirewallManager cannot be null!");
	Objects.requireNonNull(connection, "connection cannot be null!");

	String database = sqlEvent.getDatabase();

	Set<SqlFirewallTrigger> sqlFirewallTriggers = InjectedClassesStore.get().getSqlFirewallTriggerMap()
		.get(database);

	for (SqlFirewallTrigger sqlFirewallTrigger : sqlFirewallTriggers) {
	    sqlFirewallTrigger.runIfStatementRefused(sqlEvent, sqlFirewallManager, connection);
	}

    }

}
