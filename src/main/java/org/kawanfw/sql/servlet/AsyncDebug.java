/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.kawanfw.sql.util.FrameworkDebug;

/**
 * @author Nicolas de Pomereu
 *
 */
public class AsyncDebug {

    public static boolean DEBUG = FrameworkDebug.isSet(AsyncDebug.class);

    public static void debug(String s) {
	if (DEBUG) {
	    System.out.println(getNowFormatted() + " " + s);
	}
    }

    public static String getNowFormatted() {
	Timestamp tsNow = new Timestamp(System.currentTimeMillis());
	DateFormat df = new SimpleDateFormat("yy/MM/dd HH:mm:ss.SSS");
	String now = df.format(tsNow);
	return now;
    }

}
