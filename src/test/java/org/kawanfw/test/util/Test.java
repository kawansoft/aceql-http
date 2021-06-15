/**
 *
 */
package org.kawanfw.test.util;

import org.kawanfw.sql.api.server.util.SimpleSha1;

/**
 * @author Nicolas de Pomereu
 *
 */
public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	String hash = SimpleSha1.sha1(" ", true);
	System.out.println("hash: " + hash);
	
    }

}
