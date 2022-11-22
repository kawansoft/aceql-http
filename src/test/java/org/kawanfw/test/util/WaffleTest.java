/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
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
