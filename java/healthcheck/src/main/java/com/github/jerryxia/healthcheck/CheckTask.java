/**
 * 
 */
package com.github.jerryxia.healthcheck;

import java.io.File;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * @author Administrator
 *
 */
public class CheckTask implements Runnable {

    private InstanceNode checkingNode;
    private String       lastStatusHash;

    public CheckTask(InstanceNode node) {
        this.checkingNode = node;
        nodeValid();
    }

    public void dispatchNode(InstanceNode node) {
        this.checkingNode = node;
        nodeValid();
    }

    private void nodeValid() {
        if (this.checkingNode == null) {
            throw new IllegalArgumentException("node is null");
        } else {

        }
    }

    private void checkNode() {
        for (int i = 0; i < this.checkingNode.getNodes().size(); i++) {
            ServerNode node = this.checkingNode.getNodes().get(i);
            boolean valid = getUrl(node);
            if (valid) {
                node.setActived(true);
            } else {
                node.setActived(false);
            }
        }

        String nginxConf = generateNginxConf();
        String nowHash = DigestUtils.md5Hex(nginxConf);
        if (nowHash.equalsIgnoreCase(this.lastStatusHash)) {
            // 不变
        } else {
            // nginx -t
            // nginx: the configuration file /etc/nginx/nginx.conf syntax is ok
            // nginx: configuration file /etc/nginx/nginx.conf test is successful
            String confFileName = String.format("/etc/nginx/conf.d/%s.conf", this.checkingNode.getServerName());
            try {
                FileUtils.writeStringToFile(new File(confFileName), nginxConf);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String nginxConfCheck = callShell("nginx -t");
            if (nginxConfCheck != null && (nginxConfCheck.indexOf("syntax is ok") > -1
                    || nginxConfCheck.indexOf("test is successful") > -1)) {
                //String nginxReload = callShell("nginx -s reload");
                //System.out.println(nginxReload);
                this.lastStatusHash = nowHash;
            } else {
                System.err.print("nginxConfCheck:");
                System.err.println(nginxConfCheck);
            }
        }
    }

    public String callShell(String shellString) {
        String shellResult = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(shellString);
            int exitValue = process.waitFor();
            if (0 != exitValue) {
                System.err.println("call shell failed, error code is :" + exitValue);
                shellResult = null;
            } else {
                char[] chars = IOUtils.toCharArray(process.getInputStream(), "UTF-8");
                shellResult = new String(chars);
            }
        } catch (Exception e) {
            System.err.println(e);
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
            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(1234).setConnectTimeout(1234)
                    .setSocketTimeout(1234).build();
            httpget.setConfig(requestConfig);
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
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                System.err.println(e);
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
