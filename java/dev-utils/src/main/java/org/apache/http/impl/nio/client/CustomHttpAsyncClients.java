/**
 * 
 */
package org.apache.http.impl.nio.client;

import java.util.concurrent.ThreadFactory;

import org.apache.http.impl.client.DefaultClientConnectionReuseStrategy;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.util.Args;

import com.github.jerryxia.devutil.http.NegativeConnectionKeepAliveStrategy;

/**
 * org.apache.http.impl.nio.client.HttpAsyncClients
 * org.apache.http.impl.nio.client.MinimalHttpAsyncClientBuilder
 * 
 * @author guqk
 *
 */
public final class CustomHttpAsyncClients {
    public static CloseableHttpAsyncClient createMinimal(final NHttpClientConnectionManager connManager,
            final ThreadFactory threadFactory, final String userAgent) {
        Args.notNull(connManager, "Connection manager");
        return MinimalHttpAsyncClientBuilder.create()
                .setConnectionManager(connManager)
                .setConnectionManagerShared(false)
                .setThreadFactory(threadFactory)
                .setKeepAliveStrategy(NegativeConnectionKeepAliveStrategy.INSTANCE)
                .setUserAgent(userAgent)
                .disableCookieManagement()
                .build();
    }

    public static CloseableHttpAsyncClient createClientNegotiationMinimal(final NHttpClientConnectionManager connManager,
            final ThreadFactory threadFactory, final String userAgent) {
        Args.notNull(connManager, "Connection manager");
        return MinimalHttpAsyncClientBuilder.create()
                .setConnectionManager(connManager)
                .setConnectionManagerShared(false)
                .setThreadFactory(threadFactory)
                .setConnectionReuseStrategy(DefaultClientConnectionReuseStrategy.INSTANCE)
                .setKeepAliveStrategy(NegativeConnectionKeepAliveStrategy.INSTANCE)
                .setUserAgent(userAgent)
                .disableCookieManagement()
                .build();
    }
}
