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
package com.kawanwall.server.api.logging;

import java.util.Map;

import org.slf4j.Logger;

/**
 * Allows to create a neutral slf4 Logger that thus will support many implementations.
 * @author Nicolas de Pomereu
 *
 */
public interface LoggerCreator {

    /**
     * Returns the Logger as sl4j instance
     * @return the Logger as sl4j instance
     */
    public Logger getLogger();

    /**
     * Return the elements of the {@code Logger}: name, fileNamePattern, etc. 
     * This allows to give admin users info about the {@code Logger} when stating the firewall.
     * Implementation is free.
     * @return the elements of the Logger
     */
    public Map<String, String> getElements();

}
