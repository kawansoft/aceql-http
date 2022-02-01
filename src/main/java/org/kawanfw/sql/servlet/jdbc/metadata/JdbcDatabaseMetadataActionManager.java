package org.kawanfw.sql.servlet.jdbc.metadata;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;

public interface JdbcDatabaseMetadataActionManager {

    void execute(HttpServletRequest request, HttpServletResponse response, OutputStream out,
	    List<SqlFirewallManager> sqlFirewallManagers, Connection connection) throws SQLException, IOException;

}