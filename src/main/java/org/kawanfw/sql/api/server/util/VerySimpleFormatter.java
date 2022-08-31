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
package org.kawanfw.sql.api.server.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * A very simple formatter on one line Stolen on
 * https://stackoverflow.com/questions/194765/how-do-i-get-java-logging-output-to-appear-on-a-single-line
 *
 * @author Nicolas de Pomereu
 *
 */
public class VerySimpleFormatter extends Formatter {

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    @Override
    public String format(final LogRecord record) {
	return String.format("%1$s %2$-7s %3$s\n", new SimpleDateFormat(PATTERN).format(new Date(record.getMillis())),
		record.getLevel().getName(),
		formatMessage(record));
    }

}
