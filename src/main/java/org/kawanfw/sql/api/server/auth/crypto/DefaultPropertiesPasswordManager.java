/**
 * 
 */
package org.kawanfw.sql.api.server.auth.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import org.kawanfw.sql.util.FrameworkFileUtil;

/**
 * This default implementation will extract the password from the "password"
 * property of the file
 * {@code user.home/.kawansoft/properties_password_manager.properties}. <br/>
 * <br/>
 * This default implementation is provided asis, password is not secured if an
 * attacker gets access to the server. <br/>
 * Note that the {@code #getPassword()} will return null if the file does not
 * exists. <br/>
 * <br/>
 * 
 * @author Nicolas de Pomereu
 *
 */
public class DefaultPropertiesPasswordManager implements PropertiesPasswordManager {

    /**
     * Returns the password value of property contained in
     * {@code user.home/.kawansoft/properties_password_manager.properties}. <br>
     * Returns {@code null} if the file does not exist.
     */
    @Override
    public char[] getPassword() throws IOException, SQLException {
	FrameworkFileUtil.getUserHomeDotKawansoftDir();

	File file = new File(FrameworkFileUtil.getUserHomeDotKawansoftDir() + File.separator
		+ "properties_password_manager.properties");

	if (!file.exists()) {
	    return null;
	}

	Properties properties = new Properties();
	try (InputStream in = new FileInputStream(file);) {
	    properties.load(in);
	}

	String password = properties.getProperty("password");

	if (password == null || password.isEmpty()) {
	    throw new IOException("password property not defined in file: " + file);
	}

	return password.toCharArray();
    }

}
