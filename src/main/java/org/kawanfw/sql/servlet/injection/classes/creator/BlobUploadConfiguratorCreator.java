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

import org.kawanfw.sql.api.server.blob.BlobUploadConfigurator;
import org.kawanfw.sql.api.server.blob.DefaultBlobUploadConfigurator;

public class BlobUploadConfiguratorCreator {

    private BlobUploadConfigurator blobUploadConfigurator = null;
    private String blobUploadConfiguratorClassName = null;

    private static String[] PREDEFINED_CLASS_NAMES = {
	    DefaultBlobUploadConfigurator.class.getSimpleName(),
	    };

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
		String theClassNameNew = DefaultBlobUploadConfigurator.class.getPackage()
			.getName() + "." + theClassName;
		return theClassNameNew;
	    }
	}

	return theClassName;
    }

    public BlobUploadConfiguratorCreator(final String theBlobUploadConfiguratorClassName)
	    throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException, NoSuchMethodException, SecurityException {

	if (theBlobUploadConfiguratorClassName != null && !theBlobUploadConfiguratorClassName.isEmpty()) {

	    String theBlobUploadConfiguratorClassNameNew = getNameWithPackage(theBlobUploadConfiguratorClassName);

	    Class<?> c = Class.forName(theBlobUploadConfiguratorClassNameNew);
	    Constructor<?> constructor = c.getConstructor();
	    blobUploadConfigurator = (BlobUploadConfigurator) constructor.newInstance();
	    this.blobUploadConfiguratorClassName = theBlobUploadConfiguratorClassNameNew;
	} else {
	    blobUploadConfigurator = new DefaultBlobUploadConfigurator();
	    this.blobUploadConfiguratorClassName = blobUploadConfigurator.getClass().getName();
	}

    }

    public BlobUploadConfigurator getBlobUploadConfigurator() {
        return blobUploadConfigurator;
    }

    public String getBlobUploadConfiguratorClassName() {
        return blobUploadConfiguratorClassName;
    }

}
