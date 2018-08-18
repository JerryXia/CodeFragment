/**
 * 
 */
package com.github.jerryxia.healthcheck.domain;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.github.jerryxia.healthcheck.util.RecordLogViewStatusMessagesServlet;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Administrator
 *
 */
@Slf4j
public class CheckTask implements Runnable {
    private static final RequestConfig REQUEST_CONFIG         = RequestConfig.custom().setConnectionRequestTimeout(1234)
            .setConnectTimeout(1234).setSocketTimeout(1234).build();
    private static final long          DEFAULT_CHECK_INTERVAL = 1000;

    private final ServerCheckManager   checkingManager;
    private final CheckingInstanceNode checkingInstanceNode;

    private boolean working = true;
    private boolean active;

    public CheckTask(final ServerCheckManager manager, final CheckingInstanceNode checkingInstanceNode) {
        this.checkingManager = manager;
        this.checkingInstanceNode = checkingInstanceNode;
        this.active = this.checkingInstanceNode.isActived();
    }

    private void checkNode(CheckingInstanceNode checkingInstanceNode) {
        boolean nowIsActive = getUrl(checkingInstanceNode);
        if (nowIsActive == this.active) {
            // ignore
        } else {
            this.active = nowIsActive;
            this.checkingInstanceNode.setActived(nowIsActive);
            this.checkingManager.receiveUpdateReport(this.checkingInstanceNode);
        }
    }

    public boolean getUrl(CheckingInstanceNode node) {
        boolean valid = false;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = null;
        try {
            String uri = String.format("http://%s:%d%s?%s=%d", node.getIp(), node.getPort(), node.getPath(),
                    node.getQueryWithTimestampParamName(), System.currentTimeMillis());
            HttpGet httpget = new HttpGet(uri);
            httpget.setHeader("User-Agent", "HealthChecker");
            if (StringUtils.isNotBlank(node.getCookie())) {
                httpget.addHeader("Cookie", node.getCookie());
            }
            httpget.setConfig(REQUEST_CONFIG);

            httpResponse = httpclient.execute(httpget);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    valid = true;
                }
            }
        } catch (ClientProtocolException e) {
            log.error("getUrl ClientProtocolException", e);
        } catch (IOException e) {
            log.info("getUrl IOException", e);
            RecordLogViewStatusMessagesServlet.info(e.getMessage(), this);
        } catch (Exception e) {
            log.error("getUrl Exception", e);
        } finally {
            HttpClientUtils.closeQuietly(httpResponse);
            HttpClientUtils.closeQuietly(httpclient);
        }
        return valid;
    }

    @Override
    public void run() {
        while (working) {
            try {
                checkNode(this.checkingInstanceNode);
                Thread.sleep(DEFAULT_CHECK_INTERVAL);
            } catch (InterruptedException e) {
                working = false;
                log.info("{} interrupted", Thread.currentThread().getName());
            }
        }
    }
}
