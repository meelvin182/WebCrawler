package com.scalablecapital;

import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author sokolov
 */
@Slf4j
public class Main {

    public static void main(String[] args) {
        Optional<String> param = Arrays.stream(args).findFirst();
        if (param.isEmpty()) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Please enter a query param");
            param = Optional.of(sc.nextLine());
        }
        String googleQuery = param.get();
        log.info("You have passed = {}", googleQuery);

        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();

        /*
         basically all the logic could be stored in 1 method (even 1 line)
         (CF stands for CompletableFuture)
         1) Get the CF for google query
         2) Then parse and get the main result links using supply async
         3) CF::allOf for google results
         4) CF::thenCompose with the root
         5) chain of CF::theApply to parse results
         6) ??
         7) ???
         8) PROFIT
         BUT this kind of solution is undebugable and too complex to test
         See branch secondVersion https://github.com/meelvin182/WebCrawler/tree/secondVersion
         Integration test can be written using http://www.mock-server.com/
         */

        TopNStorage<String> topNStorage = new TopNStorage<>(5);
        PageDownloader pageDownloader = new PageDownloader(client);
        GoogleSearcher googleSearcher = new GoogleSearcher(pageDownloader);
        JsCounter jsCounter = new JsCounter(topNStorage, pageDownloader);

        List<String> mainResultLinks = googleSearcher.findMainResultLinks(googleQuery);

        jsCounter.downloadAndCountJsLibs(mainResultLinks);

        jsCounter.getJsMapStorage().getTop().forEach(System.out::println);

    }


}
