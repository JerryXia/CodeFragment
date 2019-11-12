/**
 * 
 */
package com.github.jerryxia.devutil.http;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.protocol.HttpContext;

/**
 * @author Administrator
 *
 */
public class NegativeConnectionKeepAliveStrategy extends DefaultConnectionKeepAliveStrategy {
    private static final long                               DEFAULT_KEEPALIVE_TIMEOUT_MILLISECONDS    = 30 * 1000;
    public static final long                                FOR_NGINX_KEEPALIVE_TIMEOUT_MILLISECONDS  = 75 * 1000 - 5000;
    public static final long                                FOR_TOMCAT_KEEPALIVE_TIMEOUT_MILLISECONDS = 60 * 1000 - 5000;
    public static final NegativeConnectionKeepAliveStrategy INSTANCE                                  = new NegativeConnectionKeepAliveStrategy();

    private final long keepAliveMilliseconds;

    public NegativeConnectionKeepAliveStrategy() {
        this(DEFAULT_KEEPALIVE_TIMEOUT_MILLISECONDS);
    }

    public NegativeConnectionKeepAliveStrategy(long keepAliveMilliseconds) {
        this.keepAliveMilliseconds = keepAliveMilliseconds;
    }

    @Override
    public long getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
        long keepAlive = super.getKeepAliveDuration(response, context);
        if (keepAlive == -1) {
            keepAlive = this.keepAliveMilliseconds;
        }
        return keepAlive;
    }
}
