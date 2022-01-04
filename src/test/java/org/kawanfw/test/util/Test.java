/**
 *
 */
package org.kawanfw.test.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.digest.config.EnvironmentStringDigesterConfig;
import org.jasypt.salt.StringFixedSaltGenerator;
import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.api.server.auth.JdbcPasswordEncryptor;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;

/**
 * @author Nicolas de Pomereu
 *
 */
public class Test {

    /**
     * @param args
     */

    // DatabaseConfigurator databaseConfigurator =
    // InjectedClassesStore.get().getDatabaseConfigurators().get(database);
    public static void main(String[] args) throws Exception {
	passwordEncryptor();
    }

    /**
     * @throws DatabaseConfigurationException
     * @throws IOException
     */
    public static void passwordEncryptor() throws DatabaseConfigurationException, IOException {
	File file = new File("I:\\_dev_awake\\aceql-http-main\\aceql-http\\conf\\aceql-server.properties");
	String password = "MyPassword";
	
	JdbcPasswordEncryptor jdbcPasswordEncryptor = new JdbcPasswordEncryptor(file);
	String encryptedPassword = jdbcPasswordEncryptor.encryptPassword(password);
	System.out.println(encryptedPassword);
    }

    /**
     * 
     */
    public static void passwordEncryptorRawTest() {
	System.out.println(new Date() + " " + System.currentTimeMillis());
	String inputPassword = "MyPassword";

	ConfigurablePasswordEncryptor passwordEncryptor = new ConfigurablePasswordEncryptor();
	passwordEncryptor.setStringOutputType("hexadecimal");

	EnvironmentStringDigesterConfig digesterConfig = new EnvironmentStringDigesterConfig();
	// digesterConfig.setAlgorithm("SHA-1");
	digesterConfig.setAlgorithm("SHA-256");
	digesterConfig.setSaltSizeBytes(0);
	digesterConfig.setSaltGenerator(new StringFixedSaltGenerator("abcdefgh"));
	digesterConfig.setIterations(1);
	passwordEncryptor.setConfig(digesterConfig);

	String encryptedPassword = passwordEncryptor.encryptPassword(inputPassword);
	System.out.println(new Date() + " " + encryptedPassword);

	if (passwordEncryptor.checkPassword(inputPassword, encryptedPassword.toLowerCase())) {
	    // correct!
	    System.out.println("correct!");
	} else {
	    // bad login!
	    System.out.println("bad login!");
	}

	System.out.println(new Date() + " " + System.currentTimeMillis());
    }

    /**
     * 
     */
    public static void extractVersion() {
	String version = "AceQL HTTP Community v9.0 - 26-Nov-2021";
	String newVersion = StringUtils.substringBetween(version, "v", "-");
	System.out.println(newVersion);
    }

    /**
     * @throws JSQLParserException
     */
    public static void testFullText() throws JSQLParserException {
	// System.out.println("Default Charset: " + Charset.defaultCharset());
	TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
	String statement = "SELECT _fulltext_ FROM _fulltext_";
	Statement parsedStatement = CCJSqlParserUtil.parse(statement); // Throws the Exception
	List<String> tables = tablesNamesFinder.getTableList(parsedStatement);
	System.out.println(tables);
    }

}
