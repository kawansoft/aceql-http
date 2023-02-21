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
package org.kawanfw.sql.version;
/**
 * @author Nicolas de Pomereu
 *
 */

public class EditionUtil {

    /**
     * Says is edition is Enterprise.
     */
    public static boolean isEnterpriseEdition() {
	 String edition = System.getProperty("ACEQL_EDITION");
	 if (edition == null) {
	     return false;
	 }
	 return edition.equals("ACEQL_ENTERPRISE");
    }

}
