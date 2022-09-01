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
package org.kawanfw.sql.api.server.logging;

import java.util.Map;

import org.slf4j.Logger;

/**
 * Allows to create a neutral slf4 Logger that thus will support many implementations.
 * @author Nicolas de Pomereu
 *
 */
public interface LoggerCreator {

    /**
     * Returns the Logger as sl4j instance
     * @return the Logger as sl4j instance
     */
    public Logger getLogger();

    /**
     * Return the elements of the {@code Logger}: name, fileNamePattern, etc. 
     * This allows to give admin users info about the {@code Logger} when stating the firewall.
     * Implementation is free.
     * @return the elements of the Logger
     */
    public Map<String, String> getElements();

}
