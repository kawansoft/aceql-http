package org.kawanfw.test.api.server.auth;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import org.kawanfw.sql.api.server.auth.WebServiceUserAuthenticator;
import org.kawanfw.sql.servlet.ServerSqlManager;

public class WebServiceUserAuthenticatorTest {

    /**
     * Tests a login using a Web Service.
     * @throws IOException
     * @throws SQLException
     */
    public static void test() throws IOException, SQLException {
	ServerSqlManager.setAceqlServerProperties(new File("I:\\_dev_awake\\aceql-http-main\\aceql-http\\conf\\aceql-server.properties"));
	WebServiceUserAuthenticator webServiceUserAuthenticator = new WebServiceUserAuthenticator();
	String username = "user1";
	String password = "password1";

	boolean logged = webServiceUserAuthenticator.login(username, password.toCharArray(), "database", "10.0.0.10");
	System.out.println(new Date() + " WebServiceUserAuthenticator logged: " + logged);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	test();
    }

}
