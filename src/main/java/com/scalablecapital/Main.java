package com.scalablecapital;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author sokolov
 */
@Slf4j
public class Main {


    public static void main(String[] args) throws IOException, GeneralSecurityException, ExecutionException, InterruptedException {
        String param = Arrays.stream(args).findFirst().orElseThrow(() -> new RuntimeException("You have not entered any string to query"));
        log.info("You have passed = {}", param);
        PageDownloader downloader = new PageDownloader();
        Collection<String> googleMainResultLinks = downloader.findMainResultsLinks(param);
        log.info("Found main results = {}", googleMainResultLinks);
        ConcurrentHashMap<String, LongAdder> storage = new ConcurrentHashMap<>();
        final JsCounter jsCounter = new JsCounter(storage);
        googleMainResultLinks.parallelStream().forEach(link -> {
            try {
                jsCounter.getAndCount(link);
            } catch (IOException | GeneralSecurityException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });


        LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<>();

        jsCounter.getJsMapStorage().forEach((key, val) -> linkedHashMap.put(key, val.intValue()));
        LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();
        linkedHashMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
        linkedHashMap.forEach((key, value) -> System.out.println(key + "   =   " + value));
    }


}
