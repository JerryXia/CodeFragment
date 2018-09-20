/**
 * 
 */
package com.github.jerryxia.devutil.http;

import org.apache.http.Header;
import org.apache.http.StatusLine;

/**
 * @author Administrator
 *
 */
public final class CopiedTextHttpResponse {
    private final StatusLine statusLine;
    private final Header[] headers;
    private final String body;

    public CopiedTextHttpResponse(StatusLine statusLine, Header[] headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
