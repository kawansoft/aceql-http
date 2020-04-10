/**
 *
 */
package org.kawanfw.sql.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;

/**
 * @author Nicolas de Pomereu
 *
 */
public class Base64Test {

    /**
     *
     */
    public Base64Test() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {

	System.out.println(new Date() + " Begin...");
	File file = new File("c:\\test\\proust.txt");
	String text = FileUtils.readFileToString(file, "UTF-8");
	System.out.println(text);

	String originalInput = text;
	org.apache.commons.codec.binary.Base64 base64 = new org.apache.commons.codec.binary.Base64(80);
	String encodedString = new String(base64.encode(originalInput.getBytes()));
	System.out.println(encodedString);
    }

}
