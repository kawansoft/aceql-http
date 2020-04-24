/**
 *
 */
package org.kawanfw.test.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import org.kawanfw.sql.servlet.sql.json_return.JsonUtil;

/**
 * @author Nicolas de Pomereu
 *
 */
public class Test {

    /**
     *
     */
    public Test() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	int cpt = 10000;
	boolean  prettyPrinting = true;
	boolean doFlush = true;

	System.out.println(new Date() + " Begin for " + cpt + " pretty printing: " + prettyPrinting);
	long begin = System.currentTimeMillis();
	OutputStream out = new BufferedOutputStream(new FileOutputStream(new File("c:\\test\\out.txt")));
	JsonGeneratorFactory jf = JsonUtil.getJsonGeneratorFactory(prettyPrinting);

	JsonGenerator gen = jf.createGenerator(out);
	gen.writeStartObject().write("status", "OK");
	gen.writeStartArray("column_types");
	for (int i = 0; i < cpt; i++) {
	    gen.write(i);

	    if (doFlush)
		gen.flush();
	}
	gen.writeEnd();
	gen.writeEnd();
	gen.close();
	long end = System.currentTimeMillis();;
	System.out.println(new Date() + " End: " + (end - begin));
    }

}
