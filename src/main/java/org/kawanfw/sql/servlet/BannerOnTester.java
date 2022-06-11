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
