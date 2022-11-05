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
package org.kawanfw.sql.api.server.logging;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;

/**
 * Allows to create a neutral sl4fj Logger that thus will support many implementations.
 * @author Nicolas de Pomereu
 *
 */
public interface LoggerCreator {

    /**
     * Returns the Logger as sl4fj instance
     * @return the Logger as sl4fj instance
     * @throws IOException if any I/O error occurs at Logger creation
     */
    public Logger getLogger() throws IOException;

    /**
     * Return the elements of the {@code Logger}: name, fileNamePattern, etc. 
     * This allows to give admin users info about the {@code Logger} when stating the programe.
     * Implementation is free.
     * @return the elements of the Logger
     */
    public Map<String, String> getElements();
    
    

}
