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
package org.kawanfw.sql.version;

/**
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
	String edition = EditionUtil.isCommunityEdition() ? EDITION_COMMUNITY:EDITION_ENTERPRISE;
	return NAME + " " + edition + " " + VERSION + " - " + DATE;
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
