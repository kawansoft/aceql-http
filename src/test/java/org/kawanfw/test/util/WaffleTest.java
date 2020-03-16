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
package org.kawanfw.test.util;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.kawanfw.sql.api.server.auth.WindowsUserAuthenticator;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsComputer;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * @author Nicolas de Pomereu
 *
 */
public class WaffleTest {

    /**
     *
     */
    public WaffleTest() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

	String username = "ndepomereu@kawansoft.com";
	String password = FileUtils.readFileToString(new File("I:\\__NDP\\_MyPasswords\\login.txt"), "UTF-8");

	boolean logged = new WindowsUserAuthenticator().login(username, password.toCharArray(), ".", "10.0.0.0");
	System.out.println("logged: " + logged);

	boolean doContinue = false;
	if (! doContinue) {
	    return;
	}

	WindowsAuthProviderImpl windowsAuthProviderImpl = new WindowsAuthProviderImpl();
	windowsAuthProviderImpl.logonDomainUser(username, ".", password);
	System.out.println("logged!");
	System.out.println();

	IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
	IWindowsComputer computer = prov.getCurrentComputer();

	System.out.println("getComputerName(): " + computer.getComputerName());
	System.out.println("getJoinStatus()  : " + computer.getJoinStatus());
	System.out.println("getMemberOf()    : " + computer.getMemberOf());
	System.out.println();

	String[] localGroups = computer.getGroups();
	for(String localGroup : localGroups) {
	    System.out.println(" " + localGroup);
	}

//	IWindowsDomain[] domains = prov.getDomains();
//	for(IWindowsDomain domain : domains) {
//	    System.out.println(domain.getFqn() + ": " + domain.getTrustDirectionString());
//	}

	IWindowsIdentity identity = prov.logonUser(username, password);
	System.out.println("User identity: " + identity.getFqn());
	for(IWindowsAccount group : identity.getGroups()) {
	    System.out.println(" " + group.getFqn() + " (" + group.getSidString() + ")");
	}
	System.out.println();


    }

}
