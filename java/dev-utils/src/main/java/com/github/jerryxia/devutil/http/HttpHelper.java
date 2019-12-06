/**
 * 
 */
package com.github.jerryxia.devutil.http;

import java.io.IOException;
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
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultClientConnectionReuseStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jerryxia.devutil.RuntimeVariables;

/**
 * @author Administrator
 *
 */
public final class HttpHelper {
    private static final Logger log          = LoggerFactory.getLogger(HttpHelper.class);
    private static final String DEFAULT_NAME = "HttpHelper";

    public static final int              DEFAULT_CONN_MAXPERROUTE          = 128;
    public static final int              DEFAULT_CONN_MAXTOTAL             = 1024;
    public static final ConnectionConfig DEFAULT_CONN_CONFIG               = defaultConnectionConfigBuilder().build();
    public static final int              DEFAULT_TIMEOUT_MILLISECONDS      = 20 * 1000;
    public static final RequestConfig    DEFAULT_REQUEST_CONFIG            = defaultRequestConfigBuilder().build();
    public static final String           DEFAULT_USERAGENT                 = String.format("%s:%s/%s-%s", RuntimeVariables.LIB_GROUP_ID, RuntimeVariables.LIB_ARTIFACT_ID,
            DEFAULT_NAME, RuntimeVariables.LIB_VERSION);
    public static final ContentType      APPLICATION_FORM_URLENCODED_UTF_8 = ContentType.create(URLEncodedUtils.CONTENT_TYPE, Consts.UTF_8);

    public static final Registry<ConnectionSocketFactory>  DEFAULT_SOCKET_FACTORY_REGISTRY = createSocketFactoryRegistry();
    public static final PoolingHttpClientConnectionManager DEFAULT_CONN_MANAGER            = createDefaultHttpClientConnectionManager(DEFAULT_SOCKET_FACTORY_REGISTRY,
            DEFAULT_CONN_CONFIG, DEFAULT_CONN_MAXPERROUTE, DEFAULT_CONN_MAXTOTAL);
    public static final CloseableHttpClient                DEFAULT_HTTPCLIENT              = createDefaultHttpClient(DEFAULT_CONN_MANAGER,
            DefaultClientConnectionReuseStrategy.INSTANCE, NegativeConnectionKeepAliveStrategy.INSTANCE, 20, DEFAULT_USERAGENT);

    public static void close() {
        close(DEFAULT_HTTPCLIENT);
    }

    public static void close(final CloseableHttpClient httpClient) {
        HttpClientUtils.closeQuietly(httpClient);
    }

    /**
     * <p>
     * 简单Get请求，等价于：
     * </p>
     * <p>
     * HttpGet httpGet = buildSimpleGet(uri, params, DEFAULT_REQUEST_CONFIG);
     * <p>
     * <p>
     * return simpleExecuteRequest(httpPost);
     * </p>
     * 
     * @param uri
     *            指定的Uri
     * @param params
     *            可以为null
     * @return
     * @throws URISyntaxException
     */
    public static String simpleGet(String uri, Map<String, String> params) throws URISyntaxException {
        HttpGet httpGet = buildSimpleGet(uri, params, DEFAULT_REQUEST_CONFIG);
        String responseString = simpleExecuteRequest(httpGet);
        return responseString;
    }

    /**
     * <p>
     * 表单提交请求，等价于：
     * </p>
     * <p>
     * HttpPost httpPost = buildSimpleFormPost(uri, params, DEFAULT_REQUEST_CONFIG);
     * <p>
     * <p>
     * return simpleExecuteRequest(httpPost);
     * </p>
     * 
     * @param uri
     *            指定的Uri
     * @param params
     *            可以为null
     * @return
     */
    public static String simpleFormPost(String uri, Map<String, String> params) {
        HttpPost httpPost = buildSimpleFormPost(uri, params, DEFAULT_REQUEST_CONFIG);
        String responseString = simpleExecuteRequest(httpPost);
        return responseString;
    }

    /**
     * <p>
     * json payload提交请求，等价于：
     * </p>
     * <p>
     * HttpPost httpPost = buildSimpleJsonPost(uri, jsonStr, DEFAULT_REQUEST_CONFIG);
     * <p>
     * <p>
     * return simpleExecuteRequest(httpPost);
     * </p>
     * 
     * @param uri
     *            指定的Uri
     * @param jsonStr
     *            不能为null
     * @return
     */
    public static String simpleJsonPost(String uri, String jsonStr) {
        HttpPost httpPost = buildSimpleJsonPost(uri, jsonStr, DEFAULT_REQUEST_CONFIG);
        String responseString = simpleExecuteRequest(httpPost);
        return responseString;
    }

    public static HttpGet buildSimpleGet(String uri, Map<String, String> params, RequestConfig reqConfig) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(URI.create(uri));
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

    public static HttpPost buildSimpleFormPost(String uri, Map<String, String> params, RequestConfig reqConfig) {
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
        HttpPost httpPost = new HttpPost(uri);
        UrlEncodedFormEntity encodedParamsEntity = new UrlEncodedFormEntity(basicNameValuePairs, Consts.UTF_8);
        httpPost.setEntity(encodedParamsEntity);
        httpPost.setConfig(reqConfig);
        return httpPost;
    }

    public static HttpPost buildSimpleJsonPost(String uri, String jsonStr, RequestConfig reqConfig) {
        HttpPost httpPost = new HttpPost(uri);
        StringEntity jsonStringEntity = new StringEntity(jsonStr, ContentType.APPLICATION_JSON);
        httpPost.setEntity(jsonStringEntity);
        httpPost.setConfig(reqConfig);
        return httpPost;
    }

    public static String simpleExecuteRequest(HttpUriRequest request) {
        CopiedTextHttpResponse copiedHttpResponse = expectedTextExecuteRequest(request);
        if (copiedHttpResponse == null) {
            return null;
        } else {
            return copiedHttpResponse.getBody();
        }
    }

    public static CopiedTextHttpResponse expectedTextExecuteRequest(HttpUriRequest request) {
        return expectedTextExecuteRequest(DEFAULT_HTTPCLIENT, request);
    }

    public static CopiedByteHttpResponse expectedBytesExecuteRequest(HttpUriRequest request) {
        return expectedBytesExecuteRequest(DEFAULT_HTTPCLIENT, request);
    }

    public static CopiedTextHttpResponse expectedTextExecuteRequest(CloseableHttpClient httpclient, HttpUriRequest request) {
        CopiedTextHttpResponse copiedHttpResponse = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpclient.execute(request);
            StatusLine statusLine = httpResponse.getStatusLine();
            HttpEntity entity = httpResponse.getEntity();
            String responseString = EntityUtils.toString(entity, Consts.UTF_8);
            copiedHttpResponse = new CopiedTextHttpResponse(statusLine, httpResponse.getAllHeaders(), responseString);
        } catch (IOException e) {
            if(log.isErrorEnabled()) {
                log.error("HttpHelper.expectedTextExecuteRequest() io error", e);
            }
        } finally {
            HttpClientUtils.closeQuietly(httpResponse);
        }
        return copiedHttpResponse;
    }

    public static CopiedByteHttpResponse expectedBytesExecuteRequest(CloseableHttpClient httpclient, HttpUriRequest request) {
        CopiedByteHttpResponse copiedHttpResponse = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpclient.execute(request);
            StatusLine statusLine = httpResponse.getStatusLine();
            HttpEntity entity = httpResponse.getEntity();
            byte[] responseBytes = EntityUtils.toByteArray(entity);
            copiedHttpResponse = new CopiedByteHttpResponse(statusLine, httpResponse.getAllHeaders(), responseBytes);
        } catch (IOException e) {
            if(log.isErrorEnabled()) {
                log.error("HttpHelper.expectedBytesExecuteRequest() io error", e);
            }
        } finally {
            HttpClientUtils.closeQuietly(httpResponse);
        }
        return copiedHttpResponse;
    }

    public static CloseableHttpClient createDefaultHttpClient(HttpClientConnectionManager connManager, ConnectionReuseStrategy reuseStrategy,
            ConnectionKeepAliveStrategy keepAliveStrategy, long maxIdleTimeSeconds, String userAgent) {
        CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(connManager).setConnectionManagerShared(false).setConnectionReuseStrategy(reuseStrategy)
                .setKeepAliveStrategy(keepAliveStrategy).evictExpiredConnections().evictIdleConnections(maxIdleTimeSeconds, TimeUnit.SECONDS).setUserAgent(userAgent)
                .disableCookieManagement().build();
        return httpclient;
    }

    public static PoolingHttpClientConnectionManager createDefaultHttpClientConnectionManager(Registry<ConnectionSocketFactory> socketFactoryRegistry, ConnectionConfig connConfig,
            int defaultMaxPerRoute, int maxTotal) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setDefaultConnectionConfig(connConfig);
        connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        connectionManager.setMaxTotal(maxTotal);
        return connectionManager;
    }

    private static Registry<ConnectionSocketFactory> createSocketFactoryRegistry() {
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
        SSLConnectionSocketFactory sslf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
        Registry<ConnectionSocketFactory> socketFactoryRegistry = registryBuilder.register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslf).build();
        return socketFactoryRegistry;
    }

    public static org.apache.http.config.ConnectionConfig.Builder defaultConnectionConfigBuilder() {
        return ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE).setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8);
    }

    public static org.apache.http.client.config.RequestConfig.Builder defaultRequestConfigBuilder() {
        return RequestConfig.custom().setConnectionRequestTimeout(DEFAULT_TIMEOUT_MILLISECONDS).setConnectTimeout(DEFAULT_TIMEOUT_MILLISECONDS)
                .setSocketTimeout(DEFAULT_TIMEOUT_MILLISECONDS);
    }
}
