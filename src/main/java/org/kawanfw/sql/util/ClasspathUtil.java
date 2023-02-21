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
package org.kawanfw.sql.util;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Nicolas de Pomereu
 */
public class ClasspathUtil {

    public static List<String> getClasspath() {
        String classpath = System.getProperty("java.class.path");
        
        String [] classpathArray = classpath.split(System.getProperty("path.separator"));
                
        if (classpathArray == null) {
            return null;
        }         
        
        List<String> classpathList = Arrays.asList(classpathArray);
        return classpathList;
        
    }
    
}
