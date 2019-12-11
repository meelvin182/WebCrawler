package com.scalablecapital;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

import static com.scalablecapital.functions.UrlExtractorFunctions.*;


@Slf4j
class JsCounter {

    @Getter
    private final Map<String, LongAdder> jsMapStorage;

    private final PageDownloader pageDownloader;

    JsCounter(Map<String, LongAdder> storage, PageDownloader pageDownloader) {
        jsMapStorage = storage;
        this.pageDownloader = pageDownloader;
    }

    /**
     * This method parses the html page and stores the js libs inthe to storage
     *
     * @param links From what link we should crawl the libs
     */
    void getAndCountJsLibs(List<String> links) {
        log.info("checking link = {}", links);
        List<String> pages = pageDownloader.downloadPages(links);
        if (pages.isEmpty()) {
            log.info("noting to download for = {}", links);
            return;
        }


        for (String page : pages) {
            Document document = Jsoup.parse(page);
            List<String> scripts = document.select("script").stream().map(s -> s.attr("src")).collect(Collectors.toList());
            for (String scriptSourse : scripts) {
                Optional.of(scriptSourse).map(extractBeforeAmpersand).map(extractBeforeQuestionMark).map(s -> {
                    if (!s.isEmpty()) {
                        log.info("found {}", s);
                        jsMapStorage
                                .computeIfAbsent(Optional.of(s).
                                                map(getBeforejsExtention).
                                                orElseThrow(() -> new RuntimeException("Failed to compute"))
                                        , key -> new LongAdder()).increment();
                    }
                    return jsMapStorage;
                });
            }
        }
    }

    /**
     * Just get top5 libs
     *
     * @return
     */
    List<String> getTopFive() {
        Map<String, Integer> linkedHashMap = new LinkedHashMap<>();



        getJsMapStorage().forEach((key, val) -> linkedHashMap.put(key, val.intValue()));
        Map<String, Integer> jsCountSorted =
                linkedHashMap.entrySet().stream().sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        return jsCountSorted.entrySet().stream().limit(5).map(e -> "js='" + e.getKey() + "\t=\t" + e.getValue())
                .collect(Collectors.toList());

    }
}


