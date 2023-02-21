/*
 * Copyright (c)2023 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-02-21
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet;
/**
 * Trace options to ease some demos, debug, etc. Different from DEBUG
 * 
 * @author Nicolas de Pomereu
 *
 */

public class Trace {

    public static boolean TRACE_ON = false;

    /** Trace Token ID */
    public static boolean TRACE_SESSION_ID = true;

    /** Trace all Http Status on error */
    public static boolean TRACE_HTTP_STATUS = true;

    private Trace() {

    }

    public static void httpStatus(String s) {

	if (TRACE_ON && TRACE_HTTP_STATUS) {
	    System.out.println(s);
	}

    }

    public static void sessionId(String s) {

	if (TRACE_ON && TRACE_SESSION_ID) {
	    System.out.println(s);
	}
    }

}
