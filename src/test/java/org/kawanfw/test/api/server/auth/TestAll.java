/**
 *
 */
package org.kawanfw.test.api.server.auth;

import java.util.Date;

/**
 * Test all built in Authenticator classes.
 * @author Nicolas de Pomereu
 *
 */
public class TestAll {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	System.out.println(new Date() + " Begin...");
	LdapUserAuthenticatorTest.test();
	SshUserAuthenticatorTest.test();
	WebServiceUserAuthenticatorTest.test();
	WindowsUserAuthenticatorTest.test();
	System.out.println(new Date() + " End...");
    }

}
