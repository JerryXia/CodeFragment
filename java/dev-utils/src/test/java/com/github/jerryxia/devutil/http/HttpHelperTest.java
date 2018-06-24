/**
 * 
 */
package com.github.jerryxia.devutil.http;

import java.net.URISyntaxException;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Administrator
 *
 */
public class HttpHelperTest {

    @Test
    public void simpleGettNull_is_ok() {
        String url = "http://gqkzwy.gicp.net/";
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
        String url = "http://gqkzwy.gicp.net/";
        String responseStr = HttpHelper.simpleFormPost(url, null);
        Assert.assertTrue(responseStr.contains("Content-Type:application/x-www-form-urlencoded;"));
    }

    @Test
    public void simpleFormPostEmpty_is_ok() {
        String url = "http://gqkzwy.gicp.net/";
        HashMap<String, String> map = new HashMap<String, String>();
        String responseStr = HttpHelper.simpleFormPost(url, map);
        Assert.assertTrue(responseStr.contains("Content-Type:application/x-www-form-urlencoded;"));
    }

    @Test
    public void simpleFormPost_is_ok() {
        String url = "http://gqkzwy.gicp.net/";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("a", "a111111");
        String responseStr = HttpHelper.simpleFormPost(url, map);
        Assert.assertTrue(responseStr.contains("Content-Type:application/x-www-form-urlencoded;"));
    }

    @Test
    public void simpleJsonPost_is_ok() {
        String url = "http://gqkzwy.gicp.net/";
        String jsonStr = "{\"a\": 1}";
        String responseStr = HttpHelper.simpleJsonPost(url, jsonStr);
        Assert.assertTrue(responseStr.contains("Content-Type:application/json;"));
    }
}
