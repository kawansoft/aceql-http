/**
 *
 */
package org.kawanfw.sql.servlet.creator;

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

    public BlobDownloadConfiguratorCreator(String theBlobDownloadConfiguratorClassName)
	    throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException, NoSuchMethodException, SecurityException {

	if (theBlobDownloadConfiguratorClassName != null && !theBlobDownloadConfiguratorClassName.isEmpty()) {
	    Class<?> c = Class.forName(theBlobDownloadConfiguratorClassName);
	    Constructor<?> constructor = c.getConstructor();
	    blobDownloadConfigurator = (BlobDownloadConfigurator) constructor.newInstance();
	    this.blobDownloadConfiguratorClassName = theBlobDownloadConfiguratorClassName;
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
