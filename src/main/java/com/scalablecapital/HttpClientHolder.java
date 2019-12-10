package com.scalablecapital;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.security.GeneralSecurityException;

/**
 * @author sokolov
 */

@Slf4j
class HttpClientHolder {

    private static volatile HttpClientHolder instance;

    static HttpClientHolder getInstance() throws GeneralSecurityException, IOReactorException {
        if (instance == null) {
            synchronized (HttpClientHolder.class) {
                if (instance == null) {
                    instance = new HttpClientHolder();
                    instance.httpAsyncClient = HttpAsyncClients.custom()
                            .setSSLHostnameVerifier(new NoopHostnameVerifier())
                            .setSSLContext(sslContextFactory())
                            .disableCookieManagement()
                            .setUserAgent("Mozilla")
                            .setConnectionManager(getCm())
                            .setDefaultIOReactorConfig(getIOReactorConfig())
                            .build();
                }
            }
        }
        return instance;
    }

    private HttpClientHolder() {
    }

    @Getter
    private CloseableHttpAsyncClient httpAsyncClient;


    private static SSLContext sslContextFactory() throws GeneralSecurityException {
        TrustStrategy acceptingTrustStrategy = (certificate, authType) -> true;
        return SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy).build();
    }


    private static PoolingNHttpClientConnectionManager getCm() throws IOReactorException {
        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor();
        return new PoolingNHttpClientConnectionManager(ioReactor);
    }

    private static IOReactorConfig getIOReactorConfig() {
        return IOReactorConfig.custom()
                .setSelectInterval(100)
                .setSoTimeout(100)
                .setConnectTimeout(100)
                .build();
    }



}
