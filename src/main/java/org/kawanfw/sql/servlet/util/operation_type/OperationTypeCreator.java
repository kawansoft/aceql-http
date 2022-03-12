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
