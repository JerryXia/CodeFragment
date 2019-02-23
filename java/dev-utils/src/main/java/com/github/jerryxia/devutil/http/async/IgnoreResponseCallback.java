/**
 * 
 */
package com.github.jerryxia.devutil.http.async;

import org.apache.http.HttpResponse;

import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.concurrent.FutureCallback;

/**
 * @author Administrator
 *
 */
public class IgnoreResponseCallback implements FutureCallback<HttpResponse> {
    public IgnoreResponseCallback() {

    }

    public void completed(HttpResponse httpResponse) {
        HttpClientUtils.closeQuietly(httpResponse);
    }

    public void failed(Exception e) {
        e.printStackTrace();
    }

    public void cancelled() {

    }
}
