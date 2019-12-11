package com.scalablecapital;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.http.HttpClient;
import java.util.*;
import java.util.stream.Collectors;

import static com.scalablecapital.functions.UrlExtractorFunctions.*;

/**
 * @author sokolov
 */
@Slf4j
class GoogleSearcher {

    private static final String GOOGLE_QUERY = "http://www.google.com/search?&ie=utf-8&oe=utf-8&q=";

    private final HttpClient client;

    GoogleSearcher(HttpClient client) {
        this.client = client;
    }

    /**
     * This methods shoud query the google and get the main links for the first page
     * @param queryParam which param should we query
     * @return List with links to the main pages
     */
    List<String> findMainResultLinks(String queryParam) {
        String googleMainSearch = GOOGLE_QUERY + queryParam;
        PageDownloader pageDownloader = new PageDownloader(client);
        List<String> mainPage = pageDownloader.downloadPages(Collections.singletonList(googleMainSearch));
        if (mainPage.size() != 1) {
            throw new RuntimeException("The page is not valid");
        }
/*        Document doc = Jsoup.parse(mainPage.get(0));
        //CSS qurery to select all child from kCrYt
        // I do not love the solutiuon with hard-coded div class, but could not google anything better (using google search engine is even worse)
        Elements firstPageLinks = doc.select("div.kCrYT > a[href]");
        return firstPageLinks.stream().
                map(s -> s.attr("href"))
                .map(removeUrlQ)
                .map(Objects::toString)
                .map(extractBeforeAmpersand)
                .map(extractBeforeQuestionMark)
                .collect(Collectors.toList());*/
return null;
    }

}
