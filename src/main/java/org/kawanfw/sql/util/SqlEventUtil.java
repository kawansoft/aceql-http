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
package org.kawanfw.sql.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nicolas de Pomereu
 *
 */
public class SqlEventUtil {

    /**
     * Transforms the Object parameters values into strings.
     * 
     * @param parameterValues the Object parameter values
     * @return the converted String parameter values
     */
    public static List<String> toString(List<Object> parameterValues) {
        List<String> list = new ArrayList<>();
        for (Object object : parameterValues) {
            list.add(String.valueOf(object));
        }
        return list;
    }


}
