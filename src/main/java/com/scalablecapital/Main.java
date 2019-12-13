package com.scalablecapital;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author sokolov
 */
@Slf4j
public class Main {

    private static final String GOOGLE_QUERY = "http://www.google.com/search?&ie=utf-8&oe=utf-8&q=";


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Optional<String> param = Arrays.stream(args).findFirst();
        if (param.isEmpty()) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Please enter a query param");
            param = Optional.of(sc.nextLine());
        }
        String userQuery = param.get();
        log.info("You have passed = {}", userQuery);

        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
        TopNStorage<String> storage = new TopNStorage<>(5);

        CompletableFuture<List<String>> href =
                downloadPage(httpClient, GOOGLE_QUERY + userQuery)
                        .thenApply(Main::parseGoogleResultPage)
                        .thenCompose(urls -> {
                            List<CompletableFuture<String>> googleResults =
                                    urls.stream()
                                            .map(url -> downloadPageAndFindLibs(httpClient, url, storage::add))
                                            .collect(Collectors.toList());

                            return CompletableFuture.allOf(googleResults.toArray(new CompletableFuture[0]))
                                    .thenApply(ignore ->
                                            googleResults.stream()
                                                    .map(CompletableFuture::join)
                                                    .collect(Collectors.toList()));
                        });

        System.out.println("Parse results: \n" + href.get());
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("Top 5 libs:\n" + storage.getTop());

    }

    private static CompletableFuture<String> downloadPage(HttpClient httpClient, String url) {
        log.info("download " + url);
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .setHeader("User-Agent", "Mozilla")
                .build();
        HttpResponse.BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString();


        return httpClient.sendAsync(request, bodyHandler)
                .thenApply(Main::extractBody);
    }

    private static List<String> parseGoogleResultPage(String body) {
        Document doc = Jsoup.parse(body);
        Elements firstPageLinks = doc.select("div.kCrYT > a[href]");
        return firstPageLinks.stream().
                map(s -> s.attr("href"))
                .map(Main::removeUrlQ)
                .map(Main::extractBeforeAmpersand)
                .map(Main::extractBeforeQuestionMark)
                .collect(Collectors.toList());
    }


    private static CompletableFuture<String> downloadPageAndFindLibs(HttpClient httpClient, String url, Consumer<String> libConsumer) {
        return downloadPage(httpClient, url)
                .thenApply(body -> url + " has " + extractLibs(body, libConsumer) + " js libs")
                .exceptionally(throwable -> {
                    log.error(throwable.getMessage());
                    return "no results for " + url + "... reason: " + throwable.getMessage();
                });

    }

    private static int extractLibs(String body, Consumer<String> libConsumer) {
        int[] result = new int[1];
        Document document = Jsoup.parse(body);
        document.select("script").stream()
                .map(script -> script.attr("src"))
                .filter(script -> !script.isEmpty())
                .map(Main::extractBeforeAmpersand)
                .map(Main::extractBeforeJsExtension)
                // not a good style but I wanted some statistic and to keep flow style
                .peek(s -> result[0]++)
                .forEach(libConsumer);
        return result[0];
    }

    private static String extractBody(HttpResponse<String> response) {
        log.info("extract body for url " + response.uri());
        if (!response.body().startsWith("<!")) {
            throw new RuntimeException("unsupported body type");
        }
        return response.body();
    }

    private static String extractBeforeAmpersand(String url) {
        if (url.contains("&")) {
            return url.substring(0, url.indexOf("&"));
        }
        return url;
    }

    private static String extractBeforeJsExtension(String libName) {
        String result = libName;
        if (libName.contains(".js") && !libName.endsWith(".js")) {
            result = libName.substring(0, libName.indexOf(".js") + 3);
        }
        return result;
    }

    private static String extractBeforeQuestionMark(String url) {
        String tmp = url;
        if (url.contains("?")) {
            tmp = url.substring(0, url.indexOf("?"));
        }
        if (url.contains("%3F")) {
            tmp = url.substring(0, url.indexOf("%3F"));
        }
        return tmp;
    }

    private static String removeUrlQ(String url) {
        return url.replace("/url?q=", "");
    }


}
