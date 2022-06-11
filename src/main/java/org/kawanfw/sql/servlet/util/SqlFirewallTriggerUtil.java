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
        
        gen.write("sqlFirewallManager", sqlFirewallManager.getClass().getSimpleName());
    
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
