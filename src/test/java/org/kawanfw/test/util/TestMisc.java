/*
 * Copyright (c)2022 KawanSoft S.A.S.
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-30
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.test.util;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.digest.config.EnvironmentStringDigesterConfig;
import org.jasypt.salt.StringFixedSaltGenerator;
import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.kawanfw.sql.api.server.DatabaseConfigurationException;
import org.kawanfw.sql.api.server.StatementAnalyzer;
import org.kawanfw.sql.api.server.auth.JdbcPasswordEncryptor;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;

/**
 * @author Nicolas de Pomereu
 *
 */
public class TestMisc {

    public static String CR_LF = System.getProperty("line.separator");
    
    /**
     * @param args
     */

    public static void main(String[] args) throws Exception {

	String sql = "select * from public.customer";
	List<Object> parameterValues = new ArrayList<>();
	StatementAnalyzer analyzer = new StatementAnalyzer(sql, parameterValues);
	List<String> tables = analyzer.getTables();
	System.out.println(tables);
	//testSqlParse();

    }

    /**
     * 
     */
    public static void testSqlParse() {
	String sql = "SELECT 	*         " + CR_LF + " from my_table where my_colum   =   ? ";
	System.out.println(sql);
	
	//String [] array1 = sql.split("\'");
	//System.out.println(Arrays.asList(array1));

	System.out.println();
	List<String> tokens = getTokens(sql);
	
	System.out.println(getTokens(sql));
	
	String finalString = tokensTrim(tokens);
	System.out.println(finalString);
	
	/*
	System.out.println();
	tokens = splitOnSpaces(sql);
	
	finalString = "";
	for (String token : tokens) {
	    if (token.isEmpty()) {
		continue;
	    }
	    finalString += token.trim() + " ";
	}
	System.out.println(finalString);
	*/
    }

    /**
     * @param tokens
     * @return
     */
    public static String tokensTrim(List<String> tokens) {
	String finalString = "";
	for (String token : tokens) {
	    if (token.isEmpty()) {
		continue;
	    }
	    finalString += token.trim() + " ";
	}
	return finalString;
    }

    @SuppressWarnings("unused")
    private static List<String> splitOnSpaces(String str) {
	//String str = "This is a string that \"will be\" highlighted when your 'regular expression' matches something.";
	str = str + " "; // add trailing space
	int len = str.length();
	Matcher m = Pattern.compile("((\"[^\"]+?\")|('[^']+?')|([^\\s]+?))\\s++").matcher(str);

	List<String> tokens = new ArrayList<>();
	
	for (int i = 0; i < len; i++)
	{
	    m.region(i, len);

	    if (m.lookingAt())
	    {
	        String s = m.group(1);

	        if ((s.startsWith("\"") && s.endsWith("\"")) ||
	            (s.startsWith("'") && s.endsWith("'")))
	        {
	            s = s.substring(1, s.length() - 1);
	        }

	        //System.out.println(i + ": \"" + s + "\"");
	        tokens.add(s);
	        i += (m.group(0).length() - 1);
	    }
	}
	
	return tokens;
	
    }

    public static List<String> getTokens(String str) {
	    List<String> tokens = new ArrayList<>();
	    StringTokenizer tokenizer = new StringTokenizer(str, " ");
	    while (tokenizer.hasMoreElements()) {
	        tokens.add(tokenizer.nextToken().trim());
	    }
	    return tokens;
	}
    
    /**
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static void reflectionTest() throws ClassNotFoundException, NoSuchMethodException, SecurityException,
	    InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	String string = "string";
	
	System.out.println(string.getClass().getSimpleName());

	Rectangle rectangle;
	Class<?> rectangleDefinition;
	Class<?>[] intArgsClass = new Class[] { int.class, int.class };
	Integer height = new Integer(12);
	Integer width = new Integer(34);
	Object[] intArgs = new Object[] { height, width };
	Constructor<?> constructor;

	rectangleDefinition = Class.forName("java.awt.Rectangle");
	constructor = rectangleDefinition.getConstructor(intArgsClass);
	Object object = constructor.newInstance(intArgs);

	rectangle = (Rectangle) object;
	System.out.println(rectangle.height + ", " + rectangle.width);
    }

    public static Object createObject(Constructor<?> constructor, Object[] arguments) {

	System.out.println("Constructor: " + constructor.toString());
	Object object = null;

	try {
	    object = constructor.newInstance(arguments);
	    System.out.println("Object: " + object.toString());
	    return object;
	} catch (InstantiationException e) {
	    System.out.println(e);
	} catch (IllegalAccessException e) {
	    System.out.println(e);
	} catch (IllegalArgumentException e) {
	    System.out.println(e);
	} catch (InvocationTargetException e) {
	    System.out.println(e);
	}
	return object;
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
