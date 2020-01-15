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
package org.kawanfw.sql.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

/**
 * Misc file utilities
 * 
 * @author Nicolas de Pomereu
 * 
 */
public class FrameworkFileUtil {

    /** The DEBUG flag */
    private static boolean DEBUG = FrameworkDebug
	    .isSet(FrameworkFileUtil.class);

    public static String CR_LF = System.getProperty("line.separator");

    /** List of files */
    // private List<File> m_fileList = new Vector<File>();

    /**
     * Constructor
     */
    protected FrameworkFileUtil() {
	super();
    }

    /**
     * Build a unique string
     * 
     * @return a unique string
     */
    public static synchronized String getUniqueId() {
	UUID uuid = UUID.randomUUID();
	String uuidStr = uuid.toString();
	uuidStr = uuidStr.replace("-", "");
	return uuidStr;
    }

    /**
     * Returns System.getProperty("user.home") for all devices except for
     * Android which returns java.io.tmpdir/kawansoft-user-home
     * 
     * @return System.getProperty("user.home") for all devices except for
     *         Android which returns java.io.tmpdir/kawansoft-user-home
     */
    public static String getUserHome() {
	String userHome = System.getProperty("user.home");

	if (!FrameworkSystemUtil.isAndroid()) {
	    return userHome;
	}

	// We are on android
	userHome = System.getProperty("java.io.tmpdir");
	if (!userHome.endsWith(File.separator)) {
	    userHome += File.separator;
	}

	userHome += "kawansoft-user-home";

	File userHomeFile = new File(userHome);
	userHomeFile.mkdirs();

	return userHome;

    }

    /**
     * Returns System.getProperty("user.home") + File.separator + ".kawansoft
     * for all devices except for Android which returns
     * java.io.tmpdir/kawansoft-user-home/.kawansoft
     * 
     * @return System.getProperty("user.home") + File.separator + ".kawansoft
     *         for all devices except for Android which returns
     *         java.io.tmpdir/kawansoft-user-home/.kawansoft
     */

    public static String getUserHomeDotKawansoftDir() {

	String userHomeDotKwansoft = getUserHome();
	if (!userHomeDotKwansoft.endsWith(File.separator)) {
	    userHomeDotKwansoft += File.separator;
	}

	userHomeDotKwansoft += ".kawansoft";

	File tempDirFile = new File(userHomeDotKwansoft);
	tempDirFile.mkdirs();

	return userHomeDotKwansoft;
    }

    /**
     * @return the Kawansoft temp directory (create it if not exists)
     */
    public static String getKawansoftTempDir() {

	String tempDir = FrameworkFileUtil.getUserHomeDotKawansoftDir()
		+ File.separator + "tmp";

	File tempDirFile = new File(tempDir);
	tempDirFile.mkdirs();

	return tempDir;
    }

    /**
     * Extract the first line of a presumed text file
     * 
     * @param file
     *            the presumed text file to extract the first line from
     * @return the first line of the file
     * @throws IOException
     */
    public static String getFirstLineOfFile(File file) throws IOException {

	if (file == null) {
	    throw new IllegalArgumentException(
		    Tag.PRODUCT_PRODUCT_FAIL + "receiveFile is null");
	}

	if (!file.exists()) {
	    throw new FileNotFoundException(Tag.PRODUCT_PRODUCT_FAIL
		    + "receiveFile does not exists: " + file);
	}

	try (BufferedReader bufferedReader = new BufferedReader(
		new InputStreamReader(new FileInputStream(file)));) {
	    // Read content of first line.

	    String firstLine = bufferedReader.readLine();
	    return firstLine;
	}
    }

    /**
     * Return true if the filename is a Linux possible Filename
     * 
     * @param filename
     *            the filename to test
     * @return true if the filename is a Linux Filename
     */
    public static boolean isPossibleLinuxFilename(String filename) {
	if (filename.indexOf("\\") != -1 || // Windows
		filename.indexOf("/") != -1 || // Windows
		filename.indexOf(":") != -1 || // Windows
		filename.indexOf("*") != -1 || // Windows
		filename.indexOf("?") != -1 || // Windows
		filename.indexOf("\"") != -1 || // Windows
		filename.indexOf("<") != -1 || // Windows
		filename.indexOf(">") != -1 || // Windows
		filename.indexOf("|") != -1 || // Windows
		filename.indexOf("@") != -1 || // Linux
		filename.indexOf(" ") != -1) // Linux
	{
	    return false;
	} else {
	    return true;
	}
    }

    /**
     * Return true if the fils exists, is readable and is not locked by another
     * process <br>
     * New version that works because it uses RandomAccessFile and will throw an
     * exception if another file has locked the file
     */
    public static boolean isUnlockedForRead(File file) {
	if (file.exists() && file.canRead()) {
	    if (file.isDirectory()) {
		return true;
	    }

	    try {
		RandomAccessFile raf = new RandomAccessFile(file, "r");

		raf.close();

		debug(new Date() + " " + file + " OK!");

		return true;
	    } catch (Exception e) {
		debug(new Date() + " " + file + " LOCKED! " + e.getMessage());
	    }
	} else {
	    debug(new Date() + " " + file + " LOCKED! File exists(): "
		    + file.exists() + " File canWrite: " + file.canWrite());
	}

	return false;

    }

    /**
     * Return true if the fils exists and is readable and writable not locked by
     * another process <br>
     * New version that works because it uses RandomAccessFile and will throw an
     * exception if another file has locked the file
     */
    public static boolean isUnlockedForWrite(File file) {
	if (file.exists() && file.canWrite()) {
	    if (file.isDirectory()) {
		return true;
	    }

	    try {
		RandomAccessFile raf = new RandomAccessFile(file, "rw");

		raf.close();

		debug(new Date() + " " + file + " OK!");

		return true;
	    } catch (Exception e) {
		debug(new Date() + " " + file + " LOCKED! " + e.getMessage());
	    }
	} else {
	    debug(new Date() + " " + file + " LOCKED! File exists(): "
		    + file.exists() + " File canWrite: " + file.canWrite());
	}

	return false;

    }

    /**
     * Recuse research of a files. Result is stored in m_fileList
     * 
     * @param fileList
     *            the initial list
     * @param recurse
     *            if true, search inside directories
     */

    // private static void searchFiles(File[] fileList, boolean recurse) {
    // if (fileList == null) {
    // throw new IllegalArgumentException("File list can't be null!");
    // }
    //
    // for (int i = 0; i < fileList.length; i++) {
    // m_fileList.add(fileList[i]);
    //
    // if (fileList[i].isDirectory()) {
    // if (recurse) {
    // searchFiles(fileList[i].listFiles(), recurse);
    // }
    // }
    // }
    // }

    /**
     * Extract all files from a list of files, with recurse options
     * 
     * @param fileList
     *            the initial list
     * @param recurse
     *            if true, search inside directories
     * 
     * @return the extracted all files
     */
    // public static List<File> listAllFiles(List<File> fileList, boolean
    // recurse) {
    //
    // if (fileList == null) {
    // throw new IllegalArgumentException("File list can't be null!");
    // }
    //
    // if (fileList.isEmpty()) {
    // return new Vector<File>();
    // }
    //
    // File[] files = new File[fileList.size()];
    //
    // for (int i = 0; i < fileList.size(); i++) {
    // files[i] = fileList.get(i);
    // }
    //
    // searchFiles(files, recurse);
    // return m_fileList;
    // }

    /**
     * 
     * Extract the files that are not directories from a file list
     * 
     * @param fileList
     *            the initial list (firt level only)
     * @return the file list
     */
    public static List<File> extractFilesOnly(List<File> fileList) {
	if (fileList == null) {
	    throw new IllegalArgumentException("File list can't be null!");
	}

	if (fileList.isEmpty()) {
	    return new Vector<File>();
	}

	List<File> files = new Vector<File>();

	for (int i = 0; i < fileList.size(); i++) {
	    if (!fileList.get(i).isDirectory()) {
		files.add(fileList.get(i));
	    }
	}

	return files;

    }

    /**
     * 
     * Extract the directories only from a file list
     * 
     * @param fileList
     *            the initial list (firt level only)
     * @return the file list
     */
    public static List<File> extractDirectoriesOnly(List<File> fileList) {
	List<File> directories = new Vector<File>();

	for (int i = 0; i < fileList.size(); i++) {
	    if (fileList.get(i).isDirectory()) {
		directories.add(fileList.get(i));
	    }
	}

	return directories;

    }

    /**
     * Put the content of a file as HTML into a String <br>
     * No carriage returns will be included in output String
     * 
     * @param fileIn
     *            The HTML text file
     * @return The content in text
     * @throws IOException
     */
    public static String getHtmlContent(File fileIn) throws IOException {
	if (fileIn == null) {
	    throw new IllegalArgumentException("File name can't be null!");
	}

	try (BufferedReader br = new BufferedReader(new FileReader(fileIn));) {
	    String text = "";

	    String line = null;

	    while ((line = br.readLine()) != null) {
		text += line;
	    }

	    return text;
	}
    }

    /**
     * debug tool
     */
    private static void debug(String s) {
	if (DEBUG) {
	    System.out.println(s);
	}
    }

}
