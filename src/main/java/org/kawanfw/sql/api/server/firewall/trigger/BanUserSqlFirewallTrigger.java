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
    create table aceql_banned_user
    (               
      username			varchar(254)	not null,    
      ip_address		varchar(254) 	not null, 
      sql_database		varchar(254)    not null,	
      sql_firewall_manager	varchar(254)    not null,
      sql_statement		varchar(4000)		,
      is_metadata		integer			, 
      dt_creation       	timestamp	not null		
    );
    create index idx_address_username on aceql_banned_user(username);
 * </code>
 * </pre>
 * 
 * Users inserted in the SQL table will not be allowed - by the AceQL Server -
 * to further access to the SQL database after the ban action.
 * <br>Any new access
 * attempt of a banned user will be blocked by returning the ambiguous error
 * message "Access Forbidden for Username". <br>
 * <br>
 * Activation of this trigger requires to define the
 * {@code BanUserSqlFirewallTrigger} as a value in the
 * {@code database.sqlFirewallTriggerClassNames} property of the
 * {@code aceql-server.properties} file. <br>
 * <br>
 * Example:<br>
 * {@code my_database.sqlFirewallTriggerClassNames=com.mycompany.MySqlFirewallTrigger1, com.mycomapny.MySqlFirewallTrigger2}
 * <p>
 * 
 * @author Nicolas de Pomereu
 * @since 11.0
 */

public class BanUserSqlFirewallTrigger implements SqlFirewallTrigger {

    /**
     * Inserts the {@code SqlEvent} info into the {@code aceql_banned_user} SQL table.
     */
    @Override
    public void runIfStatementRefused(SqlEvent sqlEvent, SqlFirewallManager sqlFirewallManager, Connection connection)
	    throws IOException, SQLException {

	// We use SQL int type for SQLEvent boolean values to be compatible with all db vendors
	
	String sql = "insert into aceql_banned_user values (?, ?, ?, ?, ?, ?, ?)";
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
