/**
 *
 */
package org.kawanfw.sql.servlet.creator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.kawanfw.sql.api.server.UserAuthenticator;
import org.kawanfw.sql.api.server.auth.DefaultUserAuthenticator;

/**
 * @author Nicolas de Pomereu
 *
 */
public class UserAuthenticatorCreator {

    private UserAuthenticator userAuthenticator = null;
    private String userAuthenticatorClassName = null;

    /**
     * @param userAuthenticatorClassName
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     *
     */
    public UserAuthenticatorCreator(String theUserAuthenticatorClassName) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

	if (theUserAuthenticatorClassName != null && !theUserAuthenticatorClassName.isEmpty()) {
	    Class<?> c = Class.forName(theUserAuthenticatorClassName);
	    Constructor<?> constructor = c.getConstructor();
	    userAuthenticator = (UserAuthenticator) constructor.newInstance();
	    userAuthenticatorClassName = theUserAuthenticatorClassName;
	} else {
	    userAuthenticator = new DefaultUserAuthenticator();
	    userAuthenticatorClassName = userAuthenticator.getClass().getName();
	}

    }

    public UserAuthenticator getUserAuthenticator() {
        return userAuthenticator;
    }

    public String getUserAuthenticatorClassName() {
        return userAuthenticatorClassName;
    }

}
