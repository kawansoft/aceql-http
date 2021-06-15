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
package org.kawanfw.sql.transport;

import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * @author Nicolas de Pomereu
 *
 */
public class SavepointHttp implements Savepoint {

    private int id = 0;
    private String name = null;

    /**
     * Constructor
     * 
     * @param id
     *            the Savepoint Id
     * @param name
     *            the Savepoint Name
     */
    public SavepointHttp(int id, String name) {
	super();
	this.id = id;
	this.name = name;
    }

    /**
     * Retrieves the generated ID for the savepoint that this
     * <code>Savepoint</code> object represents.
     * 
     * @return the numeric ID of this savepoint
     * @exception SQLException
     *                if this is a named savepoint
     */
    @Override
    public int getSavepointId() throws SQLException {
	return id;
    }

    /**
     * Retrieves the name of the savepoint that this <code>Savepoint</code>
     * object represents.
     * 
     * @return the name of this savepoint
     * @exception SQLException
     *                if this is an un-named savepoint
     */
    @Override
    public String getSavepointName() throws SQLException {
	return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "[id=" + id + ", name=" + name + "]";
    }

}
