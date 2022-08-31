/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
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
	//Objects.requireNonNull(injectedClasses, "injectedClasses is null and was never set!");
	return injectedClasses;
    }

    public static void set(InjectedClasses injectedClasses) {
	Objects.requireNonNull(injectedClasses, "injectedClasses cannot be null!");
	InjectedClassesStore.injectedClasses = injectedClasses;
    }

}
