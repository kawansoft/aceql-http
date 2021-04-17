
package org.kawanfw.sql.api.server.auth.jdbc;

import java.io.IOException;
import java.sql.SQLException;

import org.kawanfw.sql.api.server.DatabaseConfigurator;

/**
 * Allows to provide JDBC passwords for a database, instead of
 * {@code database.password} property values in the
 * {@code aceql-server.properties} file. <br/>
 * <br/>
 * There is no default class provided by AceQL that implements this interface:
 * any default implementation would just obfuscate the JDBC passwords and this
 * would create a security vulnerability.
 * <br/>
 * <br/>Note that this {@code JdbcPasswordsManager} mechanism does not apply for
 * Connections created though {@link DatabaseConfigurator#getConnection(String)} 
 * calls.
 * @author Nicolas de Pomereu
 * @since 7.0
 */
public interface JdbcPasswordsManager {

    /**
     * Returns for the passed {@code database} parameter the JBDC password to use
     * for {@code Connection} creations.<br/>
     * <br/>
     * Note that method will be called only if the {@code database.password}
     * property of {@code aceql-server.properties} is undefined or is set to void
     * (nothing after the =).
     * 
     * @param database the database for which to get the JDBC password
     * @return the JDBC password of the passed {@code database} parameter
     * @throws IOException  if an IOException occurs
     * @throws SQLException if a SQLException occurs
     */
    public char[] getPassword(String database) throws IOException, SQLException;

}
