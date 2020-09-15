/**
 *
 */
package org.kawanfw.test.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Nicolas de Pomereu
 *
 */
public class TestArray {

    /**
     *
     */
    public TestArray() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	// TODO Auto-generated method stub
	List<String> listArray = new ArrayList<String>();
	listArray.add("nicolas");
	listArray.add("de");
	listArray.add("Pom,ereu");
	String join = StringUtils.join(listArray, ",");
	System.out.println(join);


	String [] split = StringUtils.split(join, ",");
	for (int i = 0; i < split.length; i++) {
	    System.out.println(split[i]);
	}

    }

}
