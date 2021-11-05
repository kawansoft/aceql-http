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

package org.kawanfw.sql.util.log;

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
	StringFlattener stringFlattener = new StringFlattener(msg);
	String flatten = stringFlattener.flatten();
	super.log(level, flatten);
    }
    
}
