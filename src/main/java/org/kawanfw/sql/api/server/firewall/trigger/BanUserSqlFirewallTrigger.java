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

package org.kawanfw.sql.api.server.firewall.trigger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;

/**
 * A trigger that will insert into the following table the info detected by the
 * {@code SqlFirewallManager} which fired the trigger:
 * 
 * <pre>
 * <code>
    create table banned_users
    (               
      username			varchar(254)	not null,    
      ip_address		varchar(254) 	not null, 
      sql_database		varchar(254)    not null,	
      sql_firewall_trigger	varchar(254)    not null,
      sql_statement		varchar(4000)		,
      is_metadata		integer			, 
      dt_creation       	timestamp	not null		
    );
    create index idx_address_username on banned_users(username);
 * </code>
 * </pre>
 * 
 * Users inserted in the SQL table will not be allowed - by the AceQL Server -
 * to further access to the SQL database after the ban action. Any new access
 * attempt of a banned user will be blocked by returning the ambiguous error
 * message "Access Forbidden for Username". <br>
 * <br>
 * Activation of this trigger requires to define the
 * {@code BanUserSqlFirewallTrigger} as a value in the
 * {@code database.sqlFirewallTriggerClassNames} property of the
 * {@code aceql-server.properties} file. <br>
 * <br>
 * Example:<br>
 * {@code my_database.sqlFirewallTriggerClassNames=BanUserSqlFirewallTrigger, com.mycompany.MyOtherSqlFirewallTrigger1, com.mycomapny.MyOtherSqlFirewallTrigger2}
 * <p>
 * 
 * @author Nicolas de Pomereu
 * @since 11.0
 */

public class BanUserSqlFirewallTrigger implements SqlFirewallTrigger {

    /**
     * Inserts the {@code SqlEvent} info into the {@code banned_users} SQL table.
     */
    @Override
    public void runIfStatementRefused(SqlEvent sqlEvent, SqlFirewallManager sqlFirewallManager, Connection connection)
	    throws IOException, SQLException {

	String sql = "insert into banned_users values (?, ?, ?, ?, ?, ?, ?)";
	try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
	    int i = 1;
	    preparedStatement.setString(i++, sqlEvent.getUsername());
	    preparedStatement.setString(i++, sqlEvent.getIpAddress());
	    preparedStatement.setString(i++, sqlEvent.getDatabase());
	    preparedStatement.setString(i++, sqlFirewallManager.getClass().getName());
	    preparedStatement.setString(i++, sqlEvent.getSql());
	    preparedStatement.setInt(i++, sqlEvent.isMetadataQuery() ? 1 : 0); // We don't use other type, not
									       // compatible with all db vendors
	    preparedStatement.setTimestamp(i++, new Timestamp(System.currentTimeMillis()));
	    preparedStatement.executeUpdate();
	}

    }

}
