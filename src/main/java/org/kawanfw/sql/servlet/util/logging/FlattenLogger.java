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
package org.kawanfw.sql.servlet.util.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A logger where msg log are "flattened", aka all CR/LF are removed.
 * @author Nicolas de Pomereu
 *
 */
public class FlattenLogger extends Logger {

    /**
     * Constructor
     * 
     * @param name               A name for the logger. This should be a
     *                           dot-separated name and should normally be based on
     *                           the package name or class name of the subsystem,
     *                           such as java.net or javax.swing. It may be null for
     *                           anonymous Loggers.
     * @param resourceBundleName name of ResourceBundle to be used for localizing
     *                           messages for this logger. May be null if none of
     *                           the messages require localization.
     */
    public FlattenLogger(String name, String resourceBundleName) {
	super(name, resourceBundleName);
    }

    @Override
    public void log(Level level, String msg) {
	String flatten;
	try {
	    StringFlattener stringFlattener = new StringFlattener(msg);
	    flatten = stringFlattener.flatten();
	    super.log(level, flatten);
	} catch (Throwable throwable) {
	    super.log(level, "CAN NOT FLATTEN MSG IN LOG: " + throwable.toString());
	    super.log(level, msg);
	}
    }
    
}
