/**
 * 
 */
package org.kawanfw.test.api.server.config;

import java.io.IOException;
import java.sql.SQLException;

import org.kawanfw.sql.api.server.auth.jdbc.JdbcPasswordsManager;

/**
 * org.kawanfw.test.api.server.config.MyJdbcPasswordsManager
 * @author Nicolas de Pomereu
 *
 */
public class MyJdbcPasswordsManager implements JdbcPasswordsManager {

    /**
     * 
     */
    public MyJdbcPasswordsManager() {
	// TODO Auto-generated constructor stub
    }

    @Override
    public char[] getPassword(String database) throws IOException, SQLException {
	if (database.equals("sampledb")) {
	    return "password1".toCharArray();
	}
	return null;
    }

}
