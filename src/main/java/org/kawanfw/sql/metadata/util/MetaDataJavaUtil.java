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

    public MetaDataJavaUtil() {

    }

    private static final int int_columnNoNulls = 0;
    private static final int int_columnNullable = 1;
    private static final int int_columnNullableUnknown = 2;

    public static String decodeNullable(int nullable) {
	if (nullable == int_columnNoNulls) return Column.columnNoNulls;
	else if (nullable == int_columnNullable) return Column.columnNullable;
	else if (nullable == int_columnNullableUnknown) return Column.columnNullableUnknown;
	else return "unknown";
    }

    private static final int int_importedKeyCascade  = 0;
    private static final int int_importedKeyRestrict = 1;
    private static final int int_importedKeySetNull  = 2;
    private static final int int_importedKeyNoAction = 3;
    private static final int int_importedKeySetDefault  = 4;
    private static final int int_importedKeyInitiallyDeferred  = 5;
    private static final int int_importedKeyInitiallyImmediate  = 6;
    private static final int int_importedKeyNotDeferrable  = 7;

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

    private static short int_tableIndexStatistic = 0;
    private static short int_tableIndexClustered = 1;
    private static short int_tableIndexHashed = 2;
    private static short int_tableIndexOther = 3;

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
