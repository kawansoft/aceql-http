/*
 * This file is part of KawanWall.
 * KawanWall: Firewall for SQL statements
 * Copyright (C) 2022,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.                 
 *                                                                         
 * KawanWall is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.         
 *              
 * KawanWall is distributed in the hope that it will be useful,               
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU Affero General Public License for more details.       
 *                                  
 * You should have received a copy of the GNU Affero General 
 * Public License along with this program; if not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * If you develop commercial activities using KawanWall, you must: 
 * a) disclose and distribute all source code of your own product,
 * b) license your own product under the GNU General Public License.
 * 
 * You can be released from the requirements of the license by
 * purchasing a commercial license. Buying such a license will allow you 
 * to ship KawanWall with your closed source products without disclosing 
 * the source code.
 *
 * For more information, please contact KawanSoft SAS at this
 * address: sales@kawansoft.com
 * 
 * Any modifications to this file must keep this entire header
 * intact.
 */
package org.kawanfw.sql.util.parser.keywords;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Loads the set of keywords in memory and allow to get them.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class SqlKeywords {

    private static Set<String> keywords = null;
    
    /**
     * Get the SQL keywords Set
     * @return the SQL keywords Set
     */
    public static Set<String> getKeywordSet() {
	if (keywords == null) {
	    keywords= new HashSet<String>(Arrays.asList(SqlKeywordsArray.KEYWORDS));
	}
	
	return keywords;
    }
}
