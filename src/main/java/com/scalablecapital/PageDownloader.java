package com.scalablecapital;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Slf4j
@AllArgsConstructor
class PageDownloader {

    private final HttpClient httpClient;

    /**
     * This method parallel and asyncronously downloads the urls and stores theirs htmls into the list
     * @param urls Which url we should get
     * @return list of htmls for each page
     */
    List<String> downloadPages(List<String> urls) {
        log.info("downloading {}", urls);
        List<CompletableFuture<HttpResponse<String>>> httpResponcesFutures = urls.stream()
                .map(url -> httpClient.sendAsync(
                        HttpRequest.newBuilder(URI.create(url))
                                .GET().setHeader("User-Agent", "Mozilla")
                                .build(),
                        HttpResponse.BodyHandlers.ofString())).collect(Collectors.toList());

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                httpResponcesFutures.toArray(new CompletableFuture[0]));

        CompletableFuture<List<HttpResponse<String>>> allPageContentsFuture =
                allFutures.thenApply(
                        v -> httpResponcesFutures.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList()));

        CompletableFuture<List<String>> countFuture = allPageContentsFuture
                .thenApply(
                        httpResponses -> httpResponses.stream()
                                .map(HttpResponse::body)
                                .collect(Collectors.toList()));


        return countFuture.join();
    }
}
