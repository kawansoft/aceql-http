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
package org.kawanfw.sql.api.util.firewall;

import java.io.File;

public class IllegalFirstLineException extends IllegalArgumentException {

    private static final long serialVersionUID = -7299666473941182269L;

    public IllegalFirstLineException(File file, String s) {
	super(file.getName() + ":  " + s);
    }

    public IllegalFirstLineException(Throwable cause) {
	super(cause);
    }

    public IllegalFirstLineException(String message, Throwable cause) {
	super(message, cause);

    }

}
