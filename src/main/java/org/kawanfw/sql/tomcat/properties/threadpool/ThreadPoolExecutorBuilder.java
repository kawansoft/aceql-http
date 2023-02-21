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
package org.kawanfw.sql.tomcat.properties.threadpool;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.concurrent.ThreadPoolExecutor;

import org.kawanfw.sql.api.server.DatabaseConfigurationException;

public interface ThreadPoolExecutorBuilder {

    /**
     * Creates the ThreadPoolExecutor that will be used using properties
     * @throws SQLException 
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    ThreadPoolExecutor build() throws DatabaseConfigurationException, IOException, SQLException ;

}
