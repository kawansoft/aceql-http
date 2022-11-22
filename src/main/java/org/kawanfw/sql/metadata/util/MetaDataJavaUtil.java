/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-11-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.sql.metadata.util;

import org.kawanfw.sql.metadata.Column;
import org.kawanfw.sql.metadata.ForeignKey;
import org.kawanfw.sql.metadata.Index;

/**
 * Misc utility methods for AceQLMetaData class calls.
 * @author Nicolas de Pomereu
 *
 */
public class MetaDataJavaUtil {

    private static final int int_columnNoNulls = 0;
    private static final int int_columnNullable = 1;
    private static final int int_columnNullableUnknown = 2;

    private static final int int_importedKeyCascade  = 0;
    private static final int int_importedKeyRestrict = 1;
    private static final int int_importedKeySetNull  = 2;
    private static final int int_importedKeyNoAction = 3;
    private static final int int_importedKeySetDefault  = 4;
    private static final int int_importedKeyInitiallyDeferred  = 5;
    private static final int int_importedKeyInitiallyImmediate  = 6;
    private static final int int_importedKeyNotDeferrable  = 7;

    private static short int_tableIndexStatistic = 0;
    private static short int_tableIndexClustered = 1;
    private static short int_tableIndexHashed = 2;
    private static short int_tableIndexOther = 3;

    public static String decodeNullable(int nullable) {
	if (nullable == int_columnNoNulls) return Column.columnNoNulls;
	else if (nullable == int_columnNullable) return Column.columnNullable;
	else if (nullable == int_columnNullableUnknown) return Column.columnNullableUnknown;
	else return "unknown";
    }



    public static String decodeRule(int rule) {
	if (rule == int_importedKeyCascade) return ForeignKey.importedKeyCascade;
	else if (rule == int_importedKeyRestrict) return ForeignKey.importedKeyRestrict;
	else if (rule == int_importedKeySetNull) return ForeignKey.importedKeySetNull;
	else if (rule == int_importedKeyNoAction) return ForeignKey.importedKeyNoAction;
	else if (rule == int_importedKeySetDefault) return ForeignKey.importedKeySetDefault;
	else if (rule == int_importedKeyInitiallyDeferred) return ForeignKey.importedKeyInitiallyDeferred;
	else if (rule == int_importedKeyInitiallyImmediate) return ForeignKey.importedKeyInitiallyImmediate;
	else if (rule == int_importedKeyNotDeferrable) return ForeignKey.importedKeyNotDeferrable;
	else return "unknown";
    }


    public static String decodeType(int type) {
	if (type == int_tableIndexStatistic)
	    return Index.tableIndexStatistic;
	else if (type == int_tableIndexClustered)
	    return Index.tableIndexClustered;
	else if (type == int_tableIndexHashed)
	    return Index.tableIndexHashed;
	else if (type == int_tableIndexOther)
	    return Index.tableIndexOther;
	else
	    return "unknown";
    }
}
