/*
 * Copyright (c)2023 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-02-21
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
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

	// See https://tomcat.apache.org/tomcat-9.0-doc/config/filter.html for
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
