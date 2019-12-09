package com.scalablecapital;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.scalablecapital.functions.UrlExtractorFunctions.*;

@Slf4j
class PageDownloader {


    Collection<String> findMainResultsLinks(String param) throws IOException, GeneralSecurityException, ExecutionException, InterruptedException {
        String googleMainSearch = "http://www.google.com/search?&ie=utf-8&oe=utf-8&q=" + param;
        String mainPage = downloadPage(googleMainSearch).orElseThrow(() -> new RuntimeException("Cannot query the " + param));

        Document doc = Jsoup.parse(mainPage);
        //CSS qurery to select all child from kCrYt
        // I do not love the solutiuon with hard-coded div class, but could not google anything better (using google search engine is even worse)
        Elements firstPageLinks = doc.select("div.kCrYT > a[href]");
        return firstPageLinks.stream().map(s -> s.attr("href")).map(removeUrlQ).map(Objects::toString).map(this::getUrlFromHref).collect(Collectors.toList());
    }


    Optional<String> downloadPage(String url) throws IOException, GeneralSecurityException, ExecutionException, InterruptedException {
        //Closable hht client is ThreadSafe!
        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor();
        PoolingNHttpClientConnectionManager cm = new PoolingNHttpClientConnectionManager(ioReactor);
        try (CloseableHttpAsyncClient client = HttpAsyncClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier())
                .setSSLContext(sslContextFactory()).disableCookieManagement().setUserAgent("Mozilla").setConnectionManager(cm).build()) {
            final HttpGet get = new HttpGet(url);
            client.start();
            Future<HttpResponse> response = client.execute(get, null);
            HttpResponse httpResponse;
            try {
                httpResponse = response.get();
            } catch (Exception e) {
                log.error("Cannot connect to" + url);
                return Optional.empty();
            }

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String responseEntity = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                return Optional.of(responseEntity);
            }

        }
        return Optional.empty();
    }

    private String getUrlFromHref(String href) {
        return Optional.of(href)
                .map(getBeforeAmpersand)
                .map(getBeforeQuestion)
                .orElseThrow(() -> new RuntimeException("Transformation has failed"));
    }

    private static SSLContext sslContextFactory() throws GeneralSecurityException {
        TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] certificate, String authType) {
                return true;
            }
        };
        return SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy).build();
    }

}
