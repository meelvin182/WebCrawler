package com.scalablecapital;

import com.sun.deploy.net.HttpResponse;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Optional;

/**
 * @author sokolov
 */
@Slf4j
public class Main {
    public static void main(String[] args) throws IOException {
        String param = "Natashka";
        Optional<String> googleRes = queryGoogle(param);
        Document doc = Jsoup.parse(googleRes.orElseThrow(() -> new RuntimeException("Query of " + param + " failed")));
        log.info("here");

    }

    private static Optional<String> queryGoogle(String param) throws IOException {
        StringBuilder urlForGetRequest = new StringBuilder("http://www.google.com/search?&ie=utf-8&oe=utf-8&q=");
        urlForGetRequest.append(param);
        CloseableHttpClient client = HttpClientBuilder.create().disableCookieManagement().build();
        final HttpGet get = new HttpGet(urlForGetRequest.toString());
        CloseableHttpResponse response = client.execute(get);
        int statusCode = response.getStatusLine().getStatusCode();
        log.debug("statusCode=" + statusCode);
        if (statusCode == HttpStatus.SC_OK) {
            String responseEntity = EntityUtils.toString(response.getEntity(), "UTF-8");
            RandomAccessFile writer = new RandomAccessFile("googleres.html", "rw");
            writer.write(responseEntity.getBytes());

            System.out.println(responseEntity);
            return Optional.of(responseEntity);
        }
//        log.debug("StatusCode is not 200, was " + statusCode + ", url = " + urlForGetRequest);
//        log.debug("responseBody " + EntityUtils.toString(response.getEntity(), "UTF-8"));
        return Optional.empty();
    }
}
