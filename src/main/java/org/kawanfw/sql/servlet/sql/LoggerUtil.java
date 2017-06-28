/**
 * 
 */
package org.kawanfw.sql.servlet.sql;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.servlet.HttpParameter;
import org.kawanfw.sql.servlet.ServerSqlManager;

/**
 * Logs all Exceptions thrown on server side, even user and application Exceptions (SQLException), for ease of debug if any problem.
 * @author Nicolas de Pomereu
 *
 */
public class LoggerUtil {

    /**
     * 
     */
    protected LoggerUtil() {

    }

    /**
     * Logs the SQL Exception with out internal AceQL errorMessage that details the reason of the SQLException to ease debug.
     * @param request
     * @param sqlException		
     * @param aceQLErrorMessage
     * @throws IOException 
     */
    public static void log(HttpServletRequest request, SQLException sqlException,
	    String aceQLErrorMessage) throws IOException {
	
	String database = request.getParameter(HttpParameter.DATABASE);

	DatabaseConfigurator databaseConfigurator = ServerSqlManager
		.getDatabaseConfigurator(database);
	
	Logger logger = databaseConfigurator.getLogger();	
	logger.log(Level.WARNING, "SQLException errorMessage: " + aceQLErrorMessage);
	logger.log(Level.WARNING, "SQLException: " + sqlException);
	
    }

    
    /**
     * Logs the thrown Exception.
     * @param request
     * @param exception
     * @throws IOException 
     */
    public static void log(HttpServletRequest request, Exception exception) throws IOException {
	String database = request.getParameter(HttpParameter.DATABASE);

	DatabaseConfigurator databaseConfigurator = ServerSqlManager
		.getDatabaseConfigurator(database);
	
	Logger logger = databaseConfigurator.getLogger();
	logger.log(Level.WARNING, "Exception: " + exception);
    }

}
