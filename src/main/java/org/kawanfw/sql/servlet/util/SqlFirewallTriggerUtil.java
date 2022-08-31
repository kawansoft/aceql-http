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
package org.kawanfw.sql.servlet.util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.servlet.sql.json_return.JsonUtil;
import org.kawanfw.sql.util.TimestampUtil;

public class SqlFirewallTriggerUtil {

    /**
     * Transforms the input {@code ClientEvent}  and {@code SqlFirewallManager} into  Json String.
     * 
     * @param evt the ClientEvent
     * @param sqlFirewallManager the firewall class name that caused the trigger
     * @return the output Json String
     */
    public static String toJsonString(SqlEvent evt, SqlFirewallManager sqlFirewallManager) {
    
        JsonGeneratorFactory jf = JsonUtil.getJsonGeneratorFactory(false);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JsonGenerator gen = jf.createGenerator(bos);
        gen.writeStartObject();
        gen.write("date", TimestampUtil.getHumanTimestampNow());
        gen.write("username", evt.getUsername());
        gen.write("database", evt.getDatabase());
        gen.write("ipAddress", evt.getIpAddress());
        
        if (sqlFirewallManager != null) {
            gen.write("sqlFirewallManager", sqlFirewallManager.getClass().getSimpleName());          
        }
        else {
            String nullString = null;
            gen.write("sqlFirewallManager", nullString);   
        }

    
        gen.write("sql", evt.getSql());
        gen.write("isPreparedStatement", evt.isPreparedStatement());
        gen.write("isMetadataQuery", evt.isMetadataQuery());
        
        gen.writeStartArray("parameterValues");
        List<String> values = SqlFirewallTriggerUtil.paramValuesAsList(evt.getParameterValues());
        for (String value : values) {
            gen.write(value);
        }
        gen.writeEnd();
    
        gen.writeEnd();
        gen.close();
        return bos.toString();
    }

    /**
     * Transforms the Object parameters values into strings
     * 
     * @param parameterValues the Object parameter values
     * @return the converted String parameter values
     */
    public static List<String> paramValuesAsList(List<Object> parameterValues) {
        List<String> list = new ArrayList<>();
        for (Object object : parameterValues) {
            list.add(String.valueOf(object));
        }
        return list;
    }
    
}
