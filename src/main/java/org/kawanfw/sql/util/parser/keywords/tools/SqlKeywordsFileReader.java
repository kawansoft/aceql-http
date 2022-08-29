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
package org.kawanfw.sql.util.parser.keywords.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Nicolas de Pomereu
 *
 */
class SqlKeywordsFileReader {

    private String name;
	

    public SqlKeywordsFileReader(String name) {
	this.name = name;
    }

    public Set<String> buildKeywordsSet() throws IOException {
	
	Set<String> keywords = new LinkedHashSet<>();
	
	InputStream in = getClass().getResourceAsStream(name);
	try (BufferedReader bufferedReader = new BufferedReader(
		new InputStreamReader(in));) {

	    String line = null;
	    while ((line = bufferedReader.readLine()) != null) {
		keywords.add(line.toUpperCase().trim());
	    }
	}
	
	return keywords;
    }

    public static void main(String[] args) throws IOException {
	SqlKeywordsFileReader sqlKeywordsFileReader = new SqlKeywordsFileReader(SqlKeywordsClassBuilder.SQL_KEYWORDS_TXT);
	Set<String> keywords = sqlKeywordsFileReader.buildKeywordsSet();
	for (String keyword : keywords) {
	    System.out.println(keyword);
	}
    }

}
