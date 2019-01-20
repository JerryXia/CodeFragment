/**
 * 
 */
package com.github.jerryxia.tinypng;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.FileUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jerryxia.devutil.http.HttpHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * @author Administrator
 *
 */
public class TinyPngClient {
    private static final Logger log = LoggerFactory.getLogger(TinyPngClient.class);

    public static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.80 Safari/537.36";
    public static final String DEFAULT_COOKIE     = "ga=GA1.2.1289424115.1540862296; _gid=GA1.2.838093185.1544511023; _gat=1";

    private static SSLContext sslContext = null;
    private static final Gson gson       = new GsonBuilder().disableHtmlEscaping().create();

    static {
        try {
            sslContext = SSLContextBuilder.create().loadTrustMaterial(AllTrustStrategy.INSTANCE).build();
        } catch (KeyManagementException e) {
            log.error("", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("", e);
        } catch (KeyStoreException e) {
            log.error("", e);
        }
    }

    public void min(File file, File outputFile) {
        HttpPost httpPost = new HttpPost("https://tinypng.com/web/shrink");
        httpPost.setConfig(HttpHelper.DEFAULT_REQUEST_CONFIG);
        FileEntity fileEntity = null;
        if (file.getName().endsWith(".png")) {
            fileEntity = new FileEntity(file, ContentType.IMAGE_PNG);
        } else {
            fileEntity = new FileEntity(file, ContentType.IMAGE_JPEG);
        }
        httpPost.setHeader("User-Agent", DEFAULT_USER_AGENT);
        httpPost.setHeader("Cookie", DEFAULT_COOKIE);
        httpPost.setHeader("Referer", "https://tinypng.com/");
        httpPost.setEntity(fileEntity);

        CloseableHttpClient httpclient = HttpClients.custom().setSSLContext(sslContext).build();
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpclient.execute(httpPost);
            StatusLine statusLine = httpResponse.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_CREATED) {
                HttpEntity entity = httpResponse.getEntity();
                // Content-Type: application/json
                // Location: https://tinypng.com/web/output/g95z796ntm8qbbx6rxzyp1uypj80hvh3
                log.info("httpresponse headers:");
                for(Header header : httpResponse.getAllHeaders()) {
                    log.info("{}", header);
                }
                String responseString = EntityUtils.toString(entity, Consts.UTF_8);
                try {
                    TinyPngMinResponse minResponse = gson.fromJson(responseString, TinyPngMinResponse.class);
                    if(minResponse != null) {
                        HttpGet downLoadRequest = null;
                        try {
                            downLoadRequest = HttpHelper.buildSimpleGet(minResponse.getOutput().getUrl(), null, HttpHelper.DEFAULT_REQUEST_CONFIG);
                        } catch (URISyntaxException uriEx) {
                            log.error("output Uri非法", uriEx);
                        }
                        if(downLoadRequest != null) {
                            CloseableHttpResponse downLoadHttpResponse = httpclient.execute(downLoadRequest);
                            HttpEntity downLoadEntity = downLoadHttpResponse.getEntity();
                            byte[] downLoadResponseBytes = EntityUtils.toByteArray(downLoadEntity);
                            FileUtils.writeByteArrayToFile(outputFile, downLoadResponseBytes);
                        }
                    } else {
                        log.error("解析responseString的对象为空，response: {}", responseString);
                    }
                } catch(JsonSyntaxException jsonEx) {
                    log.error("解析responseString异常，response: {}", responseString);
                }
            } else {
                log.warn("检测到未能处理的statusCode: {}", statusLine);
            }
        } catch (IOException e) {
            log.error("min() io error", e);
        } finally {
            HttpClientUtils.closeQuietly(httpResponse);
            HttpClientUtils.closeQuietly(httpclient);
        }
    }

}
