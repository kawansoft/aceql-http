/*
 * Copyright (c)2023 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-02-21
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
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
     *         {@link Throwable#getCause()} method).  (A {@code null} value
     *         is permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public DatabaseConfigurationException(String message, Throwable cause) {
	super(message, cause);
    }

}
