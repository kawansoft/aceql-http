/**
 * 
 */
package org.kawanfw.test.util;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.kawanfw.sql.api.util.firewall.CloudmersiveApi;

/**
 * @author Nicolas de Pomereu
 *
 */
public class CloudmersiveApiTest {

    /**
     * @param args
     * @throws IOException
     * @throws InterruptedException
     * @throws SQLException
     */
    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
	CloudmersiveApi cloudmersiveApi = new CloudmersiveApi();
	File file = new File("I:\\_dev_awake\\aceql-http-main\\aceql-http\\conf_test\\cloudmersive.properties");
	cloudmersiveApi.connect(file);

	while (true) {
	    String sql = "select * from password where password = 'my_password' and 1 = 1";
	    System.out.println("sql: " + sql);
	    System.out.println("cloudmersiveApi.sqlInjectionDetect(sql): " + cloudmersiveApi.sqlInjectionDetect(sql));
	    Thread.sleep(60000);
	}

    }

}
