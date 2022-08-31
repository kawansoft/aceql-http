/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */

package org.kawanfw.sql.tomcat;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class ProEditionServletNamesGetterWrap{

    /**
     * @param properties
     * @param licenseStatus
     * @return
     * @throws SQLException
     */
    public static Set<String> getServletsWrap(Properties properties) throws SQLException {
        
        String servlets = properties.getProperty("servlets");
    
        if (servlets == null || servlets.isEmpty()) {
            return new HashSet<>();
        }
    
        String[] servletArray = servlets.split(",");
    
        Set<String> servletSet = new HashSet<>();
        for (int i = 0; i < servletArray.length; i++) {
            servletSet.add(servletArray[i].trim());
        }
        return servletSet;
    }

}
