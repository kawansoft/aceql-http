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
package org.kawanfw.sql.api.server;

import java.io.IOException;

import org.kawanfw.sql.api.server.util.Ssh;

/**
 * A concrete {@code DatabaseConfigurator} that extends
 * {@code DefaultDatabaseConfigurator} and allows zero-code client
 * {@code (username, password)} authentication using SSH.
 * 
 * @author Nicolas de Pomereu
 * 
 */
public class SshAuthDatabaseConfigurator extends DefaultDatabaseConfigurator
	implements DatabaseConfigurator {

    /**
     * Allows using SSH to authenticate the remote {@code (username, password)}
     * couple sent by the client side.
     * <ul>
     * <li>If the {@code user.home/.kawansoft/sshAuth.properties} file exists:
     * <br>
     * the {@code (username, password)} couple is checked against the SSH server
     * of the host defined with the properties {@code host} for the hostname and
     * {@code port} for the port in the
     * {@code user.home/.kawansoft/sshAuth.properties} file.</li>
     * <li>If {@code sshAuth.properties} file does not exists: <br>
     * the host IP is used as hostname value and port is 22.</li>
     * </ul>
     * {@code user.home} is the one of the running servlet container.
     * <p>
     * 
     * @param username
     *            the username sent by the client login
     * @param password
     *            the password to connect to the server
     * @param database
     *            the database name to which the client wants to connect
     * @param ipAddress
     *            the IP address of the client user
     * 
     * @return <code>true</code> if the (login, password) couple is
     *         correct/valid as a SSH user on this host. If false, the client
     *         side will not be authorized to send any command.
     * @throws IOException
     *             if wrapped {@code Ssh.login(String, char[])} throws an I/O
     *             Exception.
     */
    @Override
    public boolean login(String username, char[] password, String database,
	    String ipAddress) throws IOException {
	return Ssh.login(username, password);
    }

}
