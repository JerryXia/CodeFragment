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

import com.github.jerryxia.devutil.SystemClock;
import com.github.jerryxia.devutil.http.CopiedTextHttpResponse;

/**
 * @author Administrator
 *
 */
public class ExpectedTextResponseCallback implements FutureCallback<HttpResponse> {
    protected final long             start;
    private long                     completedDuration;
    protected CopiedTextHttpResponse copiedHttpResponse;

    public ExpectedTextResponseCallback() {
        this.start = SystemClock.now();
    }

    public long getCompletedDuration() {
        return completedDuration;
    }

    public CopiedTextHttpResponse getCopiedHttpResponse() {
        return copiedHttpResponse;
    }

    public void completed(HttpResponse httpResponse) {
        completedDuration = SystemClock.now() - this.start;
        try {
            // class org.apache.http.message.BasicHttpResponse
            StatusLine statusLine = httpResponse.getStatusLine();
            HttpEntity entity = httpResponse.getEntity();
            String responseString = EntityUtils.toString(entity, Consts.UTF_8);
            copiedHttpResponse = new CopiedTextHttpResponse(statusLine, httpResponse.getAllHeaders(), responseString);
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