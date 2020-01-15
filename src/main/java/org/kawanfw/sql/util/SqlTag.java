/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP                                     
 * Copyright (C) 2020,  KawanSoft SAS
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
package org.kawanfw.sql.util;

import org.kawanfw.sql.version.Version;

/**
 * @author Nicolas de Pomereu
 *
 */
public class SqlTag {

    protected SqlTag() {

    }

    public static final String SQL_PRODUCT_START = "["
	    + Version.PRODUCT.NAME.toUpperCase() + " START]";
    public static final String SQL_PRODUCT_START_FAILURE = "["
	    + Version.PRODUCT.NAME.toUpperCase() + " START FAILURE]";

    public static final String USER_CONFIGURATION_FAILURE = "[USER CONFIGURATION]";
    public static final String SQL_PRODUCT_LICENSE_FAILURE = "["
	    + Version.PRODUCT.NAME.toUpperCase() + " - LICENSE FAILURE]";

    public static final String PLEASE_CORRECT = "Please correct and retry.";

}
