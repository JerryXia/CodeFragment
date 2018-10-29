/**
 * 
 */
package com.github.jerryxia.healthcheck.domain;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;

import com.github.jerryxia.devutil.SystemClock;
import com.github.jerryxia.devutil.http.HttpHelper;
import com.github.jerryxia.healthcheck.common.Const;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * @author guqk
 *
 */
@Slf4j
public class SpringBootActuatorClient {
    public String getLoggers(ActuatorInstanceNode node) {
        String jsonContent = getUrl(node, "/loggers");
        return jsonContent;
    }

    public String modifyLoggerLevel(ActuatorInstanceNode node, String loggerName, String configuredLevel) {
        String jsonData = "{\"configuredLevel\": \"" + configuredLevel + "\"}";
        return postUrl(node, "/loggers/" + loggerName, jsonData);
    }

    public String getUrl(ActuatorInstanceNode node, String suffixPath) {
        String uri = String.format("http://%s:%d%s%s", node.getIp(), node.getPort(), StringUtils.stripEnd(node.getContextPath(), "/"), suffixPath);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(node.getQueryWithTimestampParamName(), String.valueOf(SystemClock.now()));
        try {
            val httpget = HttpHelper.buildSimpleGet(uri, params, HttpHelper.DEFAULT_REQUEST_CONFIG);
            httpget.setHeader(HttpHeaders.HOST, node.getServerName());
            httpget.setHeader(HttpHeaders.USER_AGENT, Const.DEFAULT_USER_AGENT);
            if (StringUtils.isNotBlank(node.getHeader())) {
                List<String> kv = Const.COLON_SPLITTER.splitToList(node.getHeader());
                httpget.addHeader(kv.get(0), kv.get(1));
            }
            String jsonContent = HttpHelper.simpleExecuteRequest(httpget);
            return jsonContent;
        } catch (URISyntaxException e) {
            log.error("uri错误", e);
            return null;
        }
    }

    public String postUrl(ActuatorInstanceNode node, String suffixPath, String jsonStr) {
        String uri = String.format("http://%s:%d%s%s", node.getIp(), node.getPort(), StringUtils.stripEnd(node.getContextPath(), "/"), suffixPath);
        val httppost = HttpHelper.buildSimpleJsonPost(uri, jsonStr, HttpHelper.DEFAULT_REQUEST_CONFIG);
        httppost.setHeader(HttpHeaders.HOST, node.getServerName());
        httppost.setHeader(HttpHeaders.USER_AGENT, Const.DEFAULT_USER_AGENT);
        if (StringUtils.isNotBlank(node.getHeader())) {
            List<String> kv = Const.COLON_SPLITTER.splitToList(node.getHeader());
            httppost.addHeader(kv.get(0), kv.get(1));
        }
        String jsonContent = HttpHelper.simpleExecuteRequest(httppost);
        return jsonContent;
    }
}
