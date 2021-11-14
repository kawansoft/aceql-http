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

package org.kawanfw.sql.servlet.injection.classes;

import java.util.Objects;

/**
 * Static store of the injected classes instances ready to use.
 * @author Nicolas de Pomereu
 *
 */

public class InjectedClassesStore {

    private static InjectedClasses injectedClasses = null;
    
    private InjectedClassesStore() {
    }

    public static InjectedClasses get() {
	Objects.requireNonNull(injectedClasses, "injectedClasses is null and was never set!");
	return injectedClasses;
    }

    public static void set(InjectedClasses injectedClasses) {
	InjectedClassesStore.injectedClasses = injectedClasses;
    }

}
