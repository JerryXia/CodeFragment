/**
 * 
 */
package com.github.jerryxia.devutil.http.async;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.CustomHttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.client.util.HttpAsyncClientUtils;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.entity.NByteArrayEntity;
import org.apache.http.nio.entity.NFileEntity;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.ssl.SSLContextBuilder;

import com.github.jerryxia.devutil.CustomNameThreadFactory;
import com.github.jerryxia.devutil.RuntimeVariables;
import com.github.jerryxia.devutil.http.AllTrustStrategy;

/**
 * @author Administrator
 *
 */
public final class AsyncHttpHelper {
    private static final String DEFAULT_NAME                            = "AsyncHttpHelper";
    private static final String DEFAULT_THREAD_FACTORY_POOL_NAME_PREFIX = DEFAULT_NAME;

    public static final int              DEFAULT_CONN_MAXPERROUTE          = 128;
    public static final int              DEFAULT_CONN_MAXTOTAL             = 1024;
    public static final ConnectionConfig DEFAULT_CONN_CONFIG               = defaultConnectionConfigBuilder().build();
    public static final int              DEFAULT_TIMEOUT_MILLISECONDS      = 2 * 1000;
    public static final RequestConfig    DEFAULT_REQUEST_CONFIG            = defaultRequestConfigBuilder().build();
    public static final String           DEFAULT_USERAGENT                 = String.format("%s:%s/%s-%s", RuntimeVariables.LIB_GROUP_ID, RuntimeVariables.LIB_ARTIFACT_ID,
            DEFAULT_NAME, RuntimeVariables.LIB_VERSION);
    public static final ContentType      APPLICATION_FORM_URLENCODED_UTF_8 = ContentType.create(URLEncodedUtils.CONTENT_TYPE, Consts.UTF_8);

    public static final Registry<SchemeIOSessionStrategy>   DEFAULT_IOSESSION_FACTORY_REGISTRY = createIOSessionFactoryRegistry();
    public static final DefaultConnectingIOReactor          DEFAULT_IOREACTOR                  = createIOReactor(IOReactorConfig.DEFAULT, DEFAULT_THREAD_FACTORY_POOL_NAME_PREFIX);
    public static final PoolingNHttpClientConnectionManager DEFAULT_CONN_MANAGER               = createDefaultNHttpClientConnectionManager(DEFAULT_IOREACTOR,
            DEFAULT_IOSESSION_FACTORY_REGISTRY, DEFAULT_CONN_CONFIG, DEFAULT_CONN_MAXPERROUTE, DEFAULT_CONN_MAXTOTAL);
    public static final CloseableHttpAsyncClient            DEFAULT_HTTPASYNCCLIENT            = createHttpAsyncClient(DEFAULT_CONN_MANAGER,
            DEFAULT_THREAD_FACTORY_POOL_NAME_PREFIX, DEFAULT_USERAGENT);

    static {
        DEFAULT_HTTPASYNCCLIENT.start();
    }

    public static void close() {
        close(DEFAULT_HTTPASYNCCLIENT);
    }

    public static void close(final CloseableHttpAsyncClient httpAsyncClient) {
        // org.apache.http.impl.nio.client.MinimalHttpAsyncClient().close();
        HttpAsyncClientUtils.closeQuietly(httpAsyncClient);
        // connectionManager.closeExpiredConnections();
        // connectionManager.closeIdleConnections(idletime, tunit);
    }

    public static Future<HttpResponse> simpleGet(final URI uri, final Map<String, String> params, final FutureCallback<HttpResponse> callback) throws URISyntaxException {
        HttpGet httpGet = createSimpleGet(uri, params, DEFAULT_REQUEST_CONFIG);
        return DEFAULT_HTTPASYNCCLIENT.execute(httpGet, HttpClientContext.create(), callback);
    }

    public static Future<HttpResponse> simpleFormPost(final URI uri, Map<String, String> params, final FutureCallback<HttpResponse> callback) throws URISyntaxException {
        HttpPost httpPost = createSimpleFormPost(uri, params, DEFAULT_REQUEST_CONFIG);
        return DEFAULT_HTTPASYNCCLIENT.execute(httpPost, HttpClientContext.create(), callback);
    }

    public static HttpGet createSimpleGet(final URI uri, final Map<String, String> params, final RequestConfig reqConfig) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(uri);
        if (params != null) {
            // uriBuilder.setParameters(basicNameValuePairs);
            Iterator<Map.Entry<String, String>> entryIterator = params.entrySet().iterator();
            while (entryIterator.hasNext()) {
                Entry<String, String> entry = entryIterator.next();
                if (entry.getValue() == null) {
                    uriBuilder.addParameter(entry.getKey(), "");
                } else {
                    uriBuilder.addParameter(entry.getKey(), entry.getValue());
                }
            }
        }
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.setConfig(reqConfig);
        return httpGet;
    }

    public static HttpPost createSimpleFormPost(final URI uri, final Map<String, String> params, final RequestConfig reqConfig) {
        ArrayList<BasicNameValuePair> basicNameValuePairs = new ArrayList<BasicNameValuePair>();
        if (params != null) {
            Iterator<Map.Entry<String, String>> entryIterator = params.entrySet().iterator();
            while (entryIterator.hasNext()) {
                Entry<String, String> entry = entryIterator.next();
                BasicNameValuePair pair = null;
                if (entry.getValue() == null) {
                    pair = new BasicNameValuePair(entry.getKey(), "");
                } else {
                    pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                }
                basicNameValuePairs.add(pair);
            }
        }
        String formData = URLEncodedUtils.format(basicNameValuePairs, Consts.UTF_8);
        final NStringEntity producer = new NStringEntity(formData, APPLICATION_FORM_URLENCODED_UTF_8);
        return createHttpPost(uri, producer, reqConfig);
    }

    public static HttpPost createSimpleJsonPost(final URI uri, final String jsonStr, final RequestConfig reqConfig) {
        final NStringEntity producer = new NStringEntity(jsonStr, ContentType.APPLICATION_JSON);
        return createHttpPost(uri, producer, reqConfig);
    }

    public static HttpPost createSimpleBytesPost(final URI uri, final byte[] b, final RequestConfig reqConfig) {
        final NByteArrayEntity producer = new NByteArrayEntity(b, ContentType.DEFAULT_BINARY);
        return createHttpPost(uri, producer, reqConfig);
    }

    public static HttpPost createSimpleFilePost(final URI uri, final File file, final RequestConfig reqConfig) {
        final NFileEntity producer = new NFileEntity(file, ContentType.MULTIPART_FORM_DATA);
        return createHttpPost(uri, producer, reqConfig);
    }

    /**
     * base create http post
     * 
     * @param uri
     * @param nonBlockingEntity
     *            support {@link org.apache.http.nio.entity.NStringEntity} and
     *            {@link org.apache.http.nio.entity.NByteArrayEntity} and {@link org.apache.http.nio.entity.NFileEntity}
     * @param reqConfig
     * @return
     */
    public static HttpPost createHttpPost(final URI uri, final HttpEntity nonBlockingEntity, final RequestConfig reqConfig) {
        final HttpPost httpPost = new HttpPost(uri);
        httpPost.setProtocolVersion(HttpVersion.HTTP_1_1);
        // org.apache.http.nio.pool.AbstractNIOConnPool line:445
        // override ioReactorconfig finally
        httpPost.setConfig(reqConfig);

        if (httpPost.getEntity() == null || httpPost.getEntity() != nonBlockingEntity) {
            httpPost.setEntity(nonBlockingEntity);
        }
        return httpPost;
    }

    public static CloseableHttpAsyncClient createHttpAsyncClient(NHttpClientConnectionManager connManager, String threadFactoryPoolNamePrefix, String userAgent) {
        CustomNameThreadFactory threadFactory = new CustomNameThreadFactory(threadFactoryPoolNamePrefix, "reactor");
        CloseableHttpAsyncClient httpclient = CustomHttpAsyncClients.createClientNegotiationMinimal(connManager, threadFactory, userAgent);
        return httpclient;
    }

    public static PoolingNHttpClientConnectionManager createDefaultNHttpClientConnectionManager(ConnectingIOReactor ioreactor,
            Registry<SchemeIOSessionStrategy> iosessionFactoryRegistry, ConnectionConfig connConfig, int defaultMaxPerRoute, int maxTotal) {
        PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(ioreactor, iosessionFactoryRegistry);
        connectionManager.setDefaultConnectionConfig(connConfig);
        connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        connectionManager.setMaxTotal(maxTotal);
        return connectionManager;
    }

    public static DefaultConnectingIOReactor createIOReactor(IOReactorConfig config, String threadFactoryPoolNamePrefix) {
        // org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor line:228
        // IOReactorConfig ioReactorconfig = IOReactorConfig.custom().build();
        CustomNameThreadFactory threadFactory = new CustomNameThreadFactory(threadFactoryPoolNamePrefix, "I/O Dispatcher");
        DefaultConnectingIOReactor ioreactor = null;
        try {
            ioreactor = new DefaultConnectingIOReactor(config, threadFactory);
        } catch (IOReactorException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return ioreactor;
    }

    public static Registry<SchemeIOSessionStrategy> createIOSessionFactoryRegistry() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContextBuilder.create().loadTrustMaterial(AllTrustStrategy.INSTANCE).build();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        SSLIOSessionStrategy ioSessionStrategy = new SSLIOSessionStrategy(sslContext, NoopHostnameVerifier.INSTANCE);
        RegistryBuilder<SchemeIOSessionStrategy> registryBuilder = RegistryBuilder.create();
        Registry<SchemeIOSessionStrategy> iosessionFactoryRegistry = registryBuilder.register("http", NoopIOSessionStrategy.INSTANCE).register("https", ioSessionStrategy).build();
        return iosessionFactoryRegistry;
    }

    public static org.apache.http.config.ConnectionConfig.Builder defaultConnectionConfigBuilder() {
        return ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE).setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8);
    }

    public static org.apache.http.client.config.RequestConfig.Builder defaultRequestConfigBuilder() {
        return RequestConfig.custom().setConnectionRequestTimeout(DEFAULT_TIMEOUT_MILLISECONDS).setConnectTimeout(DEFAULT_TIMEOUT_MILLISECONDS)
                .setSocketTimeout(DEFAULT_TIMEOUT_MILLISECONDS);
    }
}
