package com.scalablecapital;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;
import java.util.concurrent.Future;



@Slf4j
class PageDownloader {
    /**
     * This method just gets the hmlt page
     * @param url From what url we should crawl the libs
     * @param client Pass a client to use
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws InterruptedException
     */
    Optional<String> downloadPage(String url, CloseableHttpAsyncClient client) throws IOException, GeneralSecurityException, InterruptedException {
        //Closable http client is ThreadSafe @Contract(threading = ThreadingBehavior.SAFE)
        if (!client.isRunning()) {
            client.start();
        }
        log.info("downloading {}", url);
        final HttpGet get = new HttpGet(url);
        Future<HttpResponse> response = client.execute(get, null);
        HttpResponse httpResponse;
        try {
            httpResponse = response.get();
        } catch (Exception e) {
            log.error("Cannot connect to" + url);
            return Optional.empty();
        }
        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String responseEntity = EntityUtils.toString(httpResponse.getEntity());
            return Optional.of(responseEntity);
        }
        return Optional.empty();
    }
}
