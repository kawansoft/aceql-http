/**
 *
 */
package org.kawanfw.test.api.server.auth;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.kawanfw.sql.api.server.auth.WindowsUserAuthenticator;
import org.kawanfw.sql.servlet.ServerSqlManager;

/**
 * @author Nicolas de Pomereu
 *
 */
public class WindowsUserAuthenticatorTest {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	ServerSqlManager.setAceqlServerProperties(new File("I:\\_dev_awake\\aceql-http-main\\aceql-http\\conf\\aceql-server.properties"));
	WindowsUserAuthenticator windowsUserAuthenticator = new WindowsUserAuthenticator();
	String username = "Nicolas de Pomereu";
	String password = FileUtils.readFileToString(new File("I:\\__NDP\\_MyPasswords\\login.txt"), "UTF-8");

	boolean logged = windowsUserAuthenticator.login(username, password.toCharArray(), "database", "10.0.0.10");
	System.out.println("logged: " + logged);
    }

}
