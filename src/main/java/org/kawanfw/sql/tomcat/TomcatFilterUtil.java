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
package org.kawanfw.sql.tomcat;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

/**
 * @author Nicolas de Pomereu
 *
 */
public class TomcatFilterUtil {

    /**
     * Protected / static class
     */
    protected TomcatFilterUtil() {

    }

    /**
     * Adds a predefined Filter definition and mapping to a Context Method
     * 
     * @param rootCtx
     *            the Context to add the predefined Filter to
     */
    public static void addFilterToContext(Context rootCtx) {

	// See https://tomcat.apache.org/tomcat-8.5-doc/config/filter.html for
	// filters

	/*
	 * <filter> <filter-name>CorsFilter</filter-name>
	 * <filter-class>org.apache.catalina.filters.CorsFilter</filter-class>
	 * <init-param> <param-name>cors.allowed.origins</param-name>
	 * <param-value>*</param-value> </init-param> <init-param>
	 * <param-name>cors.allowed.methods</param-name>
	 * <param-value>GET,POST,HEAD,OPTIONS,PUT</param-value> </init-param>
	 * <init-param> <param-name>cors.allowed.headers</param-name>
	 * <param-value>Content-Type,X-Requested-With,accept,Origin,Access-
	 * Control-Request-Method,Access-Control-Request-Headers</param-value>
	 * </init-param>
	 * 
	 * </filter> <filter-mapping> <filter-name>CorsFilter</filter-name>
	 * <url-pattern>/*</url-pattern> </filter-mapping>
	 */

	String filterName = "CorsFilter";
	String filterClass = "org.apache.catalina.filters.CorsFilter";

	FilterDef filterDef = new FilterDef();

	filterDef.setFilterName(filterName);
	filterDef.setFilterClass(filterClass);

	filterDef.addInitParameter("cors.allowed.origins", "*");
	filterDef.addInitParameter("cors.allowed.methods",
		"GET,POST,HEAD,OPTIONS,PUT");
	filterDef.addInitParameter("cors.allowed.headers",
		"Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers");

	// Don't forget mapping
	FilterMap filterMap = new FilterMap();
	filterMap.setFilterName(filterName);
	filterMap.addURLPattern("/*");

	// Add both filter definition & mapping to context that will be used by
	// a servlet:
	rootCtx.addFilterDef(filterDef);
	rootCtx.addFilterMap(filterMap);
    }

    /**
     * Dummy help for programming filter
     * 
     * @param args
     */
    public static void main(String[] args) throws Exception {

	Tomcat tomcat = new Tomcat();
	Context rootCtx = tomcat.addContext("",
		new File("c:\\tmp").getAbsolutePath());

	addFilterToContext(rootCtx);

    }

}
