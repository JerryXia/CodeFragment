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
public final class CopiedByteHttpResponse {
    private final StatusLine statusLine;
    private final Header[]   headers;
    private final byte[]     body;

    public CopiedByteHttpResponse(StatusLine statusLine, Header[] headers, byte[] body) {
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

    public byte[] getBody() {
        return body;
    }

}
