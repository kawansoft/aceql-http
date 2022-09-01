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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;

/**
 * A trigger that will {@code INSERT} the {@code SqlEvent} info and the
 * {@code sqlFirewallManager} class name into a {@code aceql_denied_request} SQL table.
 * <br>
 * Table structure is:
 * 
 * <pre>
 * <code>
create table aceql_denied_request
(               
  date_event		timestamp	not null,  
  username		varchar(254)	not null,  	  
  ip_address		varchar(254) 	not null, 
  sql_database		varchar(254)    not null,	
  sql_firewall_manager	varchar(254)    not null,
  sql_statement		varchar(4000)	not null,
  is_metadata		integer			, 
  is_prepared_statement	integer			,
  parameter_values	varchar(4000)	not null
);
 * </code>
 * </pre>
 * 
 * @author Nicolas de Pomereu
 * @since 11.0
 */

public class JdbcLoggerSqlFirewallTrigger implements SqlFirewallTrigger {

    /**
     * LInsert into the aceql_denied_request SQL table {@code ClientEvent} and the
     * {@code SqlFirewallManager} class name
     */
    @Override
    public void runIfStatementRefused(SqlEvent sqlEvent, SqlFirewallManager sqlFirewallManager, Connection connection)
	    throws IOException, SQLException {
	
	// We use SQL int type for SQLEvent boolean values to be compatible with all db vendors
	
	String sql = "insert into aceql_denied_request values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
	    int i = 1;
	    preparedStatement.setTimestamp(i++, new Timestamp(System.currentTimeMillis()));
	    preparedStatement.setString(i++, sqlEvent.getUsername());
	    preparedStatement.setString(i++, sqlEvent.getIpAddress());
	    preparedStatement.setString(i++, sqlEvent.getDatabase());
	    preparedStatement.setString(i++, sqlFirewallManager.getClass().getName());
	    preparedStatement.setString(i++, sqlEvent.getSql());
	    preparedStatement.setInt(i++, sqlEvent.isMetadataQuery() ? 1 : 0);
	    preparedStatement.setInt(i++, sqlEvent.isPreparedStatement() ? 1 : 0);
	    preparedStatement.setString(i++, sqlEvent.getParameterStringValues().toString());
	    preparedStatement.executeUpdate();
	}
    }

}
