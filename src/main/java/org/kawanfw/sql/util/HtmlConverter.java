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

import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.translate.NumericEntityEscaper;

/**
 * Static methods to convert special characters to HTML equivalent and
 * vice-versa.
 * 
 * @author Nicolas de Pomereu
 * 
 */

@SuppressWarnings("deprecation")
public class HtmlConverter {

    private static boolean DO_NOTHING = false;

    /**
     * 
     * Converts special HTML values of characters to their original values. <br>
     * Example : <code>"&amp;eacute;"</code>"is converted to "ï¿½"
     * <p>
     * 
     * @param string
     *            A String to convert from HTML to original
     *            <p>
     * @return A String of char converted to original values
     * 
     */

    public static String fromHtml(String string) {

	if (DO_NOTHING)
	    return string;

	if (string == null)
	    return string;

	if (string.contains("&")) {
	    return StringEscapeUtils.unescapeHtml4(string);
	} else {
	    return string;
	}

    }

    /**
     * Converts special characters to their HTML values. <br>
     * Example : "é" is converted to "&amp;eacute;"
     * <p>
     * 
     * @param string
     *            A String to convert from original to HTML
     *            <p>
     * @return A String of char converted to HTML equivalent.
     * 
     */

    public static String toHtml(String string) {

	if (DO_NOTHING)
	    return string;

	string = StringEscapeUtils.ESCAPE_HTML4
		.with(NumericEntityEscaper.between(0x7f, Integer.MAX_VALUE))
		.translate(string);

	if (string != null) {
	    string = string.replaceAll("&amp;", "&"); // To keep same result if
						      // multi-call
	}

	return string;
    }

    /**
     * 
     * Converts special HTML values of characters to their original values. <br>
     * Example : "&eacute;"is converted to "é"
     * <p>
     * 
     * @param list
     *            A list of String to convert from HTML to original
     *            <p>
     * @return A list of String char converted to original values
     * 
     */

    public static List<String> fromHtml(List<String> list) {

	List<String> newList = new Vector<String>();

	for (String string : list) {
	    string = HtmlConverter.fromHtml(string);
	    newList.add(string);
	}

	return newList;
    }

    /**
     * Converts special characters to their HTML values. <br>
     * Example : "ï¿½" is converted to "&eacute;"
     * <p>
     * 
     * @param list
     *            A list of String to convert from original to HTML
     *            <p>
     * @return A list of String of char converted to HTML equivalent.
     * 
     */

    public static List<String> toHtml(List<String> list) {

	List<String> newList = new Vector<String>();

	for (String string : list) {
	    string = HtmlConverter.toHtml(string);
	    newList.add(string);
	}

	return newList;
    }

}
