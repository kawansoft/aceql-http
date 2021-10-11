/*
 * This file is part of AceQL Client SDK.
 * AceQL Client SDK: Remote JDBC access over HTTP with AceQL HTTP.
 * Copyright (C) 2021,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kawanfw.sql.jdbc.metadata;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * Transport as sString array parameters for DatabaseMetaData methods execution.
 * @author Nicolas de Pomereu
 *
 */
public class ArrayTransporter {

    private static final String SEPARATOR = "|!|";

    /**
     * Static class
     */
    protected ArrayTransporter() {

    }


    public static String arrayToString(String[] stringArray) {
	if (stringArray == null) {
	    return "NULL";
	}

	List<String> listArray = Arrays.asList(stringArray);
	String join = StringUtils.join(listArray, SEPARATOR);
	return join;
    }


    public static String[] stringToStringArray(String join) {
	if (join == null || join.equals("NULL")) {
	    return null;
	}

	String [] split = StringUtils.split(join, SEPARATOR);
	return split;
    }

    public static String arrayToString(int[] intArray) {
	if (intArray == null) {
	    return "NULL";
	}

	List<Integer> listArray = Arrays.stream(intArray).boxed().collect(Collectors.toList());
	String join = StringUtils.join(listArray, SEPARATOR);
	return join;
    }

    public static int[] stringToIntArray(String join) {

	if (join == null || join.equals("NULL")) {
	    return null;
	}

	String [] split = StringUtils.split(join, SEPARATOR);
	int [] intArray = new int [split.length];

	for (int i = 0; i < split.length; i++) {
	    intArray[i]= Integer.parseInt(split[i]);
	}

	return intArray;

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	int [] intArray = { 1, 2, 3};
	String join = arrayToString(intArray);
	int [] intArray2 = stringToIntArray(join);

	for (int i = 0; i < intArray2.length; i++) {
	    System.out.println(intArray2[i]);
	}

	String [] stringArray = {"one", "two", "threee"};
	join = arrayToString(stringArray);

	String [] stringArray2 = stringToStringArray(join);
	System.out.println(Arrays.asList(stringArray2));
    }
}
