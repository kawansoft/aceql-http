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
/**
 * 
 * Define if we delete the temp files created when uploading and downloading
 * Blobs/Clob.
 * 
 * @author Nicolas de Pomereu
 */

public class KeepTempFilePolicyParms {

    /** if true, the local blob files will be deleted after been uploaded */
    public static boolean KEEP_TEMP_FILE = FrameworkDebug.isSet(KeepTempFilePolicyParms.class);

    /**
     * No Constructor
     */
    protected KeepTempFilePolicyParms() {
    }

}
