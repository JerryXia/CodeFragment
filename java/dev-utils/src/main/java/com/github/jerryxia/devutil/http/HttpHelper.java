/**
 * 
 */
package com.github.jerryxia.devutil.http;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 *
 */
public final class HttpHelper {
    private static final Logger log = LoggerFactory.getLogger(HttpHelper.class);

    public static final int           DEFAULT_TIMEOUT        = 20 * 1000;
    public static final RequestConfig DEFAULT_REQUEST_CONFIG = buildDefaultRequestConfig();

    public static String simpleGet(String uri, Map<String, String> params) throws URISyntaxException {
        HttpGet httpGet = buildSimpleGet(uri, params, DEFAULT_REQUEST_CONFIG);
        String responseString = executeRequest(httpGet);
        return responseString;
    }

    public static String simpleFormPost(String uri, Map<String, String> params) {
        HttpPost httpPost = buildSimpleFormPost(uri, params, DEFAULT_REQUEST_CONFIG);
        String responseString = executeRequest(httpPost);
        return responseString;
    }

    public static String simpleJsonPost(String uri, String jsonStr) {
        HttpPost httpPost = buildSimpleJsonPost(uri, jsonStr, DEFAULT_REQUEST_CONFIG);
        String responseString = executeRequest(httpPost);
        return responseString;
    }

    public static HttpGet buildSimpleGet(String uri, Map<String, String> params, RequestConfig reqConfig) throws URISyntaxException {
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

    public static HttpPost buildSimpleFormPost(String uri, Map<String, String> params, RequestConfig reqConfig){
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

    public static String executeRequest(HttpUriRequest request) {
        String responseString = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpclient.execute(request);
            // StatusLine statusLine = httpResponse.getStatusLine();
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                responseString = EntityUtils.toString(entity, Consts.UTF_8);
            }
        } catch (IOException e) {
            log.error("HttpHelper.executeRequest() IOException", e);
        } finally {
            HttpClientUtils.closeQuietly(httpResponse);
            HttpClientUtils.closeQuietly(httpclient);
        }
        return responseString;
    }

    public static RequestConfig buildDefaultRequestConfig() {
        return RequestConfig.custom().setConnectTimeout(DEFAULT_TIMEOUT).setConnectionRequestTimeout(DEFAULT_TIMEOUT)
                .setSocketTimeout(DEFAULT_TIMEOUT).build();
    }
}
