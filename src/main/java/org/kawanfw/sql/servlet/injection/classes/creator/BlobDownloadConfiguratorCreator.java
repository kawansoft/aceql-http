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
package org.kawanfw.sql.servlet.injection.classes.creator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.kawanfw.sql.api.server.blob.BlobDownloadConfigurator;
import org.kawanfw.sql.api.server.blob.DefaultBlobDownloadConfigurator;

/**
 * @author Nicolas de Pomereu
 *
 */
public class BlobDownloadConfiguratorCreator {

    private BlobDownloadConfigurator blobDownloadConfigurator = null;
    private String blobDownloadConfiguratorClassName = null;

    private static String[] PREDEFINED_CLASS_NAMES = { DefaultBlobDownloadConfigurator.class.getSimpleName(), };

    /**
     * Allows to add automatically the package for predefined classes
     *
     * @param theClassName
     * @return
     */
    private static String getNameWithPackage(final String theClassName) {

	for (int i = 0; i < PREDEFINED_CLASS_NAMES.length; i++) {
	    if (PREDEFINED_CLASS_NAMES[i].equals(theClassName)) {
		// Add prefix package
		String theClassNameNew = DefaultBlobDownloadConfigurator.class.getPackage().getName() + "."
			+ theClassName;
		return theClassNameNew;
	    }
	}

	return theClassName;
    }

    public BlobDownloadConfiguratorCreator(final String theBlobDownloadConfiguratorClassName)
	    throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException, NoSuchMethodException, SecurityException {

	if (theBlobDownloadConfiguratorClassName != null && !theBlobDownloadConfiguratorClassName.isEmpty()) {

	    String theBlobDownloadConfiguratorClassNameNew = getNameWithPackage(theBlobDownloadConfiguratorClassName);

	    Class<?> c = Class.forName(theBlobDownloadConfiguratorClassNameNew);
	    Constructor<?> constructor = c.getConstructor();
	    blobDownloadConfigurator = (BlobDownloadConfigurator) constructor.newInstance();
	    this.blobDownloadConfiguratorClassName = theBlobDownloadConfiguratorClassNameNew;
	} else {
	    blobDownloadConfigurator = new DefaultBlobDownloadConfigurator();
	    this.blobDownloadConfiguratorClassName = blobDownloadConfigurator.getClass().getName();
	}

    }

    public BlobDownloadConfigurator getBlobDownloadConfigurator() {
	return blobDownloadConfigurator;
    }

    public String getBlobDownloadConfiguratorClassName() {
	return blobDownloadConfiguratorClassName;
    }

}
