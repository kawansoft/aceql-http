/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
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
