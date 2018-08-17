/**
 * 
 */
package com.github.jerryxia.healthcheck.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.github.jerryxia.healthcheck.model.CheckingInstanceNode;
import com.github.jerryxia.healthcheck.model.InstanceNode;
import com.github.jerryxia.healthcheck.model.ServerNode;

/**
 * @author Administrator
 *
 */
public class CheckTask implements Runnable {
    private static final Log           log            = LogFactory.getLog(CheckTask.class);
    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom().setConnectionRequestTimeout(1234)
            .setConnectTimeout(1234).setSocketTimeout(1234).build();

    private ServerNode           serverNode;
    private CheckingInstanceNode checkingInstanceNode;
    private String               lastStatusHash;

    public CheckTask(ServerNode node, InstanceNode instanceNode) {
        this.serverNode = node;
        this.checkingInstanceNode = (CheckingInstanceNode) instanceNode;
        nodeValid();
    }

    private void nodeValid() {
        if (this.serverNode == null) {
            throw new IllegalArgumentException("node is null");
        } else if (this.checkingInstanceNode == null) {
            throw new IllegalArgumentException("checkingInstanceNode is null");
        } else {
            //
        }
    }

    private void checkNode() {
        boolean active = getUrl(checkingInstanceNode);
        checkingInstanceNode.setActived(active);

        String nginxConf = generateNginxConf();
        String nowHash = DigestUtils.md5Hex(nginxConf);
        if (nowHash.equalsIgnoreCase(this.lastStatusHash)) {
            // 不变
        } else {
            String confFileName = String.format("/etc/nginx/conf.d/%s.conf", this.checkingNode.getServerName());
            try {
                FileUtils.writeStringToFile(new File(confFileName), nginxConf);
            } catch (IOException e) {
                log.error("FileUtils.writeStringToFile error", e);
            }

            log.info("execute: nginx -t" + IOUtils.LINE_SEPARATOR_UNIX);
            String nginxConfCheckResult = callShell("nginx -t");
            // nginx: the configuration file /etc/nginx/nginx.conf syntax is ok
            // nginx: configuration file /etc/nginx/nginx.conf test is successful
            if (nginxConfCheckResult != null && (nginxConfCheckResult.indexOf("syntax is ok") > -1
                    || nginxConfCheckResult.indexOf("test is successful") > -1)) {
                String nginxReload = callShell("nginx -s reload");
                log.info(IOUtils.LINE_SEPARATOR_UNIX + "nginx conf is ok, execute: nginx -s reload, result: "
                        + nginxReload + IOUtils.LINE_SEPARATOR_UNIX);
                this.lastStatusHash = nowHash;
            } else {
                log.error(IOUtils.LINE_SEPARATOR_UNIX + "nginx conf is error, result: " + IOUtils.LINE_SEPARATOR_UNIX
                        + nginxConfCheckResult);
            }
        }
    }

    public String callShell(String shellString) {
        String shellResult = null;
        Process process = null;
        try {
            // process = Runtime.getRuntime().exec(shellString);
            String[] cmdarray = { "/bin/sh", "-c", shellString };
            process = new ProcessBuilder(cmdarray).directory(null).redirectErrorStream(true).start();

            int exitValue = process.waitFor();
            if (0 == exitValue) {
                // OK
            } else {
                log.error("callShell fail, exitValue is :" + exitValue);
            }
            shellResult = IOUtils.toString(process.getInputStream(), "UTF-8");
        } catch (Exception e) {
            log.error("callShell error", e);
            shellResult = null;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return shellResult;
    }

    private String generateNginxConf() {
        StringBuffer sb = new StringBuffer();
        sb.append("# This file is auto modified by /home/deploy/healthcheck/").append(StringUtils.LF);
        String upstreamServers = this.checkingNode.getServerName().replace(".", "_") + "_servers";
        // upstream aaa_bbb_com {
        sb.append("upstream ").append(upstreamServers).append(" {").append(StringUtils.LF);
        for (ServerNode node : this.checkingNode.getNodes()) {
            // server 10.27.163.59:8083 down;
            // server 10.27.163.73:8083 weight=2;
            sb.append("    server ").append(node.getIp()).append(":").append(node.getPort()).append(" ");
            if (node.isActived()) {
                sb.append("weight=");
                sb.append(node.getWeight());
                sb.append(";");
            } else {
                sb.append("down;");
            }
            sb.append(StringUtils.LF);
        }
        sb.append("}").append(StringUtils.LF);

        sb.append("server {").append(StringUtils.LF);
        sb.append("    listen 80;").append(StringUtils.LF);
        // server_name aaa.bbb.com;
        sb.append("    server_name ").append(this.checkingNode.getServerName()).append(";").append(StringUtils.LF);
        // access_log /var/log/nginx/med.fosunholiday.com.access.log;
        // error_log /var/log/nginx/med.fosunholiday.com.error.log warn;
        sb.append("    access_log /var/log/nginx/").append(this.checkingNode.getServerName()).append(".access.log;")
                .append(StringUtils.LF);

        sb.append("    error_log /var/log/nginx/").append(this.checkingNode.getServerName()).append(".error.log warn;")
                .append(StringUtils.LF);

        sb.append("    location / {").append(StringUtils.LF);
        sb.append("        proxy_pass http://").append(upstreamServers).append(";").append(StringUtils.LF);

        sb.append("        proxy_set_header Host $host;").append(StringUtils.LF);
        sb.append("        proxy_set_header X-Real-IP $remote_addr;").append(StringUtils.LF);
        sb.append("        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;").append(StringUtils.LF);
        sb.append("    }").append(StringUtils.LF);

        sb.append("}").append(StringUtils.LF);

        sb.append("server {").append(StringUtils.LF);
        sb.append("    listen 443;").append(StringUtils.LF);
        // server_name aaa.bbb.com;
        sb.append("    server_name ").append(this.checkingNode.getServerName()).append(";").append(StringUtils.LF);
        // access_log /var/log/nginx/med.fosunholiday.com.access.log;
        // error_log /var/log/nginx/med.fosunholiday.com.error.log warn;
        sb.append("    access_log /var/log/nginx/").append(this.checkingNode.getServerName()).append(".access.log;")
                .append(StringUtils.LF);

        sb.append("    error_log /var/log/nginx/").append(this.checkingNode.getServerName()).append(".error.log warn;")
                .append(StringUtils.LF);

        sb.append("    ssl on;").append(StringUtils.LF);
        sb.append("    ssl_certificate  /etc/nginx/cert/214470612780424.pem;").append(StringUtils.LF);
        sb.append("    ssl_certificate_key  /etc/nginx/cert/214470612780424.key;").append(StringUtils.LF);
        sb.append("    ssl_session_timeout 5m;").append(StringUtils.LF);
        sb.append("    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4;")
                .append(StringUtils.LF);
        sb.append("    ssl_protocols TLSv1 TLSv1.1 TLSv1.2;").append(StringUtils.LF);
        sb.append("    ssl_prefer_server_ciphers on;").append(StringUtils.LF);

        sb.append("    location / {").append(StringUtils.LF);
        sb.append("        proxy_pass http://").append(upstreamServers).append(";").append(StringUtils.LF);

        sb.append("        proxy_set_header Host $host;").append(StringUtils.LF);
        sb.append("        proxy_set_header X-Real-IP $remote_addr;").append(StringUtils.LF);
        sb.append("        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;").append(StringUtils.LF);
        sb.append("    }").append(StringUtils.LF);

        sb.append("}");

        return sb.toString();
    }

    public boolean getUrl(ServerNode node) {
        boolean valid = false;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            String url = String.format("http://%s:%d%s?%s=%d", node.getIp(), node.getPort(), node.getPath(),
                    node.getQueryParamName(), System.currentTimeMillis());
            HttpGet httpget = new HttpGet(url);
            httpget.setConfig(REQUEST_CONFIG);
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                if (response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        valid = true;
                    }
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            log.error("getUrl ClientProtocolException", e);
        } catch (IOException e) {
            log.debug("getUrl IOException", e);
        } catch (Exception e) {
            log.error("getUrl Exception", e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("getUrl httpclient.close() IOException", e);
            }
        }
        return valid;
    }

    @Override
    public void run() {
        while (true) {
            checkNode();
        }
    }

}
