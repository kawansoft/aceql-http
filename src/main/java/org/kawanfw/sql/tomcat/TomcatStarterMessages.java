/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2021,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.
 *
 * AceQL HTTP is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * AceQL HTTP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301  USA
 *
 * Any modifications to this file must keep this entire header
 * intact.
 */

package org.kawanfw.sql.tomcat;

import org.kawanfw.sql.servlet.injection.properties.ConfPropertiesUtil;
import org.kawanfw.sql.util.SqlTag;
import org.kawanfw.sql.util.TimestampUtil;
import org.kawanfw.sql.version.VersionWrapper;

/**
 * The begin and end messages of the start sequence...
 * @author Nicolas de Pomereu
 *
 */
public class TomcatStarterMessages {

    /**
     * To display at startup
     */
    public static void printBeginMessage() {
        System.out.println(SqlTag.SQL_PRODUCT_START + " Starting " + VersionWrapper.getName() + " Web Server at "
        	+ TimestampUtil.getHumanTimestampNoMillisNow() + "...");
        System.out.println(SqlTag.SQL_PRODUCT_START + " " + VersionWrapper.getServerVersion());
    }

    /**
     * Print the final message thats says Web Server is started
     * 
     * @param port the port in use. if -1, port is not displayed (for Real Tolcat
     *             usage)
     */
    public static void printFinalOkMessage(int port) {
        String runningMessage = SqlTag.SQL_PRODUCT_START + " " + VersionWrapper.getName() + " Web Server OK. ";
    
        if (port > -1) {
            runningMessage += "Running on port " + port + " ";
        }
    
        String StateModeMessage = ConfPropertiesUtil.isStatelessMode() ? "(Stateless Mode)" : "";
        runningMessage += StateModeMessage;
    
        System.out.println(runningMessage);
        System.out.println();
    }

    /**
     * Print the final message thats says Web Server is started, withtout the port
     */
    public static void printFinalOkMessage() {
        printFinalOkMessage(-1);
    }


}
