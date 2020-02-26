/**
 *
 */
package org.kawanfw.test.api.server.auth;

import java.io.File;

import org.kawanfw.sql.api.server.auth.SshUserAuthenticator;
import org.kawanfw.sql.servlet.ServerSqlManager;

/**
 * @author Nicolas de Pomereu
 *
 */
public class SshUserAuthenticatorTest {


    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	ServerSqlManager.setAceqlServerProperties(new File("I:\\_dev_awake\\aceql-http-main\\aceql-http\\conf\\aceql-server.properties"));
	SshUserAuthenticator sshUserAuthenticator = new SshUserAuthenticator();
	boolean logged = sshUserAuthenticator.login("user1", "password1".toCharArray(), "database", "10.0.0.10");
	System.out.println("logged: " + logged);
    }

}
