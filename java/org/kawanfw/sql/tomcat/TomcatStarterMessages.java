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
package org.kawanfw.sql.tomcat;

import java.io.FileNotFoundException;
import java.io.IOException;

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
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public static void printBeginMessage() throws FileNotFoundException, IOException {
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
