package com.scalablecapital;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.scalablecapital.functions.UrlExtractorFunctions.*;

/**
 * @author sokolov
 */
@Slf4j
class GoogleSearcher {
    Collection<String> findMainResultLinks(String queryParam) throws Exception {
        String googleMainSearch = "http://www.google.com/search?&ie=utf-8&oe=utf-8&q=" + queryParam;
        PageDownloader pageDownloader = new PageDownloader();
        CloseableHttpAsyncClient client = HttpClientHolder.getInstance().getHttpAsyncClient();
        Optional<String> mainPage = Optional.of(pageDownloader.downloadPage(googleMainSearch,client).orElseThrow(() -> new RuntimeException("Cannot query the " + queryParam)));
        Document doc = Jsoup.parse(mainPage.orElseThrow(()->new RuntimeException("Cannot query")));
        //CSS qurery to select all child from kCrYt
        // I do not love the solutiuon with hard-coded div class, but could not google anything better (using google search engine is even worse)
        Elements firstPageLinks = doc.select("div.kCrYT > a[href]");
        return firstPageLinks.stream().
                map(s -> s.attr("href"))
                .map(removeUrlQ)
                .map(Objects::toString)
                .map(this::getUrlFromHref)
                .collect(Collectors.toList());
    }

    private String getUrlFromHref(String href) {
        return Optional.of(href)
                .map(getBeforeAmpersand)
                .map(getBeforeQuestion)
                .orElseThrow(() -> new RuntimeException("Transformation has failed"));
    }
}
