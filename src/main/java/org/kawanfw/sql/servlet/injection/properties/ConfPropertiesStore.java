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
package org.kawanfw.sql.servlet.injection.properties;
/**
 * Static store of the ConfProperties instance containing all properties in use.
 * @author Nicolas de Pomereu
 *
 */

public class ConfPropertiesStore {

    private static ConfProperties confProperties = null;
    
    private ConfPropertiesStore() {
    }

    public static ConfProperties get() {
	return confProperties;
    }

    public static void set(ConfProperties confProperties) {
	ConfPropertiesStore.confProperties = confProperties;
    }

}
