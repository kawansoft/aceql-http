/**
 *
 */
package org.kawanfw.test.util;

import java.io.File;

import org.apache.commons.io.FileUtils;

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
