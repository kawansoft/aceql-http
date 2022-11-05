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
package org.kawanfw.sql.servlet.injection.classes.blob;

import org.kawanfw.sql.api.server.blob.DefaultBlobDownloadConfigurator;

/**
 * @author Nicolas de Pomereu
 *
 */
public class DefaultBlobDownloadConfiguratorClassNameBuilder implements BlobDownloadConfiguratorClassNameBuilder {

    @Override
    public String getClassName() {
	return DefaultBlobDownloadConfigurator.class.getName();
    }

}
