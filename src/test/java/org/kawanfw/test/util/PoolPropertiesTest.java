/**
 *
 */
package org.kawanfw.test.util;

import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 * @author Nicolas de Pomereu
 *
 */
public class PoolPropertiesTest {

    /**
     *
     */
    public PoolPropertiesTest() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	PoolProperties p = new PoolProperties();
	p.setDriverClassName("driverClassName");
	p.setUrl("url");
	p.setUsername("username");
	p.setPassword("password");

	p.setInitialSize(10);
	p.setMaxActive(100);

	// p.setMinIdle(10);
	// p.setMaxIdle(maxIdle);
	// p.setValidationQuery("SELECT 1");
	// p.setTimeBetweenEvictionRunsMillis(30000);

	// Other possible values to set
	// p.setTestOnBorrow(true);
	// p.setTestOnReturn(false);
	p.setValidationInterval(30000);

	p.setMaxWait(10000);
	p.setMinEvictableIdleTimeMillis(30000);

	p.setLogAbandoned(true);

	// p.setRemoveAbandonedTimeout(86400);
	p.setRemoveAbandoned(true);
	p.setRemoveAbandonedTimeout(3600);
	p.setRollbackOnReturn(true);

	p.setJdbcInterceptors("ConnectionState;StatementFinalizer");
    }

}
