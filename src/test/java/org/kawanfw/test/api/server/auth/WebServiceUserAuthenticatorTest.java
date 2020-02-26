package org.kawanfw.test.api.server.auth;

import java.io.File;

import org.kawanfw.sql.api.server.auth.WebServiceUserAuthenticator;
import org.kawanfw.sql.servlet.ServerSqlManager;

public class WebServiceUserAuthenticatorTest {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	ServerSqlManager.setAceqlServerProperties(new File("I:\\_dev_awake\\aceql-http-main\\aceql-http\\conf\\aceql-server.properties"));
	WebServiceUserAuthenticator webServiceUserAuthenticator = new WebServiceUserAuthenticator();
	String username = "user1";
	String password = "password1";

	boolean logged = webServiceUserAuthenticator.login(username, password.toCharArray(), "database", "10.0.0.10");
	System.out.println("logged: " + logged);
    }
}
