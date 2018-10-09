/**
 * 
 */
package com.github.jerryxia.healthcheck.domain;

import java.io.IOException;
import java.util.List;

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
import com.google.common.base.Splitter;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Administrator
 *
 */
@Slf4j
public class CheckTask implements Runnable {
    private static final RequestConfig REQUEST_CONFIG         = RequestConfig.custom().setConnectionRequestTimeout(2000).setConnectTimeout(2000).setSocketTimeout(2000).build();
    private static final long          DEFAULT_CHECK_INTERVAL = 1000;
    private static final Splitter      COLON_SPLITTER         = Splitter.on(':');

    private final ServerCheckManager   checkingManager;
    private final String               groupName;
    private final CheckingInstanceNode checkingInstanceNode;

    private boolean working = true;
    private boolean active;

    public CheckTask(final ServerCheckManager manager, final String groupName, final CheckingInstanceNode checkingInstanceNode) {
        this.checkingManager = manager;
        this.groupName = groupName;
        this.checkingInstanceNode = checkingInstanceNode;
        this.active = this.checkingInstanceNode.isActived();
    }

    private void checkNode(CheckingInstanceNode checkingInstanceNode) {
        boolean nowIsActive = getUrl(checkingInstanceNode);
        if (nowIsActive == this.active) {
            // ignore
        } else {
            doubleInfo(String.format("active: %s, nowIsActive: %s", this.active, nowIsActive));
            this.active = nowIsActive;
            this.checkingInstanceNode.setActived(nowIsActive);
            this.checkingManager.receiveUpdateReport(this.groupName, this.checkingInstanceNode);
        }
    }

    public boolean getUrl(CheckingInstanceNode node) {
        boolean valid = false;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = null;
        try {
            String uri = String.format("http://%s:%d%s?%s=%d", node.getIp(), node.getPort(), node.getPath(), node.getQueryWithTimestampParamName(), System.currentTimeMillis());
            HttpGet httpget = new HttpGet(uri);
            httpget.setHeader("Host", node.getServerName());
            httpget.setHeader("User-Agent", "HealthChecker");
            if (StringUtils.isNotBlank(node.getHeader())) {
                List<String> kv = COLON_SPLITTER.splitToList(node.getHeader());
                httpget.addHeader(kv.get(0), kv.get(1));
            }
            httpget.setConfig(REQUEST_CONFIG);

            httpResponse = httpclient.execute(httpget);
            if (httpResponse.getStatusLine() != null && httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    valid = true;
                }
            }
        } catch (ClientProtocolException e) {
            log.error("getUrl ClientProtocolException", e);
        } catch (IOException e) {
            log.debug("getUrl IOException", e);
            // RecordLogViewStatusMessagesServlet.info(e.getMessage(), this);
        } catch (Exception e) {
            log.error("getUrl Exception", e);
        } finally {
            HttpClientUtils.closeQuietly(httpResponse);
            HttpClientUtils.closeQuietly(httpclient);
        }
        return valid;
    }

    private void doubleInfo(String msg) {
        log.info(msg);
        RecordLogViewStatusMessagesServlet.info(msg, this);
    }

    @Override
    public void run() {
        while (this.working) {
            try {
                checkNode(this.checkingInstanceNode);
                Thread.sleep(DEFAULT_CHECK_INTERVAL);
            } catch (InterruptedException e) {
                this.working = false;
                log.info("{} interrupted", Thread.currentThread().getName());
            }
        }
    }
}
