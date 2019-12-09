package com.scalablecapital;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * @author sokolov
 */
@Slf4j
public class Main {


    public static void main(String[] args) throws IOException, GeneralSecurityException, ExecutionException, InterruptedException {
        String param = Arrays.stream(args).findFirst().orElseThrow(() -> new RuntimeException("You have not entered any string to query"));
        log.info("You have passed = {}", param);
        GoogleSearcher googleSearcher = new GoogleSearcher();
        Collection<String> googleMainResultLinks;
        try {
            googleMainResultLinks = googleSearcher.findMainResultLinks(param);
        } catch (Exception e) {
            log.error("Cannot get the main result link because or {}", e.getMessage());
            return;
        }

        log.info("Found main results = {}", googleMainResultLinks);

        ConcurrentHashMap<String, LongAdder> storage = new ConcurrentHashMap<>();
        final JsCounter jsCounter = new JsCounter(storage);
        //do not live this solution with passing httpclient to getAndCountJsLibs, but thus I can give 1 client
        try (CloseableHttpAsyncClient closeableHttpAsyncClient = HttpClientHolder.getInstance().getHttpAsyncClient()) {
            googleMainResultLinks.parallelStream().forEach(link -> {
                try {
                    jsCounter.getAndCountJsLibs(link, closeableHttpAsyncClient);
                } catch (Exception e) {
                    log.error("getAndCountJsLibs failed with error = {}", e.getMessage());
                }

            });
        }


        LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<>();
        jsCounter.getJsMapStorage().forEach((key, val) -> linkedHashMap.put(key, val.intValue()));


        LinkedHashMap<String, Integer> jsCountSorted =
                linkedHashMap.entrySet().stream().sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        jsCountSorted.entrySet().stream().limit(5).map(e -> "js name='" + e.getKey() + "' - occurence='" + e.getValue() + "'")
                .collect(Collectors.toList()).forEach(System.out::println);
    }


}
