/**
 *
 */
package org.kawanfw.test.api.server.auth;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import org.kawanfw.sql.api.server.auth.LdapUserAuthenticator;
import org.kawanfw.sql.servlet.ServerSqlManager;

/**
 * @author Nicolas de Pomereu
 *
 */
public class LdapUserAuthenticatorTest {

    /**
     * Tests a login using SSH.
     * @throws IOException
     * @throws SQLException
     */
    public static void test() throws IOException, SQLException {

	String user = "cn=read-only-admin,dc=example,dc=com";
	String password = "password";

	ServerSqlManager.setAceqlServerProperties(new File("I:\\_dev_awake\\aceql-http-main\\aceql-http\\conf\\aceql-server.properties"));
	LdapUserAuthenticator ldapUserAuthenticator = new LdapUserAuthenticator();

	boolean logged = ldapUserAuthenticator.login(user, password.toCharArray(), "database", "10.0.0.10");
	System.out.println(new Date() + " LdapUserAuthenticator logged: " + logged);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	test();
    }



}
