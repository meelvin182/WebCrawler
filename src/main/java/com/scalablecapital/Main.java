package com.scalablecapital;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author sokolov
 */
@Slf4j
public class Main {
    public static void main(String[] args) throws IOException {
        String param = "meelvin182";
        Main main = new Main();
        Optional<String> googleRes = main.queryGoogle(param);
        String mainPage = googleRes.orElseThrow(() -> new RuntimeException("Cannot query the " + param));
        main.findMainResultsLinks(mainPage);
    }

    // href="/url?q=https://moikrug.ru/meelvin182&amp;sa=U&amp;ved=2ahUKEwjw0e65vqjmAhVHCrkGHSYDDqQQFjACegQICBAB&amp;usg=AOvVaw23jKlkibwliUAvYvY4rzqj"

    private Collection<String> findMainResultsLinks(String mainPage) {
        Document doc = Jsoup.parse(mainPage);
        //CSS qurery to select all child from kCrYt
        // I do not love the solutiuon with hard-coded div class, but could not google anything better (using google search engine is even worse)
        Elements firstPageLinks = doc.select("div.kCrYT > a[href]");
        System.out.println(firstPageLinks.toString());
        return firstPageLinks.stream().map(s -> s.attr("href")).map(removeUrlQ).map(Objects::toString).map(this::getUrlFromHref).collect(Collectors.toList());
    }


    private Optional<String> queryGoogle(String param) throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().disableCookieManagement().build();
        final HttpGet get = new HttpGet("http://www.google.com/search?&ie=utf-8&oe=utf-8&q=" + param);
        CloseableHttpResponse response = client.execute(get);
        int statusCode = response.getStatusLine().getStatusCode();
        log.debug("statusCode=" + statusCode);
        if (statusCode == HttpStatus.SC_OK) {
            String responseEntity = EntityUtils.toString(response.getEntity(), "UTF-8");
            log.info("got response from google = {}", responseEntity);
            //todo: remove (added for test purpose)
            RandomAccessFile writer = new RandomAccessFile("googleres.html", "rw");
            writer.write(responseEntity.getBytes());
            return Optional.of(responseEntity);
        }
        return Optional.empty();
    }

    private String getUrlFromHref(String href) {
        return Optional.of(href)
                .map(getBeforeAmpersand)
                .map(getBeforeQuestion)
                .orElseThrow(() -> new RuntimeException("Transformation has failed"));
    }

    private Function<String, String> removeUrlQ = url -> url.replace("/url?q=", "");

    private Function<String, String> getBeforeAmpersand = url -> {
        if (url.contains("&")) {
            return url.substring(0, url.indexOf("&"));
        }
        return url;
    };

    private Function<String, String> getBeforeQuestion = url -> {
        String tmp = url;
        if (url.contains("?")) {
            tmp = url.substring(0, url.indexOf("?"));
        }
        if (url.contains("%3F")) {
            tmp = url.substring(0, url.indexOf("%3F"));
        }
        return tmp;
    };

}
