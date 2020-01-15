package org.kawanfw.sql.servlet.creator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.kawanfw.sql.api.server.blob.BlobUploadConfigurator;
import org.kawanfw.sql.api.server.blob.DefaultBlobUploadConfigurator;

public class BlobUploadConfiguratorCreator {

    private BlobUploadConfigurator blobUploadConfigurator = null;
    private String blobUploadConfiguratorClassName = null;

    public BlobUploadConfiguratorCreator(String theBlobUploadConfiguratorClassName)
	    throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException, NoSuchMethodException, SecurityException {

	if (theBlobUploadConfiguratorClassName != null && !theBlobUploadConfiguratorClassName.isEmpty()) {
	    Class<?> c = Class.forName(theBlobUploadConfiguratorClassName);
	    Constructor<?> constructor = c.getConstructor();
	    blobUploadConfigurator = (BlobUploadConfigurator) constructor.newInstance();
	    this.blobUploadConfiguratorClassName = theBlobUploadConfiguratorClassName;
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
