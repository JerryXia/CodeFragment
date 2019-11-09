/**
 * 
 */
package com.github.jerryxia.devutil.http.async;

import java.io.IOException;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.util.EntityUtils;

import com.github.jerryxia.devutil.http.CopiedTextHttpResponse;

/**
 * @author Administrator
 *
 */
public class ExpectedTextResponseCallback implements FutureCallback<HttpResponse> {
    private boolean                  end;
    protected CopiedTextHttpResponse copiedHttpResponse;

    public ExpectedTextResponseCallback() {
        
    }

    public void completed(HttpResponse httpResponse) {
        try {
            // class org.apache.http.message.BasicHttpResponse
            StatusLine statusLine = httpResponse.getStatusLine();
            HttpEntity entity = httpResponse.getEntity();
            // if response header parse null: default charset is utf-8
            String responseString = EntityUtils.toString(entity, Consts.UTF_8);
            copiedHttpResponse = new CopiedTextHttpResponse(statusLine, httpResponse.getAllHeaders(), responseString);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // EntityUtils.consume(entity.getEntity());
            HttpClientUtils.closeQuietly(httpResponse);
        }
        switchToEnd();
    }

    public void failed(Exception e) {
        e.printStackTrace();
        switchToEnd();
    }

    public void cancelled() {
        switchToEnd();
    }

    public boolean isEnd() {
        return end;
    }

    public CopiedTextHttpResponse getCopiedHttpResponse() {
        return copiedHttpResponse;
    }

    protected void switchToEnd() {
        this.end = true;
    }
}