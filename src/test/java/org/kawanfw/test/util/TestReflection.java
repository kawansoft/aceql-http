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

import java.lang.reflect.Method;

/**
 * @author Nicolas de Pomereu
 *
 */
public class TestReflection {


    @SuppressWarnings({ "rawtypes", "unchecked", "unused" })
    public static void testReflection() throws Exception {
	String myString = "Reflection!";
	System.out.println("myString: " + myString);

	Class clazz = myString.getClass();
	Method setter = clazz.getMethod("toLowerCase"); // You need to specify
							// the parameter types
	Object[] params = new Object[] { "New String" };

	// If you have a static method you can pass 'null' instead.
	myString = (String) setter.invoke(myString); // 'this' represents the
						     // class from were you
						     // calling that method.
	System.out.println("myString: " + myString);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	testReflection();
    }

}
