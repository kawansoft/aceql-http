/*
 * Copyright (c)2022 KawanSoft S.A.S.
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-30
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.version;
/**
 * Gets version and Edition type info.
 * @author Nicolas de Pomereu
 *
 */

public class VersionWrapper {

    private static final String NAME = "AceQL HTTP";
    private static final String EDITION_COMMUNITY = "Community";
    private static final String EDITION_ENTERPRISE = "Enterprise";
    private static final String DESCRIPTION = "Remote SQL access over HTTP";

    private static String VERSION = VersionValues.VERSION;
    private static String DATE = VersionValues.DATE;
    
    public static String getName() {
	return NAME;
    }

    public static String getServerVersion() {
	return NAME + " " + getEdition() + " " + VERSION + " - " + DATE;
    }
    
    public static String getVersionNumber() {
	return VERSION;
    }
    
    public static String getVersionDate() {
	return DATE;
    }

    public static String getEdition() {
	String edition = EditionUtil.isCommunityEdition() ? EDITION_COMMUNITY:EDITION_ENTERPRISE;
	return edition;
    }
    
    
    
  
    /**
     * Future usage
     * 
     * @return
     */
    @SuppressWarnings("unused")
    private static final String getFullVersion() {
	String CR_LF = System.getProperty("line.separator");

	// return PRODUCT.DESCRIPTION + CR_LF + new DefaultVersion().getVersion() +
	// CR_LF + "by : " + new VENDOR();
	return DESCRIPTION + CR_LF + getServerVersion() + CR_LF + "by: "
		+ new Vendor().toString();
    }
    
}
