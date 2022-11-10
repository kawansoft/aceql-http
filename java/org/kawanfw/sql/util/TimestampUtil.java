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
package org.kawanfw.sql.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampUtil {
    
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String PATTERN_SHORT = "yyyy-MM-dd HH:mm:ss";
    /**
     * Protected
     */
    protected TimestampUtil() {

    }

    public static String getHumanTimestampNow() {
	return getHumanTimestamp(System.currentTimeMillis());
    }

    public static String getHumanTimestampNoMillisNow() {
	return getHumanTimestampNoMillis(System.currentTimeMillis());
    }
    
    public static String getHumanTimestamp(long timestamp) {
	return new SimpleDateFormat(PATTERN).format(new Date(timestamp));
    }
    
    public static String getHumanTimestampNoMillis(long timestamp) {
	return new SimpleDateFormat(PATTERN_SHORT).format(new Date(timestamp));
    }

}
