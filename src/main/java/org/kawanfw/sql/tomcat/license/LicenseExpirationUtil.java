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
package org.kawanfw.sql.tomcat.license;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class LicenseExpirationUtil {

    public static final long DAYS_MILLISECONDS = 3600 * 1000 * 24;
    public static final long DAYS_14_IN_MILLISECONDS = 14 * DAYS_MILLISECONDS;
    public static final long DAYS_30_IN_MILLISECONDS = 30 * DAYS_MILLISECONDS;

    public static Date stringToDate(String dateStr) throws ParseException {
	Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
	return date;
    }

    public static String dateToString(Date date) {
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	String dateStr = dateFormat.format(date);
	return dateStr;
    }

    public static long getDifferenceInDays(Date d1, Date d2) {
	long diffMilliseconds = d2.getTime() - d1.getTime();
	long remainingDays = TimeUnit.DAYS.convert(diffMilliseconds, TimeUnit.MILLISECONDS);
	return remainingDays;
    }
}
