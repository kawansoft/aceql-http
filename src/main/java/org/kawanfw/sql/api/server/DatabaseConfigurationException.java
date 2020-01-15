/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2020,  KawanSoft SAS
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
package org.kawanfw.sql.api.server;

/**
 *
 * Thrown to indicate that a Database configuration error has been detected.
 * This may happen if a {@link DatabaseConfigurator} throws an {@code Exception}
 * of if the <code>server-sql.properties</code> properties file contains
 * configuration errors.
 *
 * @author Nicolas de Pomereu
 * @since 1.0
 */

public class DatabaseConfigurationException extends IllegalArgumentException {

    private static final long serialVersionUID = 1959919487288293009L;

    /**
     * Constructs a new <code>DatabaseConfigurationException</code> with no
     * detail message.
     */

    public DatabaseConfigurationException() {
	super();
    }

    /**
     * Constructs a new <code>DatabaseConfigurationException</code> with the
     * specified detail message.
     *
     * @param s
     *            the detail message.
     */
    public DatabaseConfigurationException(String s) {
	super(s);
    }

    /**
     * Constructs a new <code>DatabaseConfigurationException</code> with the
     * specified detail message and cause.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link Throwable#getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link Throwable#getCause()} method).  (A <tt>null</tt> value
     *         is permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public DatabaseConfigurationException(String message, Throwable cause) {
	super(message, cause);
    }

}
