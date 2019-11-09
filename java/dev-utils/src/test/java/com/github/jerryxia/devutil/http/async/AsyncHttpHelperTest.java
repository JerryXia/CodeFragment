/**
 * 
 */
package com.github.jerryxia.devutil.http.async;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Administrator
 *
 */
public class AsyncHttpHelperTest {
    // AsyncHttpHelper.DEFAULT_IOREACTOR.getAuditLog();

    @Test
    public void test_simpleGetWithCookie_is_ok() throws URISyntaxException, InterruptedException {
        String uri = "https://manytools.org/http-html-text/http-request-headers/";
        HashMap<String, String> params = new HashMap<String, String>();
        ExpectedTextResponseCallback callback = new ExpectedTextResponseCallback();
        HttpGet httpGet = AsyncHttpHelper.createSimpleGet(URI.create(uri), params, AsyncHttpHelper.DEFAULT_REQUEST_CONFIG);
        httpGet.addHeader("Cookie", "a=123; b=456");
        AsyncHttpHelper.DEFAULT_HTTPASYNCCLIENT.execute(httpGet, HttpClientContext.create(), callback);
        do {
            Thread.sleep(1000);
        } while(!callback.isEnd());
        Assert.assertNotNull(callback.getCopiedHttpResponse());
        Assert.assertTrue(callback.getCopiedHttpResponse().getStatusLine().getStatusCode() == 200);
        System.out.println(callback.getCopiedHttpResponse().getBody());
        Assert.assertTrue(callback.getCopiedHttpResponse().getBody().indexOf("AsyncHttpHelper-0.0.14") > -1);
        Assert.assertTrue(callback.getCopiedHttpResponse().getBody().indexOf("a=123; b=456") > -1);
    }

    @Test
    public void test_simpleGet_is_ok() throws URISyntaxException, InterruptedException {
        URI uri = URI.create("https://www.baidu.com/");
        HashMap<String, String> params = new HashMap<String, String>();
        for(int i = 0; i < 10000; i++) {
            ExpectedTextResponseCallback callback = new ExpectedTextResponseCallback();
            AsyncHttpHelper.simpleGet(uri, params, callback);
        }
        Thread.sleep(10 * 1000);
        AsyncHttpHelper.DEFAULT_CONN_MANAGER.closeExpiredConnections();
        AsyncHttpHelper.DEFAULT_CONN_MANAGER.closeIdleConnections(10, TimeUnit.SECONDS);
        Thread.sleep(10 * 1000);
        AsyncHttpHelper.close();
        Thread.sleep(10 * 1000);
    }
}
