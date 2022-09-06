/**
 * 
 */
package org.kawanfw.test.util;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

/**
 * @author Nicolas de Pomereu
 *
 */
public class HealthCheckTestMemory {

    public static void testMemory() {
	/* Total number of processors or cores available to the JVM */
	System.out.println("Available processors (cores): " + Runtime.getRuntime().availableProcessors());

	/* Total amount of free memory available to the JVM */
	System.out.println("Free memory (bytes): " + Runtime.getRuntime().freeMemory());

	/* This will return Long.MAX_VALUE if there is no preset limit */
	long maxMemory = Runtime.getRuntime().maxMemory();
	/* Maximum amount of memory the JVM will attempt to use */
	System.out.println("Maximum memory (bytes): " + (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));

	/* Total memory currently in use by the JVM */
	System.out.println("Total memory (bytes): " + Runtime.getRuntime().totalMemory());

	/* Get a list of all filesystem roots on this system */
	File[] roots = File.listRoots();

	// https://www.baeldung.com/java-metrics
	MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
	System.out.println(String.format("Initial memory: %.2f GB",
		(double) memoryMXBean.getHeapMemoryUsage().getInit() / 1073741824));
	System.out.println(String.format("Used heap memory: %.2f GB",
		(double) memoryMXBean.getHeapMemoryUsage().getUsed() / 1073741824));
	System.out.println(String.format("Max heap memory: %.2f GB",
		(double) memoryMXBean.getHeapMemoryUsage().getMax() / 1073741824));
	System.out.println(String.format("Committed memory: %.2f GB",
		(double) memoryMXBean.getHeapMemoryUsage().getCommitted() / 1073741824));

	/* For each filesystem root, print some info */
	for (File root : roots) {
	    System.out.println("File system root: " + root.getAbsolutePath());
	    System.out.println("Total space (bytes): " + root.getTotalSpace());
	    System.out.println("Free space (bytes): " + root.getFreeSpace());
	    System.out.println("Usable space (bytes): " + root.getUsableSpace());
	}

    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	testMemory();
    }

}
