/**
 *
 */
package org.kawanfw.test.util;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.digest.config.EnvironmentStringDigesterConfig;
import org.jasypt.salt.StringFixedSaltGenerator;
import org.jasypt.util.password.ConfigurablePasswordEncryptor;

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
    
    //	DatabaseConfigurator databaseConfigurator = InjectedClassesStore.get().getDatabaseConfigurators().get(database);
    public static void main(String[] args) throws Exception {

	System.out.println(new Date() + " " + System.currentTimeMillis());
	String inputPassword = "MyPassword";
	
//	StandardStringDigester digester = new StandardStringDigester();
//	digester.setAlgorithm("SHA-1");   // optionally set the algorithm
//	//digester.setIterations(5000);  // increase security by performing 50000 hashing iterations
//	//digester.setSaltSizeBytes(200);
//	String digest = digester.digest(inputPassword);
//	System.out.println(digest);

	/*
	BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
	String encryptedPassword = passwordEncryptor.encryptPassword(inputPassword);
	System.out.println(encryptedPassword);

	if (passwordEncryptor.checkPassword(inputPassword, encryptedPassword)) {
	    // correct!
	    System.out.println("correct!");
	} else {
	    // bad login!
	    System.out.println("bad login!");
	}
	*/
	
	ConfigurablePasswordEncryptor passwordEncryptor = new ConfigurablePasswordEncryptor();
	passwordEncryptor.setStringOutputType("hexadecimal");
	
	EnvironmentStringDigesterConfig digesterConfig = new EnvironmentStringDigesterConfig();
	//digesterConfig.setAlgorithm("SHA-1");
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
