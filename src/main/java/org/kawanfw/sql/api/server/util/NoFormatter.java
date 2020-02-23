package org.kawanfw.sql.api.server.util;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * A nothing to do formater
 * 
 * @author Nicolas de Pomereu
 *
 */
public class NoFormatter extends Formatter {

    @Override
    public String format(final LogRecord record) {
	return String.format("%1$s\n", formatMessage(record));
    }
    
}