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

import com.github.jerryxia.devutil.SystemClock;
import com.github.jerryxia.devutil.http.CopiedByteHttpResponse;

/**
 * @author Administrator
 *
 */
public class ExpectedBytesResponseCallback implements FutureCallback<HttpResponse> {
    protected final long             start;
    private long                     completedDuration;
    protected CopiedByteHttpResponse copiedHttpResponse;

    public ExpectedBytesResponseCallback() {
        this.start = SystemClock.now();
    }

    public long getCompletedDuration() {
        return completedDuration;
    }

    public CopiedByteHttpResponse getCopiedHttpResponse() {
        return copiedHttpResponse;
    }

    public void completed(HttpResponse httpResponse) {
        completedDuration = SystemClock.now() - this.start;
        try {
            // class org.apache.http.message.BasicHttpResponse
            StatusLine statusLine = httpResponse.getStatusLine();
            HttpEntity entity = httpResponse.getEntity();
            byte[] responseBytes = EntityUtils.toByteArray(entity);
            copiedHttpResponse = new CopiedByteHttpResponse(statusLine, httpResponse.getAllHeaders(), responseBytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            HttpClientUtils.closeQuietly(httpResponse);
        }
    }

    public void failed(Exception e) {
        e.printStackTrace();
    }

    public void cancelled() {

    }
}