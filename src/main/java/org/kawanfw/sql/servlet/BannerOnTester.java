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
package org.kawanfw.sql.servlet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kawanfw.sql.api.server.firewall.trigger.BanUserSqlFirewallTrigger;
import org.kawanfw.sql.api.server.firewall.trigger.SqlFirewallTrigger;
import org.kawanfw.sql.servlet.injection.classes.InjectedClassesStore;

/**
 * @author Nicolas de Pomereu
 *
 */
public class BannerOnTester {
    
    private static Map<String, Boolean> databasesWithBanUserSqlFirewallTrigger = new HashMap<>();
	
    /**
     * Says if the {@code BanUserSqlFirewallTrigger} is activated for a designed database
     * @param database	the database to check for {@code BanUserSqlFirewallTrigger} activation
     * @return true if {@code BanUserSqlFirewallTrigger} has been activated for the database, else false.
     */
    public static boolean isBanUserSqlFirewallTriggerActivated(String database) {
	
    	if (databasesWithBanUserSqlFirewallTrigger.containsKey(database)) {
    	    return databasesWithBanUserSqlFirewallTrigger.get(database);
    	}

	List<SqlFirewallTrigger> sqlFirewallTriggers = InjectedClassesStore.get().getSqlFirewallTriggerMap()
		.get(database);

	for (SqlFirewallTrigger sqlFirewallTrigger : sqlFirewallTriggers) {
	    if (sqlFirewallTrigger instanceof BanUserSqlFirewallTrigger) {
		// BanUserSqlFirewallTrigger is activated for the database
		databasesWithBanUserSqlFirewallTrigger.put(database, true);
		return true;
	    }
	}
	
	// BanUserSqlFirewallTrigger is not activated
	databasesWithBanUserSqlFirewallTrigger.put(database, false);
	return false;
    }
    
}
