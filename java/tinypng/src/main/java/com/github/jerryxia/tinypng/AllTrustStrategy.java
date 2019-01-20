/**
 * 
 */
package com.github.jerryxia.tinypng;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.conn.ssl.TrustStrategy;

/**
 * @author guqk
 *
 */
public class AllTrustStrategy implements TrustStrategy {
    public static final AllTrustStrategy INSTANCE = new AllTrustStrategy();

    @Override
    public boolean isTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        return true;
    }
}