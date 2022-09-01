/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.servlet.sql;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * Utility classes for DbVendorManager
 *
 * @author Nicolas de Pomereu
 *
 */
public class DbEngineManagerUtil {

    /**
     * Protected constructor, no instanciation
     */
    protected DbEngineManagerUtil() {
    }

    /**
     * Says if the SQL order contains a word surrounded with spaces
     *
     * @param sqlOrder
     *            the SQL order
     * @param word
     *            the word to contain surrounded by spaces
     * @return true if the SQL order contains the word surrounded with spaces
     */
    public static boolean containsWord(String sqlOrder, String word) {
	String s = sqlOrder;
	s = s.replace('\t', ' ');
	return s.toLowerCase().contains(" " + word.toLowerCase() + " ");
    }

    /**
     * Remove ";" from trailing SQL order
     *
     * @param sqlOrder
     * @return sqlOrder without trailing ";"
     */
    public static String removeSemicolon(final String sqlOrder) {
	String sqlOrderNew = sqlOrder;
	while (sqlOrderNew.trim().endsWith(";")) {
	    sqlOrderNew = sqlOrderNew.trim();
	    sqlOrderNew = StringUtils.removeEnd(sqlOrderNew, ";");
	}
	return sqlOrder;
    }

}
