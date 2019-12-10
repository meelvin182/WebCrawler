package com.scalablecapital;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

import static com.scalablecapital.functions.UrlExtractorFunctions.*;


@Slf4j
class JsCounter {

    @Getter
    private final ConcurrentHashMap<String, LongAdder> jsMapStorage;


    JsCounter(ConcurrentHashMap<String, LongAdder> concurrentHashMap) {
        jsMapStorage = concurrentHashMap;
    }

    /**
     * This method parses the html page and stores the js libs inthe to storage
     * @param link From what link we should crawl the libs
     * @param client Pass a client to use
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws InterruptedException
     */
    void getAndCountJsLibs(String link, CloseableHttpAsyncClient client) throws IOException, GeneralSecurityException, InterruptedException {
        log.info("checking link = {}", link);
        PageDownloader pageDownloader = new PageDownloader();

        Optional<String> page = pageDownloader.downloadPage(link,client);
        if (!page.isPresent()) {
            log.info("noting to download for = {}", link);
            return;
        }

        Document document = Jsoup.parse(page.get());
        List<String> scripts = document.select("script").stream().map(s -> s.attr("src")).collect(Collectors.toList());
        for (String scriptSourse : scripts) {
            Optional.of(scriptSourse).map(getBeforeAmpersand).map(getBeforeQuestion).map(s -> {
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

    /**
     * Just get top5 libs
     * @return
     */
    List<String> getTopFive(){
        LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<>();
        getJsMapStorage().forEach((key, val) -> linkedHashMap.put(key, val.intValue()));
        LinkedHashMap<String, Integer> jsCountSorted =
                linkedHashMap.entrySet().stream().sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return jsCountSorted.entrySet().stream().limit(5).map(e -> "js='" + e.getKey() + "\t=\t" + e.getValue())
                .collect(Collectors.toList());

    }
}


