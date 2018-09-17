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
public final class CopiedHttpResponse {
    private final StatusLine statusLine;
    private final Header[] headers;
    private final String body;

    public CopiedHttpResponse(StatusLine statusLine, Header[] headers, String body) {
        super();
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
