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

import java.text.NumberFormat;

public class Memory {

    private static final int MB = 1024 * 1024;

    public static void printMemory() {
	/* Total number of processors or cores available to the JVM */
	String myString = null;

	// myString = NumberFormat.getInstance().format(
	// Runtime.getRuntime().availableProcessors());
	// //SystemOutHandle.display("Available processors (cores): " +
	// myString);

	myString = NumberFormat.getInstance()
		.format(Runtime.getRuntime().freeMemory() / MB);
	/* Total amount of free memory available to the JVM */
	// SystemOutHandle.display("Free memory (Mbytes) : " + myString);

	/* This will return Long.MAX_VALUE if there is no preset limit */
	long maxMemory = Runtime.getRuntime().maxMemory() / MB;
	// maxMemory = Long.MAX_VALUE ? "no limit" : maxMemory;
	myString = NumberFormat.getInstance().format(maxMemory);

	/* Maximum amount of memory the JVM will attempt to use */
	// SystemOutHandle.display("Maximum memory (Mbytes): " + myString);

	String maxValue = NumberFormat.getInstance()
		.format(Long.MAX_VALUE / MB);
	myString = NumberFormat.getInstance()
		.format(Runtime.getRuntime().freeMemory());

	/* Total memory currently in use by the JVM */
	MessageDisplayer.display("Total memory (Mbytes)  : " + myString
		+ " (Max Value: " + maxValue + ")");

	/* Get a list of all filesystem roots on this system */
	/*
	 * File[] roots = File.listRoots();
	 * 
	 * //SystemOutHandle.display(""); //SystemOutHandle.display("");
	 * 
	 * // For each filesystem root, print some info for (File root : roots)
	 * { SystemOutHandle.display("File system root: " +
	 * root.getAbsolutePath());
	 * SystemOutHandle.display("Total space (bytes): " +
	 * root.getTotalSpace()); SystemOutHandle.display("Free space (bytes): "
	 * + root.getFreeSpace());
	 * SystemOutHandle.display("Usable space (bytes): " +
	 * root.getUsableSpace()); }
	 */
    }

    public static void main(String[] args) {
	printMemory();
    }

}
