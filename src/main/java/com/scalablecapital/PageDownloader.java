package com.scalablecapital;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


@Slf4j
@AllArgsConstructor
class PageDownloader {

    private final HttpClient httpClient;

    /**
     * This method asynchronously downloads the urls and stores theirs htmls into the list
     *
     * @param urls Which urls we should get
     * @return list of htmls for each page
     */
    List<String> downloadPages(List<String> urls) {

        log.info("downloading {}", urls);
        List<CompletableFuture<HttpResponse<String>>> httpResponsesFutures = urls.stream()
                .map(url -> httpClient.sendAsync(
                        HttpRequest.newBuilder(URI.create(url))
                                .GET()
                                .setHeader("User-Agent", "Mozilla")
                                .build(),
                        HttpResponse.BodyHandlers.ofString())
                        .handle((res, ex) -> {
                            if (ex != null) {
                                log.error("Got exception = {} for url = {}", ex.getMessage(), url);
                                return null;
                            } else return res;
                        })).collect(Collectors.toList());

        CompletableFuture<?> allFutures = CompletableFuture.allOf(
                httpResponsesFutures.toArray(new CompletableFuture[0]));
        CompletableFuture<List<HttpResponse<String>>> allPageContentsFuture =
                allFutures.thenApplyAsync(
                        v -> httpResponsesFutures.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList()));

        CompletableFuture<List<String>> countFuture = allPageContentsFuture
                .thenApplyAsync(
                        httpResponses -> httpResponses.stream()
                                .filter(Objects::nonNull)
                                .map(HttpResponse::body)
                                .filter(body -> body.startsWith("<!"))
                                .collect(Collectors.toList()));

        return countFuture.join();
    }
}
