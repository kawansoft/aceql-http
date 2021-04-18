/**
 * 
 */
package org.kawanfw.test.api.server.config;

import java.io.IOException;
import java.net.PasswordAuthentication;
import java.sql.SQLException;

import org.kawanfw.sql.api.server.auth.jdbc.JdbcCredentialsManager;

/**
 * org.kawanfw.test.api.server.config.MyJdbcPasswordsManager
 * @author Nicolas de Pomereu
 *
 */
public class MyJdbcPasswordsManager implements JdbcCredentialsManager {

    /**
     * 
     */
    public MyJdbcPasswordsManager() {
	// TODO Auto-generated constructor stub
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication(String database) throws IOException, SQLException {
	if (database.equals("sampledb")) {
	    return new PasswordAuthentication("user1", "password".toCharArray());
	}
	return null;
    }

}
