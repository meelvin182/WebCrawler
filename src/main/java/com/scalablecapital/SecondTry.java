package com.scalablecapital;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.scalablecapital.functions.UrlExtractorFunctions.*;

/**
 * @author sokolov
 */

public class SecondTry {

    HttpClient client = HttpClient.newHttpClient();


    public void requstOnDrugs() throws ExecutionException, InterruptedException {
        String googleQuery = "http://www.google.com/search?&ie=utf-8&oe=utf-8&q=meelvin182";

/*        CompletableFuture<List<String>> toSout = client.sendAsync(
                HttpRequest.newBuilder(URI.create(googleQuery))
                        .GET().setHeader("User-Agent", "Mozilla")
                        .build(),
                HttpResponse.BodyHandlers.ofString())
                //zdes list vnizu
                .thenApply(this::parseGoogle).thenApply(
                    this::getRequestsFromListOfLinks
                )*/
        //System.out.println(toSout.join());


        CompletableFuture.supplyAsync(() -> client.sendAsync(
                HttpRequest.newBuilder(URI.create(googleQuery))
                        .GET().setHeader("User-Agent", "Mozilla")
                        .build(),
                HttpResponse.BodyHandlers.ofString()).
                thenApply(HttpResponse::body)
        ).thenCompose(
                mainResult -> {
                    List<CompletableFuture<HttpResponse<String>>> listCompletableFuture = new ArrayList<>();
                    mainResult.thenApply(this::parseGoogle)
                            .thenApply(this::getRequestsFromListOfLinks).thenApply(listCompletableFuture::addAll);
                    return CompletableFuture.allOf(listCompletableFuture.toArray(new CompletableFuture[listCompletableFuture.size()]));
                }).join();
        //System.out.println(mozilla);
    }

    private List<String> parseGoogle(String googlePage) {
        Document doc = Jsoup.parse(googlePage);
        //CSS qurery to select all child from kCrYt
        // I do not love the solutiuon with hard-coded div class, but could not google anything better (using google search engine is even worse)
        Elements firstPageLinks = doc.select("div.kCrYT > a[href]");
        return firstPageLinks.stream().
                map(s -> s.attr("href"))
                .map(removeUrlQ)
                .map(Objects::toString)
                .map(extractBeforeAmpersand)
                .map(extractBeforeQuestionMark)
                .collect(Collectors.toList());
    }

    private List<CompletableFuture<HttpResponse<String>>> getRequestsFromListOfLinks(List<String> urls) {
        return urls.stream()
                .map(url -> client.sendAsync(
                        HttpRequest.newBuilder(URI.create(url))
                                .GET().setHeader("User-Agent", "Mozilla")
                                .build(),
                        HttpResponse.BodyHandlers.ofString())).collect(Collectors.toList());
    }


}
