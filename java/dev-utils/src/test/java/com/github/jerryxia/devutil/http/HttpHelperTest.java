/**
 * 
 */
package com.github.jerryxia.devutil.http;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Consts;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Administrator
 *
 */
public class HttpHelperTest {

    @Test
    public void simpleGettNull_is_ok() {
        String url = "https://manytools.org/http-html-text/http-request-headers/";
        String responseStr = null;
        try {
            responseStr = HttpHelper.simpleGet(url, null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Assert.assertFalse(responseStr.contains("Content-Type:"));
    }

    @Test
    public void simpleFormPostNull_is_ok() {
        String url = "https://manytools.org/http-html-text/http-request-headers/";
        String responseStr = HttpHelper.simpleFormPost(url, null);
        Assert.assertTrue(responseStr.contains("Content-Type:application/x-www-form-urlencoded;"));
    }

    @Test
    public void simpleFormPostEmpty_is_ok() {
        String url = "https://manytools.org/http-html-text/http-request-headers/";
        HashMap<String, String> map = new HashMap<String, String>();
        String responseStr = HttpHelper.simpleFormPost(url, map);
        Assert.assertTrue(responseStr.contains("Content-Type:application/x-www-form-urlencoded;"));
    }

    @Test
    public void simpleFormPost_is_ok() {
        String url = "https://manytools.org/http-html-text/http-request-headers/";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("a", "a111111");
        String responseStr = HttpHelper.simpleFormPost(url, map);
        Assert.assertTrue(responseStr.contains("Content-Type:application/x-www-form-urlencoded;"));
    }

    @Test
    public void simpleJsonPost_is_ok() {
        String url = "https://manytools.org/http-html-text/http-request-headers/";
        String jsonStr = "{\"a\": 1}";
        String responseStr = HttpHelper.simpleJsonPost(url, jsonStr);
        Assert.assertTrue(responseStr.contains("Content-Type:application/json;"));
    }
    
    @Test
    public void test_formEncode_is_ok() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("b", "测试2a");
        params.put("a", "测试");
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
        String utf8Result = URLEncodedUtils.format(basicNameValuePairs, Consts.UTF_8);
        Assert.assertEquals("b=%E6%B5%8B%E8%AF%952a&a=%E6%B5%8B%E8%AF%95", utf8Result);

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(basicNameValuePairs, Consts.UTF_8);
        
        System.out.println(entity.toString());
    }
    
    
}
