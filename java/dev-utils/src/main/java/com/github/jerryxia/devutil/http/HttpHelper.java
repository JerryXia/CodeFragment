/**
 * 
 */
package com.github.jerryxia.devutil.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
    private static final Logger  log             = LoggerFactory.getLogger(HttpHelper.class);
    private static final Charset UTF_8           = Charset.forName("UTF-8");
    private static final int     DEFAULT_TIMEOUT = 20 * 1000;

    public static String simpleGet(String url, HashMap<String, String> params) {
        HttpGet httpGet = null;
        if (params != null) {
            ArrayList<BasicNameValuePair> basicNameValuePairs = new ArrayList<BasicNameValuePair>();
            Iterator<Map.Entry<String, String>> entryIterator = params.entrySet().iterator();
            while (entryIterator.hasNext()) {
                Entry<String, String> entry = entryIterator.next();
                basicNameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            if (basicNameValuePairs.size() > 0) {
                UrlEncodedFormEntity encodedParams = new UrlEncodedFormEntity(basicNameValuePairs, UTF_8);
                String queryParams = null;
                try {
                    queryParams = EntityUtils.toString(encodedParams, UTF_8);
                } catch (ParseException e) {
                    log.error("EntityUtils.toString() ParseException", e);
                } catch (IOException e) {
                    log.error("EntityUtils.toString() IOException", e);
                }
                httpGet = new HttpGet(url + "?" + queryParams);
            } else {
                httpGet = new HttpGet(url);
            }
        } else {
            httpGet = new HttpGet(url);
        }

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(DEFAULT_TIMEOUT)
                .setConnectionRequestTimeout(DEFAULT_TIMEOUT).setSocketTimeout(DEFAULT_TIMEOUT).build();
        httpGet.setConfig(requestConfig);

        String resultStr = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                resultStr = EntityUtils.toString(entity, UTF_8);
            }
            response.close();
        } catch (IOException e) {
            log.error("HttpHelper.simpleGet() IOException", e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("HttpHelper.simpleGet() finally IOException", e);
            }
        }
        return resultStr;
    }

    public static String simplePost(String url, String jsonStr) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(jsonStr, UTF_8));
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(DEFAULT_TIMEOUT)
                .setConnectionRequestTimeout(DEFAULT_TIMEOUT).setSocketTimeout(DEFAULT_TIMEOUT).build();
        httpPost.setConfig(requestConfig);

        String resultStr = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                resultStr = EntityUtils.toString(entity, UTF_8);
            }
            response.close();
        } catch (IOException e) {
            log.error("HttpHelper.simplePost() IOException", e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("HttpHelper.simplePost() finally IOException", e);
            }
        }
        return resultStr;
    }

}
