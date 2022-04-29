/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2021,  KawanSoft SAS
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
package org.kawanfw.sql.servlet.injection.classes;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

import org.kawanfw.sql.api.server.DatabaseConfigurator;
import org.kawanfw.sql.api.server.auth.UserAuthenticator;
import org.kawanfw.sql.api.server.auth.headers.RequestHeadersAuthenticator;
import org.kawanfw.sql.api.server.blob.BlobDownloadConfigurator;
import org.kawanfw.sql.api.server.blob.BlobUploadConfigurator;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.api.server.firewall.trigger.SqlFirewallTrigger;
import org.kawanfw.sql.api.server.listener.UpdateListener;
import org.kawanfw.sql.api.server.session.SessionConfigurator;

/**
 * All decoded Properties into injected classes instances ready to use.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class InjectedClasses {

    /** The UserAuthenticator instance */
    private UserAuthenticator userAuthenticator = null;

    /** RequestHeadersAuthenticator instance */
    private RequestHeadersAuthenticator requestHeadersAuthenticator;

    /** The map of (database, DatabaseConfigurator) */
    private Map<String, DatabaseConfigurator> databaseConfigurators = new ConcurrentHashMap<>();

    /** The map of (database, SqlFirewallTrigger) */
    private Map<String, SqlFirewallTrigger> sqlFirewallTriggers = new ConcurrentHashMap<>();
	
    /** The map of (database, List<SqlFirewallManager>) */
    private Map<String, List<SqlFirewallManager>> sqlFirewallManagerMap = new ConcurrentHashMap<>();

    /** The BlobUploadConfigurator instance */
    private BlobUploadConfigurator blobUploadConfigurator = null;

    /** The BlobUploadConfigurator instance */
    private BlobDownloadConfigurator blobDownloadConfigurator = null;

    /** The SessionConfigurator instance */
    private SessionConfigurator sessionConfigurator = null;

    /** The executor to use */
    private ThreadPoolExecutor threadPoolExecutor = null;

    /** The map of (database, List<UpdateListener>) */
    private Map<String, List<UpdateListener>> updateListenerMap = new ConcurrentHashMap<>();
    
    private InjectedClasses(InjectedClassesBuilder injectedClassesBuilder) {
	this.userAuthenticator = injectedClassesBuilder.userAuthenticator;
	this.requestHeadersAuthenticator = injectedClassesBuilder.requestHeadersAuthenticator;
	this.databaseConfigurators = injectedClassesBuilder.databaseConfigurators;
	this.sqlFirewallTriggers = injectedClassesBuilder.sqlFirewallTriggers;
	
	this.sqlFirewallManagerMap = injectedClassesBuilder.sqlFirewallManagerMap;

	this.blobUploadConfigurator = injectedClassesBuilder.blobUploadConfigurator;
	this.blobDownloadConfigurator = injectedClassesBuilder.blobDownloadConfigurator;
	this.sessionConfigurator = injectedClassesBuilder.sessionConfigurator;
	this.threadPoolExecutor = injectedClassesBuilder.threadPoolExecutor;
	
	this.updateListenerMap = injectedClassesBuilder.updateListenerMap;

    }

    /**
     * @return the userAuthenticator
     */
    public UserAuthenticator getUserAuthenticator() {
	return userAuthenticator;
    }

    /**
     * @return the requestHeadersAuthenticator
     */
    public RequestHeadersAuthenticator getRequestHeadersAuthenticator() {
	return requestHeadersAuthenticator;
    }

    /**
     * @return the databaseConfigurators
     */
    public Map<String, DatabaseConfigurator> getDatabaseConfigurators() {
	return databaseConfigurators;
    }

   
    /**
     * @return the sqlFirewallTriggers
     */
    public Map<String, SqlFirewallTrigger> getSqlFirewallTriggers() {
        return sqlFirewallTriggers;
    }

    /**
     * @return the sqlFirewallManagerMap
     */
    public Map<String, List<SqlFirewallManager>> getSqlFirewallManagerMap() {
	return sqlFirewallManagerMap;
    }

    
    /**
     * @return the blobUploadConfigurator
     */
    public BlobUploadConfigurator getBlobUploadConfigurator() {
	return blobUploadConfigurator;
    }

    /**
     * @return the blobDownloadConfigurator
     */
    public BlobDownloadConfigurator getBlobDownloadConfigurator() {
	return blobDownloadConfigurator;
    }

    /**
     * @return the sessionConfigurator
     */
    public SessionConfigurator getSessionConfigurator() {
	return sessionConfigurator;
    }

    /**
     * @return the threadPoolExecutor
     */
    public ThreadPoolExecutor getThreadPoolExecutor() {
	return threadPoolExecutor;
    }

 
    /**
     * @return the updateListenerMap
     */
    public Map<String, List<UpdateListener>> getUpdateListenerMap() {
        return updateListenerMap;
    }



    public static class InjectedClassesBuilder {
	/** The UserAuthenticator instance */
	private UserAuthenticator userAuthenticator = null;

	/** RequestHeadersAuthenticator instance */
	private RequestHeadersAuthenticator requestHeadersAuthenticator;

	/** The map of (database, DatabaseConfigurator) */
	private Map<String, DatabaseConfigurator> databaseConfigurators = new ConcurrentHashMap<>();

	/** The map of (database, SqlFirewallTrigger) */
	private Map<String, SqlFirewallTrigger> sqlFirewallTriggers = new ConcurrentHashMap<>();
	
	/** The map of (database, List<SqlFirewallManager>) */
	private Map<String, List<SqlFirewallManager>> sqlFirewallManagerMap = new ConcurrentHashMap<>();

	/** The BlobUploadConfigurator instance */
	private BlobUploadConfigurator blobUploadConfigurator = null;

	/** The BlobUploadConfigurator instance */
	private BlobDownloadConfigurator blobDownloadConfigurator = null;

	/** The SessionConfigurator instance */
	private SessionConfigurator sessionConfigurator = null;

	/** The executor to use */
	private ThreadPoolExecutor threadPoolExecutor = null;

	/** The map of (database, List<UpdateListener>) */
	private Map<String, List<UpdateListener>> updateListenerMap = new ConcurrentHashMap<>();


	  
	public InjectedClassesBuilder userAuthenticator(UserAuthenticator userAuthenticator) {
	    this.userAuthenticator = userAuthenticator;
	    return this;
	}

	public InjectedClassesBuilder requestHeadersAuthenticator(
		RequestHeadersAuthenticator requestHeadersAuthenticator) {
	    this.requestHeadersAuthenticator = requestHeadersAuthenticator;
	    return this;
	}

	public InjectedClassesBuilder databaseConfigurators(Map<String, DatabaseConfigurator> databaseConfigurators) {
	    this.databaseConfigurators = databaseConfigurators;
	    return this;
	}

	public InjectedClassesBuilder sqlFirewallTriggers(Map<String, SqlFirewallTrigger> sqlFirewallTriggers) {
	    this.sqlFirewallTriggers = sqlFirewallTriggers;
	    return this;
	}

	public InjectedClassesBuilder sqlFirewallManagerMap(Map<String, List<SqlFirewallManager>> sqlFirewallManagerMap) {
	    this.sqlFirewallManagerMap = sqlFirewallManagerMap;
	    return this;
	}

	public InjectedClassesBuilder blobDownloadConfigurator(BlobDownloadConfigurator blobDownloadConfigurator) {
	    this.blobDownloadConfigurator = blobDownloadConfigurator;
	    return this;
	}

	public InjectedClassesBuilder blobUploadConfigurator(BlobUploadConfigurator blobUploadConfigurator) {
	    this.blobUploadConfigurator = blobUploadConfigurator;
	    return this;
	}

	public InjectedClassesBuilder sessionConfigurator(SessionConfigurator sessionConfigurator) {
	    this.sessionConfigurator = sessionConfigurator;
	    return this;
	}

	/**
	 * Necessary, because Database Configurators are needed when building firewall...
	 * @return
	 */
	public Map<String, DatabaseConfigurator> getDatabaseConfigurators() {
	    return databaseConfigurators;
	}
	
	public InjectedClassesBuilder threadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
	    this.threadPoolExecutor = threadPoolExecutor;
	    return this;
	}
	
	public InjectedClassesBuilder updateListenerMap(Map<String, List<UpdateListener>> updateListenerMap) {
	    this.updateListenerMap = updateListenerMap;
	    return this;
	}

	// Return the finally constructed User object
	public InjectedClasses build() {
	    InjectedClasses injectedClasses = new InjectedClasses(this);
	    //validateUserObject(injectedClasses);
	    return injectedClasses;
	}

	@SuppressWarnings("unused")
	private void validateUserObject(InjectedClasses injectedClasses) {
	    // HACK NDP
	    // TODO LATER
	    // Do some basic validations to check
	    // if user object does not break any assumption of system
	}




    }

}
