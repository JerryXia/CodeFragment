/**
 * 
 */
package com.github.jerryxia.devutil.http.async;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.util.EntityUtils;

import com.github.jerryxia.devutil.http.CopiedByteHttpResponse;

/**
 * @author Administrator
 *
 */
public class ExpectedBytesResponseCallback implements FutureCallback<HttpResponse> {
    private boolean                  end;
    protected CopiedByteHttpResponse copiedHttpResponse;

    public ExpectedBytesResponseCallback() {

    }

    public void completed(HttpResponse httpResponse) {
        try {
            // class org.apache.http.message.BasicHttpResponse
            StatusLine statusLine = httpResponse.getStatusLine();
            HttpEntity entity = httpResponse.getEntity();
            byte[] responseBytes = EntityUtils.toByteArray(entity);
            copiedHttpResponse = new CopiedByteHttpResponse(statusLine, httpResponse.getAllHeaders(), responseBytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // EntityUtils.consume(entity.getEntity());
            HttpClientUtils.closeQuietly(httpResponse);
        }
        switchToEnd();
    }

    public void failed(Exception e) {
        switchToEnd();
        e.printStackTrace();
    }

    public void cancelled() {
        switchToEnd();
    }

    public boolean isEnd() {
        return end;
    }

    public CopiedByteHttpResponse getCopiedHttpResponse() {
        return copiedHttpResponse;
    }

    private void switchToEnd() {
        this.end = true;
    }
}