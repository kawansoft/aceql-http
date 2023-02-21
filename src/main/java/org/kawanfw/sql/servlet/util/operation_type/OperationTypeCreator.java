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
package org.kawanfw.sql.servlet.util.operation_type;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * @author Nicolas de Pomereu
 *
 */
public class OperationTypeCreator {
    
    private static OperationType operationType = null;

    /**
     * Creates an OperationType instance.
     * @return an OperationType instance.
     * @throws SQLException 
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static OperationType createInstance() throws SQLException {

	if (operationType == null) {
	    Class<?> c;
	    try {
		c = Class.forName("org.kawanfw.sql.pro.reflection.builders.ProEditionOperationType");
		Constructor<?> constructor = c.getConstructor();
		operationType = (OperationType) constructor.newInstance();
		return operationType;
	    } catch (ClassNotFoundException e) {
		return new DefaultOperationType();
	    } catch (Exception e) {
		throw new SQLException(e);
	    } 
	}

	return operationType;
    }

}
