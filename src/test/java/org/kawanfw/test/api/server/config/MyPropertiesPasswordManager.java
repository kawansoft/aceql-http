/**
 * 
 */
package org.kawanfw.test.api.server.config;

import java.io.IOException;
import java.sql.SQLException;

import org.kawanfw.sql.api.server.auth.crypto.PropertiesPasswordManager;

/**
 * org.kawanfw.test.api.server.config.MyPropertiesPasswordManager
 * @author Nicolas de Pomereu
 *
 */
public class MyPropertiesPasswordManager implements PropertiesPasswordManager {

    @Override
    public char[] getPassword() throws IOException, SQLException {
	return "azerty*$".toCharArray();
    }

}
