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
package org.kawanfw.sql.servlet.injection.classes;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
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
import org.kawanfw.sql.api.server.logging.LoggerCreator;
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

    /** The map of (database, List<SqlFirewallTrigger>) */
    private Map<String, Set<SqlFirewallTrigger>> sqlFirewallTriggerMap = new ConcurrentHashMap<>();
	
    /** The map of (database, List<SqlFirewallManager>) */
    private Map<String, Set<SqlFirewallManager>> sqlFirewallManagerMap = new ConcurrentHashMap<>();

    /** The BlobUploadConfigurator instance */
    private BlobUploadConfigurator blobUploadConfigurator = null;

    /** The BlobUploadConfigurator instance */
    private BlobDownloadConfigurator blobDownloadConfigurator = null;

    /** The SessionConfigurator instance */
    private SessionConfigurator sessionConfigurator = null;

    /** The executor to use */
    private ThreadPoolExecutor threadPoolExecutor = null;

    /** The map of (database, List<UpdateListener>) */
    private Map<String, Set<UpdateListener>> updateListenerMap = new ConcurrentHashMap<>();

    private LoggerCreator loggerCreator;
    private Set<LoggerCreator> loggerCreatorSet= new LinkedHashSet<>();
    
    private InjectedClasses(InjectedClassesBuilder injectedClassesBuilder) {
	
	this.userAuthenticator = injectedClassesBuilder.userAuthenticator;
	this.requestHeadersAuthenticator = injectedClassesBuilder.requestHeadersAuthenticator;
	this.databaseConfigurators = injectedClassesBuilder.databaseConfigurators;
	this.sqlFirewallTriggerMap = injectedClassesBuilder.sqlFirewallTriggerMap;
	
	this.sqlFirewallManagerMap = injectedClassesBuilder.sqlFirewallManagerMap;

	this.blobUploadConfigurator = injectedClassesBuilder.blobUploadConfigurator;
	this.blobDownloadConfigurator = injectedClassesBuilder.blobDownloadConfigurator;
	this.sessionConfigurator = injectedClassesBuilder.sessionConfigurator;
	this.threadPoolExecutor = injectedClassesBuilder.threadPoolExecutor;
	
	this.updateListenerMap = injectedClassesBuilder.updateListenerMap;
	this.loggerCreator = injectedClassesBuilder.loggerCreator;
	this.loggerCreatorSet = injectedClassesBuilder.loggerCreatorSet;

    }

    
    /**
     * @return the loggerCreator
     */
    public LoggerCreator getLoggerCreator() {
        return loggerCreator;
    }

   
    /**
     * @return the loggerCreatorSet
     */
    public Set<LoggerCreator> getLoggerCreatorSet() {
        return loggerCreatorSet;
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
    public Map<String, Set<SqlFirewallTrigger>> getSqlFirewallTriggerMap() {
        return sqlFirewallTriggerMap;
    }

    /**
     * @return the sqlFirewallManagerMap
     */
    public Map<String, Set<SqlFirewallManager>> getSqlFirewallManagerMap() {
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
    public Map<String, Set<UpdateListener>> getUpdateListenerMap() {
        return updateListenerMap;
    }



    public static class InjectedClassesBuilder {
	/** The UserAuthenticator instance */
	private UserAuthenticator userAuthenticator = null;

	/** RequestHeadersAuthenticator instance */
	private RequestHeadersAuthenticator requestHeadersAuthenticator;

	/** The map of (database, DatabaseConfigurator) */
	private Map<String, DatabaseConfigurator> databaseConfigurators = new ConcurrentHashMap<>();

	/** The map of (database, list<SqlFirewallTrigger>) */
	private Map<String, Set<SqlFirewallTrigger>> sqlFirewallTriggerMap = new ConcurrentHashMap<>();
	
	/** The map of (database, List<SqlFirewallManager>) */
	private Map<String, Set<SqlFirewallManager>> sqlFirewallManagerMap = new ConcurrentHashMap<>();

	/** The BlobUploadConfigurator instance */
	private BlobUploadConfigurator blobUploadConfigurator = null;

	/** The BlobUploadConfigurator instance */
	private BlobDownloadConfigurator blobDownloadConfigurator = null;

	/** The SessionConfigurator instance */
	private SessionConfigurator sessionConfigurator = null;

	/** The executor to use */
	private ThreadPoolExecutor threadPoolExecutor = null;

	/** The map of (database, List<UpdateListener>) */
	private Map<String, Set<UpdateListener>> updateListenerMap = new ConcurrentHashMap<>();

	private LoggerCreator loggerCreator = null;

	/** Use to publish a end of startup all info about Loggers used */
	private Set<LoggerCreator> loggerCreatorSet= new LinkedHashSet<>();
	
	public InjectedClassesBuilder loggerCreator(LoggerCreator loggerCreator) {
	    this.loggerCreator = loggerCreator;
	    addObjectToLoggers(loggerCreator);
	    return this;
	}
	
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

	public InjectedClassesBuilder sqlFirewallManagerMap(Map<String, Set<SqlFirewallManager>> sqlFirewallManagerMap) {
	    this.sqlFirewallManagerMap = sqlFirewallManagerMap;
	    return this;
	}
	
	public InjectedClassesBuilder sqlFirewallTriggerMap(Map<String, Set<SqlFirewallTrigger>> sqlFirewallTriggerMap) {
	    this.sqlFirewallTriggerMap = sqlFirewallTriggerMap;
	    for (Map.Entry<String, Set<SqlFirewallTrigger>> entry : sqlFirewallTriggerMap.entrySet()) {
			addSetToLoggers(entry.getValue());
	    }
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
	
	public InjectedClassesBuilder updateListenerMap(Map<String, Set<UpdateListener>> updateListenerMap) {
	    this.updateListenerMap = updateListenerMap;
	    return this;
	}

	// Return the finally constructed User object
	public InjectedClasses build() {
	    InjectedClasses injectedClasses = new InjectedClasses(this);
	    //validateUserObject(injectedClasses);
	    return injectedClasses;
	}

	/**
	 * Add to Loggers if object is a Logger
	 * @param object 
	 */
	private void addObjectToLoggers(Object object) {
	    if (object instanceof LoggerCreator) {
		loggerCreatorSet.add((LoggerCreator)object);
	    }
	}
	
	/**
	 * Add to Loggers if object  of Set is a Logger
	 * @param object 
	 */
	
	@SuppressWarnings("unused")
	private void addSetToLoggers(Set<?> set) {
	    for (Object object : set) {
		addObjectToLoggers(object);
	    }
	}
	

	@SuppressWarnings("unused")
	private void validateUserObject(InjectedClasses injectedClasses) {
	    // FUTURE USAGE
	    // Do some basic validations to check
	    // if user object does not break any assumption of system
	}






    }

}
