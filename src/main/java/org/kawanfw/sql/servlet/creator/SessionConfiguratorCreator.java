/**
 *
 */
package org.kawanfw.sql.servlet.creator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.kawanfw.sql.api.server.session.DefaultSessionConfigurator;
import org.kawanfw.sql.api.server.session.SessionConfigurator;

/**
 * @author Nicolas de Pomereu
 *
 */
public class SessionConfiguratorCreator {

    private String sessionConfiguratorClassName = null;
    private SessionConfigurator sessionConfigurator = null;

    public SessionConfiguratorCreator(String theSessionConfiguratorClassName)
	    throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException, NoSuchMethodException, SecurityException {

	if (theSessionConfiguratorClassName != null && !theSessionConfiguratorClassName.isEmpty()) {
	    Class<?> c = Class.forName(theSessionConfiguratorClassName);
	    Constructor<?> constructor = c.getConstructor();
	    sessionConfigurator = (SessionConfigurator) constructor.newInstance();
	    this.sessionConfiguratorClassName = theSessionConfiguratorClassName;
	} else {
	    sessionConfigurator = new DefaultSessionConfigurator();
	    this.sessionConfiguratorClassName = sessionConfigurator.getClass().getName();
	}

    }

    public String getSessionConfiguratorClassName() {
        return sessionConfiguratorClassName;
    }

    public SessionConfigurator getSessionConfigurator() {
        return sessionConfigurator;
    }


}
