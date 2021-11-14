/**
 * 
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
import org.kawanfw.sql.api.server.session.SessionConfigurator;

/**
 * All decoded Properties into injected classes instances ready to use.
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

    /** The map of (database, List<SqlFirewallManager>) */
    private Map<String, List<SqlFirewallManager>> sqlFirewallMap = new ConcurrentHashMap<>();

    /** The BlobUploadConfigurator instance */
    private BlobUploadConfigurator blobUploadConfigurator = null;

    /** The BlobUploadConfigurator instance */
    private BlobDownloadConfigurator blobDownloadConfigurator = null;

    /** The SessionConfigurator instance */
    private SessionConfigurator sessionConfigurator = null;

    /** The executor to use */
    private ThreadPoolExecutor threadPoolExecutor = null;
    
    
    
}
