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