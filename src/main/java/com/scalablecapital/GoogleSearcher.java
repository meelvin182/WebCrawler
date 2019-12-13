package com.scalablecapital;

import com.scalablecapital.functions.UrlExtractorFunctions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author sokolov
 */
@Slf4j
@AllArgsConstructor
class GoogleSearcher {

    private static final String GOOGLE_QUERY = "http://www.google.com/search?&ie=utf-8&oe=utf-8&q=";

    private final PageDownloader pageDownloader;

    /**
     * This methods shoud query the google and get the main links for the first page
     *
     * @param queryParam which param should we query
     * @return List with links to the main pages
     */
    List<String> findMainResultLinks(String queryParam) {
        String googleMainSearch = GOOGLE_QUERY + queryParam;
        log.info("downloading ={}", googleMainSearch);
        List<String> mainPage = pageDownloader.downloadPages(Collections.singletonList(googleMainSearch));
        Document doc = Jsoup.parse(mainPage.get(0));
        //CSS query to select all child from kCrYt
        // I do not love the solution with hard-coded div class, but could not google anything better (using google search engine is even worse)
        Elements firstPageLinks = doc.select("div.kCrYT > a[href]");
        return firstPageLinks.stream()
                .map(link -> link.attr("href"))
                .map(UrlExtractorFunctions::removeUrlQ)
                .map(Objects::toString)
                .map(UrlExtractorFunctions::extractBeforeAmpersand)
                .map(UrlExtractorFunctions::extractBeforeQuestionMark)
                .collect(Collectors.toList());
    }


}
