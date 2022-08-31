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
package org.kawanfw.sql.util;

import javax.servlet.http.HttpServletRequest;

public class IpUtil {

    protected IpUtil() {
	
    }

    /**
     * Gets user IP address of the servlet request by testing first X-FORWARDED-FOR
     * @param httpServletRequest the servlet request
     * @return the IP address of the servlet request
     */
    public static String getRemoteAddr(HttpServletRequest httpServletRequest) {
        String remoteAddr = "";

        if (httpServletRequest != null) {
            remoteAddr = httpServletRequest.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = httpServletRequest.getRemoteAddr();
            }
        }
        
        return remoteAddr;
        
    }
    
    

}