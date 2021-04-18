
package org.kawanfw.sql.api.server.auth.jdbc;

import java.io.IOException;
import java.net.PasswordAuthentication;
import java.sql.SQLException;

import org.kawanfw.sql.api.server.DatabaseConfigurator;

/**
 * Allows to provide JDBC username & passwords for a database, instead of
 * {@code database.username} and {@code database.password} property values in
 * the {@code aceql-server.properties} file. <br/>
 * <br/>
 * There is no default class provided by AceQL that implements this interface:
 * any default implementation would just obfuscate the JDBC passwords and this
 * would create a security vulnerability. <br/>
 * <br/>
 * Note that this {@code JdbcCredentialsManager} mechanism does not apply for
 * Connections created though {@link DatabaseConfigurator#getConnection(String)}
 * calls.
 * 
 * @author Nicolas de Pomereu
 * @since 7.0
 */
public interface JdbcCredentialsManager {

    /**
     * Returns for the passed {@code database} parameter the JBDC username and
     * password to use for {@code Connection} creations.<br/>
     * <br/>
     * Note that the method will be called only if the {@code database.username} and
     * {@code database.password} properties of {@code aceql-server.properties} are
     * undefined.
     * 
     * @param database the database for which to get the JDBC credentials
     * @return the JDBC credentials for the passed {@code database} parameter
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     */
    public PasswordAuthentication getPasswordAuthentication(String database) throws IOException, SQLException;

}
