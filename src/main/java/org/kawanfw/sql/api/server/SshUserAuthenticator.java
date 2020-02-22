package org.kawanfw.sql.api.server;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.api.server.util.Ssh;
import org.kawanfw.sql.servlet.ServerSqlManager;
import org.kawanfw.sql.tomcat.TomcatStarterUtil;

/**
 * Allows to authenticate remotre client users using a SSH Server.
 * @author Nicolas de Pomereu
 *
 */
public class SshUserAuthenticator implements UserAuthenticator {

    private Properties properties = null;

    public SshUserAuthenticator() {

    }

    @Override
    public boolean login(String username, char[] password, String database, String ipAddress)
	    throws IOException, SQLException {

	if (properties == null) {
	    File file = ServerSqlManager.getAceqlServerProperties();
	    properties = TomcatStarterUtil.getProperties(file);
	}

	String host = properties.getProperty("sshAuthenticator.host");
	String portStr = properties.getProperty("sshAuthenticator.port");

	if (host == null) {
	    throw new NullPointerException("host property is null!");
	}

	if (portStr == null) {
	    portStr = "22";
	}

	if (!StringUtils.isNumeric(portStr)) {
	    throw new IllegalArgumentException(
		    "The sshAuthenticator.port property is not numeric: " + portStr);
	}

	int port = Integer.parseInt(portStr);

	boolean authenticated = Ssh.login(host, port, username, password);
	return authenticated;

    }

}
