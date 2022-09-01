/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet;

import javax.servlet.http.HttpServletResponse;

/**
 * 
 * Wrapper to return an error http status. Allows logging.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class HttpStatus {

    protected HttpStatus() {

    }

    public static void set(HttpServletResponse response, int httpStatus,
	    String logMessage) {

	Trace.httpStatus(httpStatus + " " + logMessage);

	response.setStatus(httpStatus);
    }

}
